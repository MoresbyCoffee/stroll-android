package com.strollimo.android.view;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.zxing.config.ZXingLibConfig;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.strollimo.android.R;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.StrollimoPreferences;
import com.strollimo.android.controller.AccomplishableController;
import com.strollimo.android.controller.UserService;
import com.strollimo.android.model.Mystery;
import com.strollimo.android.model.Secret;
import com.strollimo.android.network.AmazonS3Controller;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailsActivity extends FragmentActivity {
    public static final String PLACE_ID_EXTRA = "place_id";
    private static final int TEMPORARY_TAKE_PHOTO = 15;

    private ZXingLibConfig zxingLibConfig;
    private AccomplishableController mAccomplishableController;
    private UserService mUserService;
    private StrollimoPreferences mPrefs;

    private TextView mTitleTextView;
    private Mystery mCurrentMystery;

    private ImageView mDetailsPhoto;
    private Secret mSelectedSecret;
    private ViewPager mViewPager;
    private SecretSlideAdapter mPagerAdapter;

    public interface OnSecretClickListener {
        public void onSecretClicked(Secret secret);
    }

    public static Intent createDetailsIntent(Context context, String placeId) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra(PLACE_ID_EXTRA, placeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mAccomplishableController = ((StrollimoApplication) getApplication()).getService(AccomplishableController.class);
        mUserService = ((StrollimoApplication) getApplication()).getService(UserService.class);
        mPrefs = ((StrollimoApplication) getApplication()).getService(StrollimoPreferences.class);
        zxingLibConfig = new ZXingLibConfig();
        zxingLibConfig.useFrontLight = true;

        setContentView(R.layout.details2_screen);
        mViewPager = (ViewPager)findViewById(R.id.secret_pager);
        mTitleTextView = (TextView) findViewById(R.id.title);

        mCurrentMystery = mAccomplishableController.getMysteryById(getIntent().getStringExtra(PLACE_ID_EXTRA));
        mPagerAdapter = new SecretSlideAdapter(getSupportFragmentManager(), getApplicationContext(), mCurrentMystery);
        mViewPager.setAdapter(mPagerAdapter);
        mPagerAdapter.setOnSecretClickListener(new OnSecretClickListener() {
            @Override
            public void onSecretClicked(Secret secret) {
                mSelectedSecret = secret;
                launchPickupActivity(mSelectedSecret.getId());

            }
        });

        mTitleTextView.setText(mCurrentMystery == null ? "Error" : mCurrentMystery.getName().toUpperCase());
        mDetailsPhoto = (ImageView)findViewById(R.id.detailed_photo);

        String imageUrl = StrollimoApplication.getService(AmazonS3Controller.class).getUrl(mCurrentMystery.getImgUrl());

        Glide.load(imageUrl).centerCrop().animate(android.R.anim.fade_in).placeholder(R.drawable.closed).into(mDetailsPhoto);

        mDetailsPhoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.i("BB", "resetting");
                if (motionEvent.getPointerCount() >= 3) {
                    Log.i("BB", "resetting - really");
                    mUserService.reset();
                    Intent intent = new Intent(DetailsActivity.this, MapFragment.class);
                    startActivity(intent);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPagerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode,
                        resultCode, data);
                if (scanResult == null) {
                    return;
                }
                String result = scanResult.getContents();
                handleResult(mCurrentMystery.isScannedCodeValid(result));
                break;
            case PhotoCaptureActivity.REQUEST_CODE:
                handleResult(PhotoCaptureActivity.getResult(requestCode, resultCode, data));
                break;
            case TEMPORARY_TAKE_PHOTO:
                final ProgressDialog progressDialog = ProgressDialog.show(this, "", "Uploading photo for checking...");
                progressDialog.show();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mUserService.captureSecret(mSelectedSecret);
                        mPagerAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }
                }, 2000);

            default:
        }
    }

    private void handleResult(boolean captureSuccessful) {
        if (captureSuccessful) {
            // TODO: recreate these dialogs
            mUserService.captureSecret(mSelectedSecret);
//            TreasureFoundDialog dialog = new TreasureFoundDialog(placesFound, placesCount, coinValue, levelUp, levelText);
//            dialog.show(getFragmentManager(), "dialog");
        } else {
            // TODO: recreate these dialogs
//            new TreasureNotFoundDialog().show(getFragmentManager(), "dialog");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mPrefs.isDebugModeOn()) {
            getMenuInflater().inflate(R.menu.main_options, menu);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mPrefs.isDebugModeOn()) {
            menu.findItem(R.id.use_barcode).setChecked(mPrefs.isUseBarcode());
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.use_barcode:
                boolean checked = item.isChecked();
                mPrefs.setUseBarcode(!checked);
                item.setChecked(!checked);
                return true;
            case R.id.add_secret:
                launchAddSecret();
//                mPhotoFile = takePhoto(createFilename());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void launchAddSecret() {
        Intent intent = new Intent(this, AddSecretActivity.class);
        intent.putExtra(PLACE_ID_EXTRA, mCurrentMystery.getId());
        startActivity(intent);
    }

    public void launchPickupActivity(String secretId) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, TEMPORARY_TAKE_PHOTO);
        // Temporarily disabling capture modes
//        if (mPrefs.isUseBarcode()) {
//            IntentIntegrator integrator = new IntentIntegrator(this);
//            integrator.initiateScan();
//        } else {
//            PhotoCaptureActivity.initiatePhotoCapture(this, secretId);
//        }
    }

    private String createFilename() {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "pic_big_1" + timeStamp + "_";
        return imageFileName;
    }

}
