package com.strollimo.android.controller;

import android.app.ActivityManager;
import android.content.Context;

import com.android.volley.toolbox.ImageLoader;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.util.BitmapLruImageCache;

/**
 * Created by marcoc on 14/08/2013.
 */
public class VolleyImageLoader {

    private static final float MEMORY_SIZE_RATIO = 1f/10f;

    private static ImageLoader instance = null;

    public static synchronized ImageLoader getInstance() {
        if (instance == null) {
            instance = new ImageLoader(VolleyRequestQueue.getInstance(), new BitmapLruImageCache(getSafeMemoryCacheSize()));
        }
        return instance;
    }

    /**
     * Get the maximum safe memory cache size for this particular device based on the # of mb allocated to each app.
     * This is a conservative estimate that has been safe for 2.2+ devices consistently. It is probably rather small
     * for newer devices.
     *
     * @param context A context
     * @return The maximum safe size for the memory cache for this devices in bytes
     */
    public static int getSafeMemoryCacheSize(){
        final ActivityManager activityManager = (ActivityManager) StrollimoApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        return Math.round(MEMORY_SIZE_RATIO * activityManager.getMemoryClass() * 1024 * 1024);
    }
}
