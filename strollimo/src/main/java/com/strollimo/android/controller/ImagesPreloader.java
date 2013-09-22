package com.strollimo.android.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.model.Mystery;
import com.strollimo.android.model.Secret;
import com.strollimo.android.network.AmazonS3Controller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcoc on 22/09/2013.
 */
public class ImagesPreloader {

    private static final String TAG = ImagesPreloader.class.getSimpleName();

    private final Context mContext;
    private final AccomplishableController mAccomplishableController;
    private final AccomplishableController.OperationCallback mOperationCallback;
    private final List<String> mImageUrls;
    private final ProgressDialog mProgressDialog;

    private int mSuccessfulCount;
    private int mErrorCount;

    private final Response.Listener<Bitmap> mResponseListener = new Response.Listener<Bitmap>() {
        @Override
        public void onResponse(Bitmap response) {
            mSuccessfulCount++;
            onOperationCompleted();
        }
    };

    private final Response.ErrorListener mErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            mErrorCount++;
            onOperationCompleted();
        }
    };

    public ImagesPreloader(Context context, AccomplishableController accomplishableController, final AccomplishableController.OperationCallback callback) {
        mContext = context;
        mAccomplishableController = accomplishableController;
        mOperationCallback = callback;
        mImageUrls = new ArrayList<String>();
        mProgressDialog = new ProgressDialog(context);
    }

    public void start() {

        getImageUrls();

        mSuccessfulCount = 0;
        mErrorCount = 0;

        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(mImageUrls.size());
        mProgressDialog.setMessage("Downloading images...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        RequestQueue requestQueue = VolleyRequestQueue.getInstance();
        for (String imageUrl : mImageUrls) {
            ImageRequest request = new ImageRequest(imageUrl, mResponseListener, 0, 0, Bitmap.Config.RGB_565, mErrorListener);
            requestQueue.add(request);
        }
    }

    private void onOperationCompleted() {
        mProgressDialog.incrementProgressBy(1);
        if (mSuccessfulCount + mErrorCount == mImageUrls.size()) {
            mProgressDialog.dismiss();
            if (mOperationCallback != null) {
                if (mErrorCount > 0) {
                    mOperationCallback.onError(mErrorCount +" images not downloaded");
                } else {
                    mOperationCallback.onSuccess();
                }
            }
        }
    }

    private void getImageUrls() {
        mImageUrls.clear();

        List<Mystery> mysteries = mAccomplishableController.getAllMysteries();

        for (Mystery mystery : mysteries) {
            String mysteryImageUrl = StrollimoApplication.getService(AmazonS3Controller.class).getUrl(mystery.getImgUrl());
            if (!TextUtils.isEmpty(mysteryImageUrl)) {
                mImageUrls.add(mysteryImageUrl);
            }
            for (String secretId : mystery.getChildren()) {
                Secret secret = mAccomplishableController.getSecretById(secretId);
                if (secret == null) {
                    Log.e(TAG, "Error - secret is not available preloading the images: " + secretId);
                    continue;
                }
                String secretImageUrl = StrollimoApplication.getService(AmazonS3Controller.class).getUrl(secret.getImgUrl());
                if (!TextUtils.isEmpty(secretImageUrl)) {
                    mImageUrls.add(secretImageUrl);
                }
            }
        }
    }
}
