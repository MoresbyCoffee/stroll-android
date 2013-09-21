package com.strollimo.android.util;

import android.content.pm.ApplicationInfo;

import com.strollimo.android.StrollimoApplication;

/**
 * Created by marcoc on 21/09/2013.
 */
public class Utils {

    public static boolean isDebugBuild() {
        return ( 0 != (StrollimoApplication.getContext().getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE ) );
    }
}
