package com.strollimo.android.controller;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.model.BaseAccomplishable;
import com.strollimo.android.model.Secret;
import com.strollimo.android.network.StrollimoApi;
import com.strollimo.android.network.response.GetPickupStatusResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SecretStatusPollingService extends Service {
    private final static String TAG = SecretStatusPollingService.class.getSimpleName();

    public static final int REFRESH_INTERVAL = 20000;
    public static final String ACTION_SECRET_STATUS_UPDATED = "SECRET_STATUS_UPDATED";

    /** The service will try to get the status for this period. **/
    public static final int PULL_TIMEOUT = 1000 * 60;

    private Handler mHandler;
    private AccomplishableController mAccomplishableController;
    private long mStartTime;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        mAccomplishableController = StrollimoApplication.getService(AccomplishableController.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler.removeCallbacks(mGetSecretStatusesRunnable);
        mHandler.postDelayed(mGetSecretStatusesRunnable, REFRESH_INTERVAL);
        mStartTime = new Date().getTime();
        return super.onStartCommand(intent, flags, startId);
    }

    private Runnable mGetSecretStatusesRunnable = new Runnable() {

        @Override
        public void run() {
            Map<String, Secret> secrets = mAccomplishableController.getAllSecrets();
            List<String> refreshableSecretIds = new ArrayList<String>();
            for (Secret secret : secrets.values()) {
                if (secret.getPickupState() == BaseAccomplishable.PickupState.PENDING) {
                    refreshableSecretIds.add(secret.getId());
                }
            }
            if (refreshableSecretIds.size() == 0) {
                stopSelf();
                return;
            }
            StrollimoApplication.getService(StrollimoApi.class).getPickupStatus(refreshableSecretIds, new Callback<GetPickupStatusResponse>() {

                @Override
                public void success(GetPickupStatusResponse getPickupStatusResponse, Response response) {
                    boolean isModified = false;
                    Map<String, BaseAccomplishable.PickupState> statuses = getPickupStatusResponse.getSecretStatuses();
                    for (String stringId : statuses.keySet()) {
                        Secret secret = mAccomplishableController.getSecretById(stringId);
                        if (secret != null) {
                            secret.setPickupState(statuses.get(stringId));
                            isModified = true;
                        }
                    }
                    if (isModified) {
                        mAccomplishableController.saveAllData();
                        sendBroadcast(new Intent(ACTION_SECRET_STATUS_UPDATED));
                    }

                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    Log.e(TAG, "Error: " + retrofitError.toString());
                }
            });
            long now = new Date().getTime();
            if (now - mStartTime < PULL_TIMEOUT) {
                mHandler.removeCallbacks(mGetSecretStatusesRunnable);
                mHandler.postDelayed(this, REFRESH_INTERVAL);
            } else {
                mHandler.removeCallbacks(mGetSecretStatusesRunnable);
                stopSelf();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
