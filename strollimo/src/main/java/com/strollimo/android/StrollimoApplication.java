package com.strollimo.android;

import android.app.Application;
import android.content.Context;
import com.crittercism.app.Crittercism;
import com.novoda.imageloader.core.ImageManager;
import com.novoda.imageloader.core.LoaderSettings;
import com.novoda.imageloader.core.cache.LruBitmapCache;
import com.squareup.picasso.Picasso;
import com.strollimo.android.controller.PhotoUploadController;
import com.strollimo.android.controller.PlacesController;
import com.strollimo.android.controller.UserService;
import com.strollimo.android.network.AmazonDownloader;
import com.strollimo.android.network.AmazonNetworkManager;
import com.strollimo.android.network.AmazonS3Controller;

public class StrollimoApplication extends Application {
    private static Context mContext;
    private PlacesController mPlacesController;
    private UserService mUserService;
    private StrollimoPreferences mPrefs;
    private AmazonS3Controller mAmazonS3Controller;
    private ImageManager mImageManager;
    private PhotoUploadController mPhotoUploadController;
    private Picasso mPicasso;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Crittercism.init(getApplicationContext(), "51f80ad3558d6a58c1000002");
        mPrefs = new StrollimoPreferences(getSharedPreferences("StrollimoPreferences", 0), this);
        mAmazonS3Controller = new AmazonS3Controller();
        LoaderSettings settings = new LoaderSettings.SettingsBuilder().withCacheManager(new LruBitmapCache(this, 50))
                .withDisconnectOnEveryCall(true).build(this);
        settings.setNetworkManager(new AmazonNetworkManager(settings));
        mPhotoUploadController = new PhotoUploadController(this, mAmazonS3Controller);
        mImageManager = new ImageManager(this, settings);

        AmazonDownloader downloader = new AmazonDownloader(this, mAmazonS3Controller);
        mPicasso = (new Picasso.Builder(this)).loader(downloader).build();

        mPlacesController = new PlacesController(this);
        mUserService = new UserService(mPrefs);
        mUserService.loadPlaces();
        mPlacesController.preloadPlaces();
    }

    public static <T> T getService(Class<T> serviceClass) {
        return ((StrollimoApplication)mContext.getApplicationContext()).getServiceInstance(serviceClass);
    }

    public <T> T getServiceInstance(Class<T> serviceClass) {
        if (serviceClass == PlacesController.class) {
            return (T) mPlacesController;
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
        } else if (serviceClass == Picasso.class) {
            return (T) mPicasso;
        }
        return null;

    }
}
