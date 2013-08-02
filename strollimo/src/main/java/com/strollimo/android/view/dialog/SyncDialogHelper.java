package com.strollimo.android.view.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;
import com.strollimo.android.StrollimoPreferences;
import com.strollimo.android.controller.AccomplishableController;

import static com.strollimo.android.controller.AccomplishableController.OperationCallback;

public class SyncDialogHelper {
    public static void syncData(final String envTag, final Activity activity, final AccomplishableController accomplishableController,
                                final StrollimoPreferences prefs, final OperationCallback callback) {
        final ProgressDialog progressDialog = ProgressDialog.show(activity, "", "Please wait...");
        progressDialog.show();
        accomplishableController.clearMysteries();
        accomplishableController.asyncSyncMysteries(envTag, new OperationCallback() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                prefs.saveSyncTime();
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(String errorMsg) {
                Toast.makeText(activity, errorMsg, Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                if (callback != null) {
                    callback.onError(errorMsg);
                }
            }
        });
    }

    public static void syncData(final String envTag, final Activity activity, final AccomplishableController accomplishableController,
                                final StrollimoPreferences prefs) {
        syncData(envTag, activity, accomplishableController, prefs, null);
    }
}
