package org.moresbycoffee.stroll.android;

import android.app.Application;

public class StrollApplication extends Application {
    PlacesService mPlacesService;

    @Override
    public void onCreate() {
        super.onCreate();
        mPlacesService = new PlacesService();
    }

    public <T> T getService(Class<T> serviceClass) {
        if (serviceClass == PlacesService.class) {
            return (T) mPlacesService;
        }
        return null;

    }
}
