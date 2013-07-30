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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.zxing.config.ZXingLibConfig;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.novoda.imageloader.core.ImageManager;
import com.novoda.imageloader.core.model.ImageTag;
import com.novoda.imageloader.core.model.ImageTagFactory;
import com.strollimo.android.R;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.StrollimoPreferences;
import com.strollimo.android.controller.PlacesController;
import com.strollimo.android.controller.UserService;
import com.strollimo.android.model.Mystery;
import com.strollimo.android.model.Secret;
import com.strollimo.android.view.dialog.TreasureFoundDialog;
import com.strollimo.android.view.dialog.TreasureNotFoundDialog;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailsActivity extends Activity {
    public static final String PLACE_ID_EXTRA = "place_id";

    private ZXingLibConfig zxingLibConfig;
    private PlacesController mPlacesController;
    private UserService mUserService;
    private StrollimoPreferences mPrefs;
    private ImageManager mImageManager;

    private TextView mTitleTextView;
    private Mystery mCurrentMystery;

    private ImageView mDetailsPhoto;
    private ListView mCaptureListView;
    private SecretListAdapter mSecretListAdapter;

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

        mImageManager = StrollimoApplication.getService(ImageManager.class);
        mPlacesController = ((StrollimoApplication) getApplication()).getService(PlacesController.class);
        mUserService = ((StrollimoApplication) getApplication()).getService(UserService.class);
        mPrefs = ((StrollimoApplication) getApplication()).getService(StrollimoPreferences.class);
        zxingLibConfig = new ZXingLibConfig();
        zxingLibConfig.useFrontLight = true;

        setContentView(R.layout.details2_screen);
        mCaptureListView = (ListView)findViewById(R.id.capture_list);
        mTitleTextView = (TextView) findViewById(R.id.title);

        mCurrentMystery = mPlacesController.getMysteryById(getIntent().getStringExtra(PLACE_ID_EXTRA));
        mSecretListAdapter = new SecretListAdapter(this, mCurrentMystery);
        mCaptureListView.setAdapter(mSecretListAdapter);
        mCaptureListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Secret secret = mSecretListAdapter.getItem(i);
                launchPickupActivity(secret.getId());
            }
        });

        mTitleTextView.setText(mCurrentMystery == null ? "Error" : mCurrentMystery.getName().toUpperCase());
        mDetailsPhoto = (ImageView)findViewById(R.id.detailed_photo);

        ImageTagFactory imageTagFactory = ImageTagFactory.newInstance(800, 600, R.drawable.closed);
        imageTagFactory.setAnimation(android.R.anim.fade_in);
        ImageTag tag = imageTagFactory.build(mCurrentMystery.getImgUrl(), this);
        mDetailsPhoto.setTag(tag);
        mImageManager.getLoader().load(mDetailsPhoto);

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
        mSecretListAdapter.notifyDataSetChanged();
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
            default:
        }
    }

    private void handleResult(boolean captureSuccessful) {
        if (captureSuccessful) {
            boolean levelUp = mUserService.capturePlace(mCurrentMystery);
            int placesFound = mUserService.getFoundPlacesNum();
            int placesCount = mPlacesController.getMysteriesCount();
            int coinValue = mCurrentMystery.getCoinValue();
            String levelText = levelUp ? mUserService.getCurrentLevel() : mUserService.getNextLevel();
            TreasureFoundDialog dialog = new TreasureFoundDialog(placesFound, placesCount, coinValue, levelUp, levelText);
            dialog.show(getFragmentManager(), "dialog");
        } else {
            new TreasureNotFoundDialog().show(getFragmentManager(), "dialog");
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
        if (mPrefs.isUseBarcode()) {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.initiateScan();
        } else {
            PhotoCaptureActivity.initiatePhotoCapture(this, secretId);
        }
    }

    private String createFilename() {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "pic_big_1" + timeStamp + "_";
        return imageFileName;
    }

}
