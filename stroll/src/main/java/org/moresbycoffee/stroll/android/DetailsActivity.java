package org.moresbycoffee.stroll.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
    private ImageView mStatusImageView;
    private View.OnClickListener onCaptureButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            IntentIntegrator.initiateScan(DetailsActivity.this, zxingLibConfig);
        }
    };

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
        mPlacesService = ((StrollApplication) getApplication()).getService(PlacesService.class);
        mUserService = ((StrollApplication) getApplication()).getService(UserService.class);
        zxingLibConfig = new ZXingLibConfig();
        zxingLibConfig.useFrontLight = true;

        setContentView(R.layout.details_screen);
        mStatusTextView = (TextView) findViewById(R.id.status);
        mStatusImageView = (ImageView) findViewById(R.id.status_icon);
        mTitleTextView = (TextView) findViewById(R.id.title);
        mCaptureButton = (Button) findViewById(R.id.capture_button);
        mCaptureButton.setOnClickListener(onCaptureButtonClickListener);

        mCurrentPlace = mPlacesService.getPlaceById(getIntent().getIntExtra(PLACE_ID_EXTRA, 0));
        mTitleTextView.setText(mCurrentPlace == null ? "Error" : mCurrentPlace.mTitle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mUserService.isPlaceCaptured(mCurrentPlace.mId)) {
            mStatusTextView.setText("Opened");
            mStatusImageView.setImageDrawable(getResources().getDrawable(R.drawable.opened));

        } else {
            mStatusTextView.setText("Closed");
            mStatusImageView.setImageDrawable(getResources().getDrawable(R.drawable.closed));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, StrollMapActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
