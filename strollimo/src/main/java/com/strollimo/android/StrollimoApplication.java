package com.strollimo.android;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Log;
import com.crittercism.app.Crittercism;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.otto.Bus;
import com.strollimo.android.core.AccomplishableController;
import com.strollimo.android.core.ImageUploadTaskQueueController;
import com.strollimo.android.core.PreferencesController;
import com.strollimo.android.utils.gson.adapters.BitmapTypeAdapter;
import com.strollimo.android.core.PhotoUploadController;
import com.strollimo.android.core.UserController;
import com.strollimo.android.services.SecretStatusPollingService;
import com.strollimo.android.models.PickupMode;
import com.strollimo.android.utils.gson.adapters.PickupModeTypeAdapter;
import com.strollimo.android.core.AmazonS3Controller;
import com.strollimo.android.core.EndpointsController;

public class StrollimoApplication extends Application {
    public static String TAG = StrollimoApplication.class.getSimpleName();

    private static Context mContext;
    private static StrollimoApplication mInstance;

    private AccomplishableController mAccomplishableController;
    private UserController mUserController;
    private PreferencesController mPrefs;
    private AmazonS3Controller mAmazonS3Controller;
    private PhotoUploadController mPhotoUploadController;
    private Gson mGson;
    private EndpointsController mEndpointsController;
    private ImageUploadTaskQueueController mImageUploadTaskQueueController;
    private Bus mBus;



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
        mPrefs = new PreferencesController(this, PreferenceManager.getDefaultSharedPreferences(this), mGson);
        mBus = new Bus();
        mEndpointsController = new EndpointsController(mGson, mPrefs);
        mAmazonS3Controller = new AmazonS3Controller();
        mPhotoUploadController = new PhotoUploadController(this, mAmazonS3Controller);
        mAccomplishableController = new AccomplishableController(this, mPrefs, mPhotoUploadController, mEndpointsController);
        mUserController = new UserController(mPrefs);
        mUserController.loadCapturedSecrets();
        mAccomplishableController.preloadPlaces();
        mImageUploadTaskQueueController = ImageUploadTaskQueueController.create(this, new GsonBuilder().registerTypeAdapter(Bitmap.class, new BitmapTypeAdapter()).create()); // start upload service if there were images to upload since the last time
        startService(new Intent(this, SecretStatusPollingService.class));

        Log.i(TAG, String.format("Device UUID: %s", mPrefs.getDeviceUUID()));
    }

    public <T> T getServiceInstance(Class<T> serviceClass) {
        if (serviceClass == AccomplishableController.class) {
            return (T) mAccomplishableController;
        } else if (serviceClass == UserController.class) {
            return (T) mUserController;
        } else if (serviceClass == PreferencesController.class) {
            return (T) mPrefs;
        } else if (serviceClass == AmazonS3Controller.class) {
            return (T) mAmazonS3Controller;
        } else if (serviceClass == PhotoUploadController.class) {
            return (T) mPhotoUploadController;
        } else if (serviceClass == EndpointsController.class) {
            return (T) mEndpointsController;
        } else if (serviceClass == ImageUploadTaskQueueController.class) {
            return (T) mImageUploadTaskQueueController;
        } else if (serviceClass == Bus.class) {
            return (T) mBus;
        }
        return null;

    }


    public static Context getContext() {
        return StrollimoApplication.mContext;
    }

    public static StrollimoApplication getInstance() {
        return mInstance;
    }
}
