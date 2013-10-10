package com.strollimo.android.controller.ImageUploader;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.squareup.otto.Bus;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.event.ImageUploadSuccessEvent;


public class ImageUploadTaskService extends Service implements ImageUploadTask.Callback {
    private static final String TAG = "Tape:ImageUploadTaskService";

    private ImageUploadTaskQueue mQueue;
    private Bus mBus;

    private boolean running;

    @Override
    public void onCreate() {
        super.onCreate();
        mQueue = StrollimoApplication.getService(ImageUploadTaskQueue.class);
        mBus = StrollimoApplication.getService(Bus.class);
        Log.i(TAG, "Service starting!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        executeNext();
        return START_STICKY;
    }

    private void executeNext() {
        if (running)
            return; // Only one task at a time.

        ImageUploadTask task = mQueue.peek();
        if (task != null) {
            running = true;
            task.execute(this);
        } else {
            Log.i(TAG, "Service stopping!");
            stopSelf(); // No more tasks are present. Stop.
        }
    }

    @Override
    public void onSuccess(final String url) {
        Log.i(TAG, "Upload image success for url: "+url);
        running = false;
        mQueue.remove();
        //mBus.post(new ImageUploadSuccessEvent(url));
        executeNext();
    }

    @Override
    public void onFailure() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}