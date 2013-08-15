package com.strollimo.android.controller;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.strollimo.android.StrollimoApplication;

import java.io.File;

/**
 * Created by marcoc on 14/08/2013.
 */
public class VolleyRequestQueue {

    /** Default on-disk cache directory. */
    private static final String DEFAULT_CACHE_DIR = "volley";

    private static RequestQueue instance = null;

    public static synchronized RequestQueue getInstance() {
        if (instance == null) {
            Cache cache = getCache();
            Network network = getNetwork();
            instance = new RequestQueue(cache, network);
            instance.start();
        }
        return instance;
    }

    private static Cache getCache() {
        File cacheDir = new File(StrollimoApplication.getContext().getCacheDir(), DEFAULT_CACHE_DIR);
        return new DiskBasedCache(cacheDir);
    }

    private static Network getNetwork(){
        Context context = StrollimoApplication.getContext();
        String userAgent = "volley/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        HttpStack httpStack;
        if (Build.VERSION.SDK_INT >= 9) {
            httpStack = new HurlStack();
        } else {
            // Prior to Gingerbread, HttpUrlConnection was unreliable.
            // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
            httpStack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
        }

        Network network = new BasicNetwork(httpStack);
        return network;
    }
}
