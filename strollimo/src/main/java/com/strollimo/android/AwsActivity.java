package com.strollimo.android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import com.strollimo.android.controller.AmazonS3Controller;
import com.strollimo.android.util.BitmapUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AwsActivity extends Activity {
    private static final String TAG = AwsActivity.class.getSimpleName();

    public static final int REQUEST_CODE = 1;
    public static Drawable drawable = null;
    private AmazonS3Controller mAmazonController = StrollimoApplication.getService(AmazonS3Controller.class);
    private File mImage;
    private ImageView mImageView;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aws_layout);
        mImageView = (ImageView) findViewById(R.id.image_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.aws, menu);
        return true;
    }

    public void pickPhoto(View view) {
        try {
            File storageDir = getExternalCacheDir();
            String timeStamp =
                    new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "pic_big_1" + timeStamp + "_";
            File image = null;
            image = File.createTempFile(
                    imageFileName,
                    ".jpeg",
                    storageDir
            );
            mImage = image;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
            takePictureIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 100000);
            startActivityForResult(takePictureIntent, REQUEST_CODE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadPhoto(View view) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                mAmazonController.uploadFile("strollimo1", mImage);
            }
        }).start();
    }

    public void downloadPhoto(View view) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                try {
                    bitmap = mAmazonController.downloadImage("strollimo1", "pic1.png");
                } catch (IOException ex) {
                    Log.e(TAG, "Error downloading from amazon", ex);
                    return;
                }
                final Bitmap fillBitmap = bitmap;
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        mImageView.setImageBitmap(fillBitmap);
                    }
                });


            }
        };
        thread.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap myBitmap = null;
        try {
            myBitmap = BitmapUtils.saveResizedBitmap(mImage, 800, 600);
        } catch (IOException ex) {
            Log.e(TAG, "Can't resize image", ex);
        }
        mImageView.setImageBitmap(myBitmap);
    }

}
