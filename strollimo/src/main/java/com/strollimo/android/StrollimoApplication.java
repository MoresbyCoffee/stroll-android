package com.strollimo.android;

import android.app.Application;
import android.content.Context;
import com.crittercism.app.Crittercism;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.novoda.imageloader.core.ImageManager;
import com.novoda.imageloader.core.LoaderSettings;
import com.novoda.imageloader.core.cache.LruBitmapCache;
import com.strollimo.android.controller.PhotoUploadController;
import com.strollimo.android.controller.AccomplishableController;
import com.strollimo.android.controller.UserService;
import com.strollimo.android.model.PickupMode;
import com.strollimo.android.model.PickupModeTypeAdapter;
import com.strollimo.android.network.AmazonNetworkManager;
import com.strollimo.android.network.AmazonS3Controller;
import com.strollimo.android.network.StrollimoApi;

public class StrollimoApplication extends Application {
    private static Context mContext;
    private AccomplishableController mAccomplishableController;
    private UserService mUserService;
    private StrollimoPreferences mPrefs;
    private AmazonS3Controller mAmazonS3Controller;
    private ImageManager mImageManager;
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
        Crittercism.init(getApplicationContext(), "51f80ad3558d6a58c1000002");
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(PickupMode.class, new PickupModeTypeAdapter());
        builder.excludeFieldsWithoutExposeAnnotation();
        mGson = builder.create();

        mPrefs = new StrollimoPreferences(this, getSharedPreferences("StrollimoPreferences", 0), mGson);
        mStrollimoApi = new StrollimoApi(mGson, mPrefs);
        mAmazonS3Controller = new AmazonS3Controller();
        LoaderSettings settings = new LoaderSettings.SettingsBuilder().withCacheManager(new LruBitmapCache(this, 50))
                .withDisconnectOnEveryCall(true).build(this);
        settings.setNetworkManager(new AmazonNetworkManager(settings));
        mPhotoUploadController = new PhotoUploadController(this, mAmazonS3Controller);
        mImageManager = new ImageManager(this, settings);
        mAccomplishableController = new AccomplishableController(this, mPrefs, mImageManager, mPhotoUploadController);
        mUserService = new UserService(mPrefs);
        mUserService.loadPlaces();
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
        } else if (serviceClass == ImageManager.class) {
            return (T) mImageManager;
        } else if (serviceClass == PhotoUploadController.class) {
            return (T) mPhotoUploadController;
        } else if (serviceClass == StrollimoApi.class) {
            return (T) mStrollimoApi;
        }
        return null;

    }
}
