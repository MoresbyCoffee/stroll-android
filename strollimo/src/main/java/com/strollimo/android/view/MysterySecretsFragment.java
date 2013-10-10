package com.strollimo.android.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.config.ZXingLibConfig;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.strollimo.android.R;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.StrollimoPreferences;
import com.strollimo.android.controller.*;
import com.strollimo.android.controller.ImageUploader.ImageUploadTask;
import com.strollimo.android.controller.ImageUploader.ImageUploadTaskQueue;
import com.strollimo.android.event.ImageUploadSuccessEvent;
import com.strollimo.android.model.BaseAccomplishable;
import com.strollimo.android.model.Mystery;
import com.strollimo.android.model.Secret;
import com.strollimo.android.network.AmazonS3Controller;
import com.strollimo.android.network.AmazonUrl;
import com.strollimo.android.network.StrollimoApi;
import com.strollimo.android.network.response.PickupSecretResponse;
import com.strollimo.android.util.Analytics;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MysterySecretsFragment extends Fragment {
    public static final String TAG = MysterySecretsFragment.class.getSimpleName();

    private static final int TEMPORARY_TAKE_PHOTO = 15;

//    private ZXingLibConfig zxingLibConfig;
    private AccomplishableController mAccomplishableController;
    private UserService mUserService;
    private StrollimoPreferences mPrefs;
    //private Bus mBus;

    private Mystery mCurrentMystery;
    private Secret mSelectedSecret;
    private ViewPager mViewPager;
    private SecretSlideAdapter mPagerAdapter;

    public MysterySecretsFragment(Mystery mystery) {
        mCurrentMystery = mystery;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.details_screen, container, false);

        //mBus = StrollimoApplication.getService(Bus.class);
        mAccomplishableController = StrollimoApplication.getService(AccomplishableController.class);
        mUserService = StrollimoApplication.getService(UserService.class);
        mPrefs = StrollimoApplication.getService(StrollimoPreferences.class);
//        zxingLibConfig = new ZXingLibConfig();
//        zxingLibConfig.useFrontLight = true;

        mViewPager = (ViewPager) rootView.findViewById(R.id.secret_pager);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        mPagerAdapter = new SecretSlideAdapter(getActivity().getSupportFragmentManager(), getActivity().getApplicationContext(), mCurrentMystery);
        mViewPager.setAdapter(mPagerAdapter);
        mPagerAdapter.setOnSecretClickListener(new OnSecretClickListener() {
            @Override
            public void onSecretClicked(Secret secret) {
                mSelectedSecret = secret;
                Analytics.track(Analytics.Event.OPEN_CAPTURE);

                launchPickupActivity(mSelectedSecret.getId());

            }
        });


        CirclePageIndicator indicator = (CirclePageIndicator) rootView.findViewById(R.id.page_indicator);
        indicator.setViewPager(mViewPager);
        indicator.setSnap(true);

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int page) {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("page", String.valueOf(page));
                Analytics.track(Analytics.Event.SWIPE_SECRET, params);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //mBus.register(this);
        mPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        //mBus.unregister(this);
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
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

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                final AmazonUrl pickupPhotoUrl = AmazonUrl.createPickupPhotoUrl(mSelectedSecret.getId(), mPrefs.getDeviceUUID());
                String imageUrl = StrollimoApplication.getService(AmazonS3Controller.class).getUrl(pickupPhotoUrl.getUrl());
                VolleyImageLoader.getInstance().putBitmapIntoCache(imageUrl, bitmap);
                String cachedUrl = imageUrl;
                if (cachedUrl.contains("amazon")) {
                    cachedUrl = cachedUrl.substring(0, cachedUrl.indexOf('?'));
                }
                VolleyRequestQueue.getInstance().getCache().remove(cachedUrl);

                ImageUploadTaskQueue imageUploadTaskQueue = StrollimoApplication.getService(ImageUploadTaskQueue.class);
                imageUploadTaskQueue.add(new ImageUploadTask(mSelectedSecret.getId(), pickupPhotoUrl, bitmap));


                mUserService.captureSecret(mSelectedSecret);
                mSelectedSecret.setPickupState(BaseAccomplishable.PickupState.PENDING);
                getActivity().startService(new Intent(getActivity(), SecretStatusPollingService.class));
                mAccomplishableController.saveAllData();
                mPagerAdapter.notifyDataSetChanged();

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



    public void launchPickupActivity(String secretId) {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, TEMPORARY_TAKE_PHOTO);
        } catch (Exception e) {
            Log.e(TAG, "LunchPickupActivity error " + e.toString());
        }
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

    public interface OnSecretClickListener {
        public void onSecretClicked(Secret secret);
    }

    public PagerAdapter getPagerAdapter() {
        return mPagerAdapter;
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }


}
