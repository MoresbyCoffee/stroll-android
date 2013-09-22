package com.strollimo.android.view.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.StrollimoPreferences;
import com.strollimo.android.controller.AccomplishableController;
import com.strollimo.android.controller.VolleyImageLoader;
import com.strollimo.android.model.Mystery;
import com.strollimo.android.model.Secret;
import com.strollimo.android.network.AmazonS3Controller;
import com.strollimo.android.network.response.GetMysteriesResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;

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
