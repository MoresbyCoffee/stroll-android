package com.strollimo.android.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import com.novoda.imageloader.core.ImageManager;
import com.novoda.imageloader.core.model.ImageTag;
import com.novoda.imageloader.core.model.ImageTagFactory;
import com.strollimo.android.R;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.controller.PhotoUploadController;
import com.strollimo.android.controller.PlacesController;
import com.strollimo.android.model.Mystery;
import com.strollimo.android.model.Secret;
import com.strollimo.android.network.AmazonUrl;
import com.strollimo.android.util.BitmapUtils;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class AddSecretActivity extends Activity {
    public static final int PHOTO_REQUEST_CODE = 51;

    private EditText mIdEditText;
    private EditText mNameEditText;
    private EditText mShortDescEditText;
    private Mystery mCurrentMystery;
    private PlacesController mPlacesController;
    private ImageView mPhotoImageView;
    private File mPhotoFile;
    private ImageManager mImageManager;
    private PhotoUploadController mPhotoUploadController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoUploadController = StrollimoApplication.getService(PhotoUploadController.class);
        mImageManager = StrollimoApplication.getService(ImageManager.class);
        setContentView(R.layout.add_secret_activity);
        mPlacesController = StrollimoApplication.getService(PlacesController.class);
        mCurrentMystery = getSelectedPlace();

        mIdEditText = (EditText) findViewById(R.id.id_edit_text);
        mNameEditText = (EditText) findViewById(R.id.name_edit_text);
        mShortDescEditText = (EditText) findViewById(R.id.short_desc_edit_text);

        mIdEditText.setText(Long.toString(Math.abs(new Random().nextLong())));
        mNameEditText.setText("test title");
        mShortDescEditText.setText("test desc");
        mPhotoImageView = (ImageView)findViewById(R.id.photo_holder);
    }

    private Mystery getSelectedPlace() {
        String placeId = getIntent().getStringExtra(Details2Activity.PLACE_ID_EXTRA);
        if (placeId != "") {
            return mPlacesController.getPlaceById(placeId);
        } else {
            return null;
        }
    }

    public void replaceImageClicked(View view) {
        mPhotoFile = takePhoto(mIdEditText.getText().toString());
    }

    public void addClicked(View view) {
        String id = mIdEditText.getText().toString();
        String name = mNameEditText.getText().toString();
        Secret secret = new Secret(id, name);
        secret.setShortDesc(mShortDescEditText.getText().toString());
        AmazonUrl amazonUrl = new AmazonUrl("strollimo1", id + ".jpeg");
        secret.setImageUrl(amazonUrl.getUrl());
        Bitmap photo = ((BitmapDrawable) mPhotoImageView.getDrawable()).getBitmap();
        mImageManager.getCacheManager().put(secret.getImageUrl(), photo);
        mPlacesController.addSecret(secret, mCurrentMystery);
        mPhotoUploadController.uploadPhotoToAmazon(amazonUrl, photo);
        finish();
    }

    private File takePhoto(String filename) {
        try {
            File storageDir = getExternalCacheDir();
            File imageFile = null;
            imageFile = File.createTempFile(
                    filename,
                    ".jpeg",
                    storageDir
            );
            Log.i("BB", imageFile.getAbsolutePath());
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
            startActivityForResult(takePictureIntent, PHOTO_REQUEST_CODE);
            return imageFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            mPhotoFile = null;
            return;
        }
        switch (requestCode) {
            case PHOTO_REQUEST_CODE:
                Bitmap b = BitmapUtils.getBitmapFromFile(mPhotoFile, 800, 600);
                mImageManager.getCacheManager().put(mPhotoFile.getAbsolutePath(), b);
                ImageTagFactory imageTagFactory = ImageTagFactory.newInstance(mPhotoImageView.getWidth(), mPhotoImageView.getHeight(), R.drawable.closed);
                ImageTag tag = imageTagFactory.build(mPhotoFile.getAbsolutePath(), this);
                mPhotoImageView.setTag(tag);
                mImageManager.getLoader().load(mPhotoImageView);
                mPhotoImageView.setImageBitmap(b);
                mPhotoFile = null;
                break;
            default:
        }
    }

}
