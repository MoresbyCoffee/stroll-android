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

import com.strollimo.android.model.Place;
import com.strollimo.android.controller.PlacesController;
import com.strollimo.android.R;
import com.strollimo.android.StrollimoApplication;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.util.Random;

public class PhotoCaptureActivity extends Activity {
    public static final int REQUEST_CODE = 1;
    public static final String PHOTO_CAPTURE_RESULT = "PHOTO_CAPTURE_RESULT";
    public static final String PLACE_ID_EXTRA = "place_id";

    private Button mCaptureButton;
    private CameraBridgeViewBase mOpenCvCameraView;
    private ImageView mRefImageView;
    private PlacesController mPlacesController;
    private Place mSelectedPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_capture);
        mPlacesController = ((StrollimoApplication)getApplication()).getService(PlacesController.class);
        mSelectedPlace = getSelectedPlace();
        if (mSelectedPlace == null) {
            // TODO: error handling, should send handled exception to crittercism
        }

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_native_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mRefImageView = (ImageView)findViewById(R.id.ref_image);
        mRefImageView.setImageDrawable(mSelectedPlace.getImage());
        mRefImageView.setAlpha(0.3f);
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

    private Place getSelectedPlace() {
        int placeId = getIntent().getIntExtra(PLACE_ID_EXTRA, -1);
        if (placeId >= 0) {
            return mPlacesController.getPlaceById(placeId);
        } else {
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    private boolean getRandomResult() {
        return new Random().nextBoolean();
    }

    public static void initiatePhotoCapture(Activity activity, int placeId) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, PhotoCaptureActivity.class);
        intent.putExtra(PLACE_ID_EXTRA, placeId);
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
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
}
