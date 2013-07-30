package com.strollimo.android.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import com.novoda.imageloader.core.ImageManager;
import com.strollimo.android.R;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.controller.PhotoUploadController;
import com.strollimo.android.controller.PlacesController;
import com.strollimo.android.model.Mystery;
import com.strollimo.android.model.Secret;
import com.strollimo.android.network.AmazonUrl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

public class AddSecretActivity extends Activity {
    public static final int REQUEST_PICK_IMAGE = 52;
    private EditText mIdEditText;
    private EditText mNameEditText;
    private EditText mShortDescEditText;
    private Mystery mCurrentMystery;
    private PlacesController mPlacesController;
    private ImageView mPhotoImageView;
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
        mPhotoImageView = (ImageView) findViewById(R.id.photo_holder);
    }

    private Mystery getSelectedPlace() {
        String placeId = getIntent().getStringExtra(DetailsActivity.PLACE_ID_EXTRA);
        if (placeId != "") {
            return mPlacesController.getMysteryById(placeId);
        } else {
            return null;
        }
    }

    public void replaceImageClicked(View view) {
        Intent pickImageIntent = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImageIntent, REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_PICK_IMAGE:
                Uri imageUri = data.getData();
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), imageUri);
                    mPhotoImageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                break;
            default:
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_secret, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_secret) {
            addClicked();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void addClicked() {
        String id = mIdEditText.getText().toString();
        String name = mNameEditText.getText().toString();
        Secret secret = new Secret(id, name);
        secret.setShortDesc(mShortDescEditText.getText().toString());
        AmazonUrl amazonUrl = new AmazonUrl("strollimo1", mCurrentMystery.getId(), id + ".jpeg");
        secret.setImgUrl(amazonUrl.getUrl());
        Bitmap photo = ((BitmapDrawable) mPhotoImageView.getDrawable()).getBitmap();
        mImageManager.getCacheManager().put(secret.getImgUrl(), photo);
        mPlacesController.addSecret(secret, mCurrentMystery);
        mPlacesController.saveAllData();
        mPhotoUploadController.asyncUploadPhotoToAmazon(amazonUrl, photo, null);
        finish();
    }
}
