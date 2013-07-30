package com.strollimo.android.network;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.squareup.picasso.UrlConnectionLoader;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;

public class AmazonDownloader extends UrlConnectionLoader {
    private final static String TAG = AmazonNetworkManager.class.getSimpleName();
    private AmazonS3Controller mAmazonController;

    public AmazonDownloader(Context context, AmazonS3Controller amazonS3Controller) {
        super(context);
        mAmazonController = amazonS3Controller;
    }

    @Override public Response load(Uri uri, boolean localCacheOnly) throws IOException {
        AmazonUrl amazonUrl;
        try {
            amazonUrl = AmazonUrl.fromUrl(uri.toString());
        } catch (ParseException e) {
            Log.e(TAG, "Wrong amazon URL", e);
            return null;
        }
        URL url = mAmazonController.getUrl(amazonUrl);
        return super.load(Uri.parse(url.toString()), localCacheOnly);
    }
}
