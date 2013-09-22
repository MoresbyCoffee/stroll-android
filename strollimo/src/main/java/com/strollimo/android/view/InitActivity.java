package com.strollimo.android.view;

import android.app.Activity;
import android.content.Intent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.StrollimoPreferences;
import com.strollimo.android.controller.AccomplishableController;
import com.strollimo.android.network.ImagesPreloader;
import com.strollimo.android.view.dialog.SyncDialogHelper;

public class InitActivity extends Activity {
    @Override
    protected void onResume() {
        super.onResume();
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
                            advanceToMainActivity();
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
            }

            @Override
            public void onError(String errorMsg) {
                advanceToMainActivity();
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

}
