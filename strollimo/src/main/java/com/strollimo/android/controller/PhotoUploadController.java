package com.strollimo.android.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import com.strollimo.android.network.AmazonS3Controller;
import com.strollimo.android.network.AmazonUrl;
import com.strollimo.android.util.BitmapUtils;

import java.io.File;

public class PhotoUploadController {
    private Context mContext;
    private Handler mHandler;
    private AmazonS3Controller mAmazonS3Controller;

    public PhotoUploadController(Context context, AmazonS3Controller amazonS3Controller) {
        mContext = context;
        HandlerThread thread = new HandlerThread("imageUploader") {
            @Override
            protected void onLooperPrepared() {
                super.onLooperPrepared();
                mHandler = new Handler(getLooper());
            }
        };
        thread.start();
        mAmazonS3Controller = amazonS3Controller;
    }

    public void uploadPhotoToAmazon(final AmazonUrl amazonUrl, final Bitmap photo) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                File file = BitmapUtils.saveImageToFile(mContext, amazonUrl.getFile(), photo);
                mAmazonS3Controller.uploadFile(amazonUrl.getBucket(), amazonUrl.getFile(), file);
            }
        });
    }
}
