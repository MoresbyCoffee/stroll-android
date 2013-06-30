package com.strollimo.android;

import android.app.Application;

public class StrollimoApplication extends Application {
    private PlacesService mPlacesService;
    private UserService mUserService;
    private StrollimoPreferences mPrefs;

    @Override
    public void onCreate() {
        super.onCreate();
        mPrefs = new StrollimoPreferences(getSharedPreferences("StrollimoPreferences", 0));
        mPlacesService = new PlacesService(this);
        mUserService = new UserService(mPrefs);
        mUserService.loadPlaces();
    }

    public <T> T getService(Class<T> serviceClass) {
        if (serviceClass == PlacesService.class) {
            return (T) mPlacesService;
        } else if (serviceClass == UserService.class) {
            return (T) mUserService;
        } else if (serviceClass == StrollimoPreferences.class) {
            return (T) mPrefs;
        }
        return null;

    }
}
