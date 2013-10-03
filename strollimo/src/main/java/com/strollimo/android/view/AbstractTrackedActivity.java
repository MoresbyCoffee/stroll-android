package com.strollimo.android.view;

import android.app.Activity;

import com.flurry.android.FlurryAgent;
import com.strollimo.android.util.Analytics;
import com.strollimo.android.util.Utils;

public abstract class AbstractTrackedActivity extends Activity {



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
