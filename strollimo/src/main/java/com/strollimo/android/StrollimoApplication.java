package com.strollimo.android;

import android.app.Application;
import android.content.Context;
import com.crittercism.app.Crittercism;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.strollimo.android.controller.PhotoUploadController;
import com.strollimo.android.controller.AccomplishableController;
import com.strollimo.android.controller.UserService;
import com.strollimo.android.model.PickupMode;
import com.strollimo.android.model.PickupModeTypeAdapter;
import com.strollimo.android.network.AmazonS3Controller;
import com.strollimo.android.network.StrollimoApi;

public class StrollimoApplication extends Application {
    private static Context mContext;
    private AccomplishableController mAccomplishableController;
    private UserService mUserService;
    private StrollimoPreferences mPrefs;
    private AmazonS3Controller mAmazonS3Controller;
    private PhotoUploadController mPhotoUploadController;
    private Gson mGson;
    private StrollimoApi mStrollimoApi;

    public static <T> T getService(Class<T> serviceClass) {
        return ((StrollimoApplication) mContext.getApplicationContext()).getServiceInstance(serviceClass);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
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
}
