package com.strollimo.android;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.crittercism.app.Crittercism;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.strollimo.android.controller.*;
import com.strollimo.android.model.PickupMode;
import com.strollimo.android.model.PickupModeTypeAdapter;
import com.strollimo.android.network.AmazonS3Controller;
import com.strollimo.android.network.StrollimoApi;
import com.strollimo.android.util.Utils;

public class StrollimoApplication extends Application {
    public static String TAG = StrollimoApplication.class.getSimpleName();

    public static String FLURRY_DEBUG_KEY = "23F35RQ9T33RMY5X3FH6";
    public static String FLURRY_PRODUCTION_KEY = "X9YHVP5TX29M8GSQWSJ3";

    private static Context mContext;
    private static StrollimoApplication mInstance;

    private AccomplishableController mAccomplishableController;
    private UserService mUserService;
    private StrollimoPreferences mPrefs;
    private AmazonS3Controller mAmazonS3Controller;
    private PhotoUploadController mPhotoUploadController;
    private Gson mGson;
    private StrollimoApi mStrollimoApi;
    private VolleyRequestQueue mVolleyRequestQueue;

    private MixpanelAPI mMixpanel;

    public static <T> T getService(Class<T> serviceClass) {
        return ((StrollimoApplication) mContext.getApplicationContext()).getServiceInstance(serviceClass);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mInstance = this;
        // Initialize Crittercism crash reporting only on release builds
        if (!BuildConfig.DEBUG) {
            Crittercism.init(getApplicationContext(), "51f80ad3558d6a58c1000002");
        }

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(PickupMode.class, new PickupModeTypeAdapter());
        builder.excludeFieldsWithoutExposeAnnotation();
        mGson = builder.create();

        mPrefs = new StrollimoPreferences(this, getSharedPreferences("StrollimoPreferences", 0), mGson);
        mStrollimoApi = new StrollimoApi(mGson, mPrefs);
        mAmazonS3Controller = new AmazonS3Controller();
        mPhotoUploadController = new PhotoUploadController(this, mAmazonS3Controller);
        mAccomplishableController = new AccomplishableController(this, mPrefs, mPhotoUploadController, mStrollimoApi);
        mUserService = new UserService(mPrefs);
        mUserService.loadCapturedSecrets();
        mAccomplishableController.preloadPlaces();
        startService(new Intent(this, SecretStatusPollingService.class));

        Log.i(TAG, String.format("Device UUID: %s", mPrefs.getDeviceUUID()));
    }

    public <T> T getServiceInstance(Class<T> serviceClass) {
        if (serviceClass == AccomplishableController.class) {
            return (T) mAccomplishableController;
        } else if (serviceClass == UserService.class) {
            return (T) mUserService;
        } else if (serviceClass == StrollimoPreferences.class) {
            return (T) mPrefs;
        } else if (serviceClass == AmazonS3Controller.class) {
            return (T) mAmazonS3Controller;
        } else if (serviceClass == PhotoUploadController.class) {
            return (T) mPhotoUploadController;
        } else if (serviceClass == StrollimoApi.class) {
            return (T) mStrollimoApi;
        }
        return null;

    }


    public static Context getContext() {
        return StrollimoApplication.mContext;
    }

    public static StrollimoApplication getInstance() {
        return mInstance;
    }

    public static MixpanelAPI getMixpanel() {
        if (mInstance.mMixpanel == null) {
            final String token;
            if (Utils.isDebugBuild()) {
                token = "dc79ca458e2252c03f31e8a86907e427";
            } else {
                token = "eeb6606cdbeb71f2774b1abad3e540d0";
            }
            mInstance.mMixpanel = MixpanelAPI.getInstance(mInstance, token);
        }
        return mInstance.mMixpanel;
    }

    public void flushMixpanel() {
        if (mMixpanel != null) {
            mMixpanel.flush();
        }
    }
}
