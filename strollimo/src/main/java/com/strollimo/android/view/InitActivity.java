package com.strollimo.android.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.strollimo.android.R;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.StrollimoPreferences;
import com.strollimo.android.controller.AccomplishableController;
import com.strollimo.android.controller.EnvSyncManager;

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
                    syncEnvironment();
                } else {
                    advanceToMainActivity();
                }
                break;
            default:
                GooglePlayServicesUtil.getErrorDialog(errorCode, this, 0).show();
                break;
        }
    }

    private void syncEnvironment() {
        String env = StrollimoApplication.getService(StrollimoPreferences.class).getEnvTag();
        EnvSyncManager preloader = new EnvSyncManager(this, env, new AccomplishableController.OperationCallback() {
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
                    .setMessage(getString(R.string.full_sync_dialog_error_msg))
                    .create();
            mRetryDialog.setCancelable(false);
            mRetryDialog.setCanceledOnTouchOutside(false);
        }
        return mRetryDialog;
    }



}
