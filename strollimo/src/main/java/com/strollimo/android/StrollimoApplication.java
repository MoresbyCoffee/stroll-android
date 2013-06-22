package com.strollimo.android;

import android.app.Application;

public class StrollimoApplication extends Application {
    private PlacesService mPlacesService;
    private UserService mUserService;

    @Override
    public void onCreate() {
        super.onCreate();
        mPlacesService = new PlacesService(this);
        mUserService = new UserService();
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
