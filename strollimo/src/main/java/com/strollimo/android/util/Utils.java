package com.strollimo.android.util;

import android.content.pm.ApplicationInfo;

import com.strollimo.android.StrollimoApplication;

public class Utils {

    public static boolean isDebugBuild() {
        return ( 0 != (StrollimoApplication.getContext().getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE ) );
    }
}
