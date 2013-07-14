package com.strollimo.android.view;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.zxing.config.ZXingLibConfig;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.strollimo.android.model.Mission;
import com.strollimo.android.controller.PlacesController;
import com.strollimo.android.R;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.StrollimoPreferences;
import com.strollimo.android.controller.UserService;
import com.strollimo.android.view.dialog.TreasureFoundDialog;
import com.strollimo.android.view.dialog.TreasureNotFoundDialog;

public class DetailsActivity extends Activity {
    public static final String PLACE_ID_EXTRA = "place_id";
    private ZXingLibConfig zxingLibConfig;
    private PlacesController mPlacesController;
    private UserService mUserService;
    private StrollimoPreferences mPrefs;

    private TextView mStatusTextView;
    private TextView mTitleTextView;
    private Mission mCurrentMission;
    private Button mCaptureButton;
    private ImageView mStatusImageView;
    private View.OnClickListener onCaptureButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            launchPickupActivity();
        }
    };

    private ImageView mDetailsPhoto;

    public static Intent createDetailsIntent(Context context, int placeId) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra(PLACE_ID_EXTRA, placeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        mPlacesController = ((StrollimoApplication) getApplication()).getService(PlacesController.class);
        mUserService = ((StrollimoApplication) getApplication()).getService(UserService.class);
        mPrefs = ((StrollimoApplication) getApplication()).getService(StrollimoPreferences.class);
        zxingLibConfig = new ZXingLibConfig();
        zxingLibConfig.useFrontLight = true;

        setContentView(R.layout.details_screen);
        mStatusTextView = (TextView) findViewById(R.id.status);
        mStatusImageView = (ImageView) findViewById(R.id.status_icon);
        mTitleTextView = (TextView) findViewById(R.id.title);
        mCaptureButton = (Button) findViewById(R.id.capture_button);
        mCaptureButton.setOnClickListener(onCaptureButtonClickListener);

        mCurrentMission = mPlacesController.getPlaceById(getIntent().getIntExtra(PLACE_ID_EXTRA, 0));
        mTitleTextView.setText(mCurrentMission == null ? "Error" : mCurrentMission.getTitle().toUpperCase());
        mDetailsPhoto = (ImageView)findViewById(R.id.detailed_photo);
        mDetailsPhoto.setImageBitmap(mCurrentMission.getBitmap());
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
        if (mUserService.isPlaceCaptured(mCurrentMission.getId())) {
            mStatusTextView.setText("Opened");
            mStatusImageView.setImageDrawable(getResources().getDrawable(R.drawable.open_padlock));
            mCaptureButton.setVisibility(View.GONE);

        } else {
            mStatusTextView.setText("Closed");
            mStatusImageView.setImageDrawable(getResources().getDrawable(R.drawable.closed_padlock));
            mCaptureButton.setVisibility(View.VISIBLE);
        }
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
                handleResult(mCurrentMission.isScannedCodeValid(result));
                break;
            case PhotoCaptureActivity.REQUEST_CODE:
                handleResult(PhotoCaptureActivity.getResult(requestCode, resultCode, data));
                break;
            default:
        }
    }

    private void handleResult(boolean captureSuccessful) {
        if (captureSuccessful) {
            boolean levelUp = mUserService.capturePlace(mCurrentMission);
            int placesFound = mUserService.getFoundPlacesNum();
            int placesCount = mPlacesController.getPlacesCount();
            int coinValue = mCurrentMission.getCoinValue();
            String levelText = levelUp ? mUserService.getCurrentLevel() : mUserService.getNextLevel();
            TreasureFoundDialog dialog = new TreasureFoundDialog(placesFound, placesCount, coinValue, levelUp, levelText);
            dialog.show(getFragmentManager(), "dialog");
        } else {
            new TreasureNotFoundDialog().show(getFragmentManager(), "dialog");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_options, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.use_barcode).setChecked(mPrefs.isUseBarcode());
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void launchPickupActivity() {
        if (mPrefs.isUseBarcode()) {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.initiateScan();
        } else {
            PhotoCaptureActivity.initiatePhotoCapture(this, mCurrentMission.getId());
        }
    }

}