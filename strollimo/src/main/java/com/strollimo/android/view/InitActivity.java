package com.strollimo.android.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.StrollimoPreferences;
import com.strollimo.android.controller.AccomplishableController;
import com.strollimo.android.controller.ImagesPreloader;
import com.strollimo.android.view.dialog.SyncDialogHelper;

public class InitActivity extends AbstractTrackedActivity {

    private AlertDialog mRetryDialog;

    @Override
    protected void onResume() {
        super.onResume();
        sync();
    }

    private void sync() {
        int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        switch (errorCode) {
            case ConnectionResult.SUCCESS:
                StrollimoPreferences prefs = StrollimoApplication.getService(StrollimoPreferences.class);
                if (prefs.needInitialSync()) {
                    AccomplishableController accomplishableController = StrollimoApplication.getService(AccomplishableController.class);
                    SyncDialogHelper.syncData(prefs.getEnvTag(), this, accomplishableController, prefs, new AccomplishableController.OperationCallback() {
                        @Override
                        public void onSuccess() {
                            preloadImages();
                        }

                        @Override
                        public void onError(String errorMsg) {
                            getRetryDialog().show();
                        }
                    });
                } else {
                    advanceToMainActivity();
                }
                break;
            default:
                GooglePlayServicesUtil.getErrorDialog(errorCode, this, 0).show();
                break;
        }
    }

    private void preloadImages() {
        ImagesPreloader preloader = new ImagesPreloader(this, StrollimoApplication.getService(AccomplishableController.class), new AccomplishableController.OperationCallback() {
            @Override
            public void onSuccess() {
                advanceToMainActivity();
                StrollimoApplication.getService(StrollimoPreferences.class).saveSyncTime();
            }

            @Override
            public void onError(String errorMsg) {
                getRetryDialog().show();
            }
        });
        preloader.start();
    }

    private void advanceToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }



    private AlertDialog getRetryDialog() {
        if (mRetryDialog == null) {
            mRetryDialog = new AlertDialog.Builder(this)
                    .setNeutralButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sync();
                            dialog.dismiss();
                        }
                    })
                    .setMessage("Your first hunt is to find a reliable internet connection! :)")
                    .create();
            mRetryDialog.setCancelable(false);
            mRetryDialog.setCanceledOnTouchOutside(false);
        }
        return mRetryDialog;
    }



}
