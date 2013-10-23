package com.strollimo.android.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.strollimo.android.core.AmazonS3Controller;
import com.strollimo.android.models.network.AmazonUrl;
import com.strollimo.android.utils.BitmapUtils;

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

    public void asyncUploadPhotoToAmazon(final AmazonUrl amazonUrl, final Bitmap photo, final Callback callback) {
        new AsyncTask<Void, Void, Boolean>() {
            private AmazonS3Exception exception;

            @Override
            protected Boolean doInBackground(Void... voids) {
                File file = BitmapUtils.saveImageToFile(mContext, amazonUrl.getFile(), photo);
                try {
                    mAmazonS3Controller.uploadFile(amazonUrl.getBucket(), amazonUrl.getPath(), file);
                    return true;
                } catch (AmazonS3Exception ex) {
                    exception = ex;
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (callback != null) {
                    if (result == true) {
                        callback.onSuccess();
                    } else {
                        callback.onError(exception);
                    }
                }
            }
        }.execute();
    }

    public interface Callback {
        public void onSuccess();

        public void onError(Exception ex);
    }

}
