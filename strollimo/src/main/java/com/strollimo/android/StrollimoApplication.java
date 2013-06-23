package com.strollimo.android;

import android.app.Application;
import android.content.SharedPreferences;

public class StrollimoApplication extends Application {
    private PlacesService mPlacesService;
    private UserService mUserService;
    private SharedPreferences mPrefs;

    @Override
    public void onCreate() {
        super.onCreate();
        mPlacesService = new PlacesService(this);
        mPrefs = getSharedPreferences("StrollimoPreferences", 0);
        mUserService = new UserService(mPrefs);
        mUserService.loadPlaces();
    }

    public <T> T getService(Class<T> serviceClass) {
        if (serviceClass == PlacesService.class) {
            return (T) mPlacesService;
        } else if (serviceClass == UserService.class) {
            return (T) mUserService;
        }
        return null;

    }
}
