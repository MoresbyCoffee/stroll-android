package com.strollimo.android.core;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.strollimo.android.R;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.models.Mystery;
import com.strollimo.android.models.Secret;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class EnvSyncController {

    private static final String TAG = EnvSyncController.class.getSimpleName();
    public static final int DEFAULT_ITEM_NUM = 20;

    private final Context mContext;
    private final AccomplishableController mAccomplishableController;
    private final AccomplishableController.OperationCallback mOperationCallback;
    private final List<String> mImageUrls;
    private final ProgressDialog mProgressDialog;
    private final String mEnv;

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

    public EnvSyncController(Context context, String env, final AccomplishableController.OperationCallback callback) {
        mContext = context;
        mAccomplishableController = StrollimoApplication.getService(AccomplishableController.class);
        mOperationCallback = callback;
        mImageUrls = new ArrayList<String>();
        mProgressDialog = new ProgressDialog(context);
        mEnv = env;
    }

    public void start() {
        showProgressDialog();
        mAccomplishableController.clearMysteries();
        mAccomplishableController.asyncSyncMysteries(mEnv, new AccomplishableController.OperationCallback() {
            @Override
            public void onSuccess() {
                loadPhotosAsync();
            }

            @Override
            public void onError(String errorMsg) {
                mOperationCallback.onError("Failed to sync mysteries");
            }
        });
    }

    private void loadPhotosAsync() {
        getImageUrls();

        mProgressDialog.setMax(mImageUrls.size());
        mSuccessfulCount = 0;
        mErrorCount = 0;
        RequestQueue requestQueue = VolleyRequestQueue.getInstance();
        for (String imageUrl : mImageUrls) {
            ImageRequest request = new ImageRequest(imageUrl, mResponseListener, 0, 0, Bitmap.Config.RGB_565, mErrorListener);
            request.setRetryPolicy(new DefaultRetryPolicy(1000, 3, 0));
            requestQueue.add(request);
        }
    }

    private void showProgressDialog() {
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setProgressPercentFormat(NumberFormat.getPercentInstance());
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(DEFAULT_ITEM_NUM);
        mProgressDialog.setMessage(mContext.getString(R.string.full_sync_dialog_title));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setProgressNumberFormat(null);
        mProgressDialog.show();
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
