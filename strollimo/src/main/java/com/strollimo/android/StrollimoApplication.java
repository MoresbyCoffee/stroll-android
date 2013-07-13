package com.strollimo.android;

import android.app.Application;

import com.strollimo.android.controller.PlacesController;
import com.strollimo.android.controller.UserService;

public class StrollimoApplication extends Application {
    private PlacesController mPlacesController;
    private UserService mUserService;
    private StrollimoPreferences mPrefs;

    @Override
    public void onCreate() {
        super.onCreate();
        mPrefs = new StrollimoPreferences(getSharedPreferences("StrollimoPreferences", 0));
        mPlacesController = new PlacesController(this);
        mUserService = new UserService(mPrefs);
        mUserService.loadPlaces();
    }

    public <T> T getService(Class<T> serviceClass) {
        if (serviceClass == PlacesController.class) {
            return (T) mPlacesController;
        } else if (serviceClass == UserService.class) {
            return (T) mUserService;
        } else if (serviceClass == StrollimoPreferences.class) {
            return (T) mPrefs;
        }
        return null;

    }
}
