package com.strollimo.android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AwsActivity extends Activity {
    public static final int REQUEST_CODE = 1;
    private final static String AWSAccessKeyId = "AKIAIO5MQ5HJN3NINXVQ";
    private final static String AWSSecretKey = "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";
    public static Drawable drawable = null;
    private Bitmap mImageBitmap;
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

    File mImage;
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
                AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials(AWSAccessKeyId, AWSSecretKey));
                PutObjectRequest por = new PutObjectRequest("strollimo1", mImage.getName(), mImage);
                s3Client.putObject(por);
            }
        }).start();
    }

    public void downloadPhoto(View view) {
        AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials(AWSAccessKeyId, AWSSecretKey));
        ResponseHeaderOverrides override = new ResponseHeaderOverrides();
        override.setContentType("image/jpeg");
        GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest("strollimo1", "pic1.png");
        urlRequest.setExpiration(new Date(System.currentTimeMillis() + 3600000));  // Added an hour's worth of milliseconds to the current time.
        urlRequest.setResponseHeaders(override);
        URL url = s3Client.generatePresignedUrl(urlRequest);
        getImageFromURL(url.toString());
    }

    public void getImageFromURL(final String urlString) {

        Thread thread = new Thread() {
            @Override
            public void run() {
                //TODO : set imageView to a "pending" image
                InputStream is = null;
                try {
                    URLConnection urlConn = new URL(urlString).openConnection();

                    is = urlConn.getInputStream();
                } catch (Exception ex) {
                }
                drawable = Drawable.createFromStream(is, "src");
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        mImageView.setImageDrawable(drawable);
                    }
                });


            }
        };
        thread.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap myBitmap = BitmapFactory.decodeFile(mImage.getAbsolutePath());
        mImageView.setImageBitmap(myBitmap);
    }

}
