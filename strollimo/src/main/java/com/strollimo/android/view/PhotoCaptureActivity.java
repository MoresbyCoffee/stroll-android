package com.strollimo.android.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.novoda.imageloader.core.ImageManager;
import com.novoda.imageloader.core.model.ImageTag;
import com.novoda.imageloader.core.model.ImageTagFactory;
import com.strollimo.android.R;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.controller.PlacesController;
import com.strollimo.android.model.Secret;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.util.Random;

public class PhotoCaptureActivity extends Activity {
    public static final int REQUEST_CODE = 1;
    public static final String PHOTO_CAPTURE_RESULT = "PHOTO_CAPTURE_RESULT";
    public static final String PLACE_ID_EXTRA = "place_id";
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private Button mCaptureButton;
    private CameraBridgeViewBase mOpenCvCameraView;
    private ImageView mRefImageView;
    private PlacesController mPlacesController;
    private Secret mSelectedSecret;
    private boolean mCameraOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_capture);
        mCameraOn = false;
        mPlacesController = ((StrollimoApplication)getApplication()).getService(PlacesController.class);
        mSelectedSecret = getSelectedPlace();
        if (mSelectedSecret == null) {
            // TODO: error handling, should send handled exception to crittercism
        }

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_native_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mRefImageView = (ImageView)findViewById(R.id.ref_image);

        ImageManager imageManager = StrollimoApplication.getService(ImageManager.class);
        ImageTagFactory imageTagFactory = ImageTagFactory.newInstance(WIDTH, HEIGHT, R.drawable.closed);
        ImageTag tag = imageTagFactory.build(mSelectedSecret.getImageUrl(), this);
        mRefImageView.setTag(tag);
        imageManager.getLoader().load(mRefImageView);

        mRefImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOn = !mCameraOn;
                switchMode(mCameraOn);
            }
        });
        mCaptureButton = (Button)findViewById(R.id.photo_capture_button);
        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOpenCvCameraView.disableView();
                displayMockProgress();
            }
        });
    }

    private void displayMockProgress() {
        final ProgressDialog myProgressDialog = ProgressDialog.show(this, null, "Comparing images...", true);
        (new Handler()).postDelayed(new Runnable() {

            @Override
            public void run() {
                myProgressDialog.dismiss();
                sendResult();
            }
        }, 3000);
    }

    private void sendResult() {
        Intent result = getIntent();
        result.putExtra(PHOTO_CAPTURE_RESULT, getRandomResult());
        setResult(RESULT_OK, result);
        finish();
    }

    private Secret getSelectedPlace() {
        String secretId = getIntent().getStringExtra(PLACE_ID_EXTRA);
        return mPlacesController.getSecretById(secretId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    private boolean getRandomResult() {
        int result = new Random().nextInt(100);
        return result < 90;
    }

    public static void initiatePhotoCapture(Activity activity, String secretId) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, PhotoCaptureActivity.class);
        intent.putExtra(PLACE_ID_EXTRA, secretId);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    public static boolean getResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            return data.getBooleanExtra(PHOTO_CAPTURE_RESULT, false);
        } else {
            return false;
        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    switchMode(false);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    private void switchMode(boolean cameraOn) {
        if (cameraOn) {
            mOpenCvCameraView.enableView();
            mRefImageView.setAlpha(0.3f);
        } else {
            mOpenCvCameraView.disableView();
            mRefImageView.setAlpha(1f);
        }
    }
}
