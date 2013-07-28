package com.strollimo.android.network;

import android.util.Log;
import com.novoda.imageloader.core.LoaderSettings;
import com.novoda.imageloader.core.file.util.FileUtil;
import com.novoda.imageloader.core.network.UrlNetworkManager;
import com.strollimo.android.StrollimoApplication;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;

public class AmazonNetworkManager extends UrlNetworkManager {
    private final static String TAG = AmazonNetworkManager.class.getSimpleName();
    private AmazonS3Controller mAmazonController;

    public AmazonNetworkManager(LoaderSettings settings) {
        super(settings);
        init();
    }

    public AmazonNetworkManager(LoaderSettings settings, FileUtil fileUtil) {
        super(settings, fileUtil);
        init();
    }

    private void init() {
        mAmazonController = StrollimoApplication.getService(AmazonS3Controller.class);
    }

    @Override
    public void retrieveImage(String s, File file) {
        AmazonUrl amazonUrl;
        try {
            amazonUrl = AmazonUrl.fromUrl(s);
        } catch (ParseException e) {
            Log.e(TAG, "Wrong amazon URL", e);
            return;
        }
        URL url = mAmazonController.getUrl(amazonUrl);
        String urlString = url.toString();
        Log.i("BB", "url: " + urlString);
        super.retrieveImage(urlString, file);
    }

    @Override
    public InputStream retrieveInputStream(String s) {
        AmazonUrl amazonUrl;
        try {
            amazonUrl = AmazonUrl.fromUrl(s);
        } catch (ParseException e) {
            Log.e(TAG, "Wrong amazon URL", e);
            return null;
        }
        return super.retrieveInputStream(amazonUrl.toString());
    }
}
