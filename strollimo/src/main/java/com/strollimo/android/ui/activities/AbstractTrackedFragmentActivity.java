package com.strollimo.android.ui.activities;

import android.support.v4.app.FragmentActivity;

import com.flurry.android.FlurryAgent;
import com.strollimo.android.utils.Analytics;
import com.strollimo.android.utils.Utils;

public class AbstractTrackedFragmentActivity extends FragmentActivity {

    @Override
    protected void onStart() {
        super.onStart();
        final String token;
        if (Utils.isDebugBuild()) {
            token = Analytics.FLURRY_DEBUG_KEY;
        } else {
            token = Analytics.FLURRY_PRODUCTION_KEY;
        }
        FlurryAgent.onStartSession(this, token);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }
}
