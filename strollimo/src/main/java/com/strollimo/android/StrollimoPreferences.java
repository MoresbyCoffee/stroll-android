package com.strollimo.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.strollimo.android.model.Mystery;
import com.strollimo.android.model.Secret;

import java.lang.reflect.Type;
import java.util.*;

public class StrollimoPreferences {
    private static final String TAG = StrollimoPreferences.class.getSimpleName();

    public static final String USE_BARCODE_KEY = "USE_BARCODE";
    public static final String DEBUG_MODE_ON = "pref_debug_mode_on";
    public static final String MISSIONS_KEY = "mmissions_";
    public static final String SECRET_KEY = "SECRET";
    private static final String CAPTURED_SECRET_KEY = "CAPTURED_SECRET_";
    private static final String CAPTURED_SECRETS_NUM_KEY = "CAPTURED_SECRET_NUM";
    public static final String DEVICEID_KEY = "DEVICEID_KEY";
    public static final String ENV_TAG_KEY = "ENV_TAG_KEY";
    public static final String DEFAULT_ENV_TAG = "cv";
    public static final String LAST_SYNC_KEY = "LAST_SYNC";
    private static final String FEEDBACK_COMPLETED_KEY = "feedback_completed";

    // The client should sync daily
    //public static final int SYNC_INTERVAL = 24 * 60 * 60 * 1000;
    private final Context mContext;
    private final Gson mGson;
    private SharedPreferences mPrefs;

    public StrollimoPreferences(Context context, SharedPreferences prefs, Gson gson) {
        mPrefs = prefs;
        mContext = context;
        mGson = gson;
    }

    public String getDeviceUUID() {
        String deviceId = mPrefs.getString(DEVICEID_KEY, null);
        if (deviceId == null) {
            final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

            final String tmDevice, tmSerial, androidId;
            tmDevice = "" + tm.getDeviceId();
            tmSerial = "" + tm.getSimSerialNumber();
            androidId = "" + android.provider.Settings.Secure.getString(mContext.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            deviceId = deviceUuid.toString();
            mPrefs.edit().putString(DEVICEID_KEY, deviceId);
        }
        return deviceId;
    }

    public boolean isUseBarcode() {
        return mPrefs.getBoolean(USE_BARCODE_KEY, false);
    }

    public void setUseBarcode(boolean useBarcode) {
        mPrefs.edit().putBoolean(USE_BARCODE_KEY, useBarcode).apply();
    }

    public HashSet<String> getCapturedSecrets() {
        HashSet<String> places = new HashSet<String>();
        int maxCaptured = mPrefs.getInt(CAPTURED_SECRETS_NUM_KEY, 0);
        for (int i = 1; i < maxCaptured + 1; i++) {
            String placeId = mPrefs.getString(CAPTURED_SECRET_KEY + i, "");
            if (placeId != "") {
                places.add(placeId);
            }
        }
        return places;
    }

    public void clearCapturedSecrets() {
        mPrefs.edit().putInt(CAPTURED_SECRETS_NUM_KEY, 0).apply();
    }

    public boolean isDebugModeOn() {
        return mPrefs.getBoolean(DEBUG_MODE_ON, false);
    }

    public void setDebugModeOn(boolean debugModeOn) {
        mPrefs.edit().putBoolean(DEBUG_MODE_ON, debugModeOn).apply();
    }

    public boolean isFeedbackCompleted() {
        return mPrefs.getBoolean(FEEDBACK_COMPLETED_KEY, false);
    }

    public void setFeedbackCompleted(boolean completed) {
        mPrefs.edit().putBoolean(FEEDBACK_COMPLETED_KEY, completed).apply();
    }

    public String getEnvTag() {
        return mPrefs.getString(ENV_TAG_KEY, DEFAULT_ENV_TAG);
    }

    public void setEnvTag(String envTag) {
        mPrefs.edit().putString(ENV_TAG_KEY, envTag).apply();
    }

    public void saveSecret(Secret secret) {
        SharedPreferences.Editor editor = mPrefs.edit();
        if (secret != null) {
            editor.putString(SECRET_KEY + secret.getId(), mGson.toJson(secret));
            editor.apply();
        }
    }

    public Secret getSecret(String id) {
        String json = mPrefs.getString(SECRET_KEY + id, "");
        return mGson.fromJson(json, Secret.class);
    }

    public void clearSecret(String id) {
        mPrefs.edit().remove(SECRET_KEY + id).apply();
    }

    public List<Mystery> getMysteries() {
        String json = mPrefs.getString(MISSIONS_KEY, "");
        return getMysteriesFromJson(json);
    }

    public void clearMysteries() {
        mPrefs.edit().remove(MISSIONS_KEY).apply();
    }

    private List<Mystery> getMysteriesFromJson(String json) {
        Type listType = new TypeToken<ArrayList<Mystery>>() {
        }.getType();
        List<Mystery> mysteries = mGson.fromJson(json, listType);
        return mysteries;
    }

    public void saveMissions(List<Mystery> mysteries, Map<String, Secret> secrets) {
        if (mysteries == null || secrets == null || secrets.size() == 0 || mysteries.size() == 0) {
            return;
        }
        SharedPreferences.Editor editor = mPrefs.edit();
        String json = mGson.toJson(mysteries);
        editor.putString(MISSIONS_KEY, json);
        for (Mystery mystery : mysteries) {
            if (mystery != null) {
                for (String secretId : mystery.getChildren()) {
                    Secret secret = secrets.get(secretId);
                    if (secret == null) {
                        Log.e(TAG, "Error - secret is not available: " + secretId);
                    } else {
                        saveSecret(secret);
                    }
                }
            }
        }
        editor.apply();
    }

    public boolean needInitialSync() {
        long lastSync = mPrefs.getLong(LAST_SYNC_KEY, 0);
        //if (System.currentTimeMillis() - lastSync > SYNC_INTERVAL) {
        if (lastSync == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void saveSyncTime() {
        mPrefs.edit().putLong(LAST_SYNC_KEY, System.currentTimeMillis()).apply();
    }

}