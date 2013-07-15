package com.strollimo.android;

import android.app.Application;
import android.content.Context;
import com.strollimo.android.controller.AmazonS3Controller;
import com.strollimo.android.controller.PlacesController;
import com.strollimo.android.controller.UserService;

public class StrollimoApplication extends Application {
    private static Context mContext;
    private PlacesController mPlacesController;
    private UserService mUserService;
    private StrollimoPreferences mPrefs;
    private AmazonS3Controller mAmazonS3Controller;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mPrefs = new StrollimoPreferences(getSharedPreferences("StrollimoPreferences", 0));
        mPlacesController = new PlacesController(this);
        mUserService = new UserService(mPrefs);
        mUserService.loadPlaces();
        mAmazonS3Controller = new AmazonS3Controller();
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
        }
        return null;

    }
}
