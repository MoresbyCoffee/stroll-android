package com.strollimo.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class PhotoCaptureActivity extends Activity {
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_capture);
    }

    public static void initiatePhotoCapture(Activity activity) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, PhotoCaptureActivity.class);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }
}
