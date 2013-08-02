package com.strollimo.android.view;

import android.app.Activity;
import android.content.Intent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class InitActivity extends Activity {
    @Override
    protected void onResume() {
        super.onResume();
        int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        switch (errorCode) {
            case ConnectionResult.SUCCESS:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            default:
                GooglePlayServicesUtil.getErrorDialog(errorCode, this, 0).show();
                break;
        }
    }
}
