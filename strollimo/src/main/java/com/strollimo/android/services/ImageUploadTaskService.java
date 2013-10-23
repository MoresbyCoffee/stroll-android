package com.strollimo.android.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.core.ImageUploadTask;
import com.strollimo.android.core.ImageUploadTaskQueueController;


public class ImageUploadTaskService extends Service implements ImageUploadTask.Callback {
    private static final String TAG = "Tape:ImageUploadTaskService";

    private ImageUploadTaskQueueController mQueue;

    private boolean running;

    @Override
    public void onCreate() {
        super.onCreate();
        mQueue = StrollimoApplication.getService(ImageUploadTaskQueueController.class);
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
        //here we could post an the event bus about a successful upload
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