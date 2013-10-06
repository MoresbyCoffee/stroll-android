package com.strollimo.android.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.strollimo.android.R;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.StrollimoPreferences;
import com.strollimo.android.controller.AccomplishableController;
import com.strollimo.android.controller.PhotoUploadController;
import com.strollimo.android.model.Mystery;
import com.strollimo.android.model.Secret;
import com.strollimo.android.network.AmazonUrl;
import com.strollimo.android.util.BitmapUtils;

import java.io.File;
import java.util.Random;

public class AddSecretActivity extends AbstractTrackedActivity {
    public static final int REQUEST_PICK_IMAGE = 52;
    StrollimoPreferences mPrefs;
    private EditText mIdEditText;
    private EditText mNameEditText;
    private EditText mShortDescEditText;
    private Mystery mCurrentMystery;
    private AccomplishableController mAccomplishableController;
    private ImageView mPhotoImageView;
    private PhotoUploadController mPhotoUploadController;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoUploadController = StrollimoApplication.getService(PhotoUploadController.class);
        mPrefs = StrollimoApplication.getService(StrollimoPreferences.class);
        setContentView(R.layout.add_secret_activity);
        mAccomplishableController = StrollimoApplication.getService(AccomplishableController.class);
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
        String placeId = getIntent().getStringExtra(MysteryOpenActivity.PLACE_ID_EXTRA);
        if (placeId != "") {
            return mAccomplishableController.getMysteryById(placeId);
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
                File file = new File(BitmapUtils.getRealPathFromURI(this, imageUri));
                Bitmap bitmap = BitmapUtils.getBitmapFromFile(file, 800, 600);
                mPhotoImageView.setImageBitmap(bitmap);
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
        AmazonUrl amazonUrl = AmazonUrl.createSecretUrl(id, mCurrentMystery.getId());

        Secret secret = new Secret(id, name);
        secret.setShortDesc(mShortDescEditText.getText().toString());
        secret.setImgUrl(amazonUrl.getUrl());
        secret.addEnvTag(mPrefs.getEnvTag());

        Bitmap photo = ((BitmapDrawable) mPhotoImageView.getDrawable()).getBitmap();

        progressDialog = ProgressDialog.show(this, "", "Uploading secret...");
        mAccomplishableController.asynUploadSecret(secret, mCurrentMystery, photo, new AccomplishableController.OperationCallback() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                finish();
            }

            @Override
            public void onError(String errorMsg) {
                progressDialog.dismiss();
                Toast.makeText(AddSecretActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                finish();
            }
        });


    }
}
