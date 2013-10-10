package com.strollimo.android.controller.ImageUploader;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.squareup.tape.Task;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.network.AmazonS3Controller;
import com.strollimo.android.network.AmazonUrl;
import com.strollimo.android.network.StrollimoApi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

import retrofit.RetrofitError;

public class ImageUploadTask implements Task<ImageUploadTask.Callback> {
    private static final long serialVersionUID = 126142781146165256L;

    private static final String TAG = "Tape:ImageUploadTask";
    private static final Handler MAIN_THREAD = new Handler(Looper.getMainLooper());

    public interface Callback {
        void onSuccess(String url);
        void onFailure();
    }

    private final String mSecretId;
    private final AmazonUrl mAmazonUrl;
    private final Bitmap mBitmap;

    public ImageUploadTask(String secretId, AmazonUrl amazonUrl, Bitmap bitmap) {
        mSecretId = secretId;
        mAmazonUrl = amazonUrl;
        mBitmap = bitmap;
    }

    @Override
    public void execute(final Callback callback) {
        // Image uploading is slow. Execute HTTP POST on a background thread.
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ByteBuffer buffer = ByteBuffer.allocate(mBitmap.getByteCount());
                    mBitmap.copyPixelsToBuffer(buffer);
                    InputStream inputStream = new ByteArrayInputStream(buffer.array(), 0, mBitmap.getByteCount());

                    AmazonS3Controller amazonS3Controller = StrollimoApplication.getService(AmazonS3Controller.class);
                    amazonS3Controller.uploadStream(mAmazonUrl.getBucket(), mAmazonUrl.getPath(), inputStream, mBitmap.getByteCount());

                    Log.i(TAG, "Upload success! " + mAmazonUrl.getUrl());

                    StrollimoApplication.getService(StrollimoApi.class).getPickupSecret(mSecretId, mAmazonUrl.getUrl());


                    // Get back to the main thread before invoking a callback.
                    MAIN_THREAD.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(mAmazonUrl.getUrl());
                        }
                    });

                } catch (RuntimeException e) {
                    // Get back to the main thread before invoking a callback.
                    MAIN_THREAD.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "Upload failed :( Will retry.");
                            callback.onFailure();
                        }
                    });
                    // remember retrofit error
                }
            }
        }).start();
    }
}