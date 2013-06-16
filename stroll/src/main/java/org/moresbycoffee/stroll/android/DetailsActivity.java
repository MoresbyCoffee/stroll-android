package org.moresbycoffee.stroll.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import jim.h.common.android.lib.zxing.config.ZXingLibConfig;
import jim.h.common.android.lib.zxing.integrator.IntentIntegrator;
import jim.h.common.android.lib.zxing.integrator.IntentResult;

public class DetailsActivity extends Activity {

    public static final String PLACE_ID_EXTRA = "place_id";
    private ZXingLibConfig zxingLibConfig;
    private PlacesService mPlacesService;
    private UserService mUserService;

    private TextView mStatusTextView;
    private TextView mTitleTextView;
    private Place mCurrentPlace;
    private Button mCaptureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlacesService = ((StrollApplication)getApplication()).getService(PlacesService.class);
        mUserService = ((StrollApplication)getApplication()).getService(UserService.class);
        zxingLibConfig = new ZXingLibConfig();
        zxingLibConfig.useFrontLight = true;

        setContentView(R.layout.details_screen);
        mStatusTextView = (TextView)findViewById(R.id.status);
        mTitleTextView = (TextView)findViewById(R.id.title);
        mCaptureButton = (Button)findViewById(R.id.capture_button);
        mCaptureButton.setOnClickListener(onCaptureButtonClickListener);

        mCurrentPlace = mPlacesService.getPlaceById(getIntent().getIntExtra(PLACE_ID_EXTRA, 0));
        mTitleTextView.setText(mCurrentPlace == null ? "Error" : mCurrentPlace.mTitle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mUserService.isPlaceCaptured(mCurrentPlace.mId)) {
            mStatusTextView.setText("Captured");
        } else {
            mStatusTextView.setText("Uncaptured");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode,
                        resultCode, data);
                if (scanResult == null) {
                    return;
                }
                final String result = scanResult.getContents();
                Log.i("BB", "" + result);
                String title = "";
                if (mCurrentPlace.isScannedCodeValid(result)) {
                    Log.i("BB", "Captured");
                    title = "Captured";
                    mUserService.capturePlace(mCurrentPlace.mId);
                } else {
                    Log.i("BB", "Not valid");
                    title = "Not valid code";
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(title).create().show();
                break;
            default:
        }
    }

    public static Intent createDetailsIntent(Context context, int placeId) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra(PLACE_ID_EXTRA, placeId);
        return intent;
    }

    private View.OnClickListener onCaptureButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            IntentIntegrator.initiateScan(DetailsActivity.this, zxingLibConfig);
        }
    };

}
