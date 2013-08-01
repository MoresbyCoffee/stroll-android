package com.strollimo.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.strollimo.android.model.Mystery;
import com.strollimo.android.model.Secret;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.*;

public class StrollimoPreferences {
    public static final String USE_BARCODE_KEY = "USE_BARCODE";
    public static final String DEBUG_MODE_ON = "DEBUG_MODE_ON";
    public static final String MISSIONS_KEY = "mmissions_";
    public static final String SECRET_KEY = "SECRET";
    private static final String COIN_VALUE_KEY = "COIN_VALUE_KEY";
    private static final String CAPTURE_PLACE_KEY = "CAPTURED_PLACE_";
    private static final String CAPTURED_PLACES_NUM_KEY = "CAPTURED_PLACES_NUM";
    public static final String DEVICEID_KEY = "DEVICEID_KEY";
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

    public int getCoins() {
        return mPrefs.getInt(COIN_VALUE_KEY, 0);
    }

    public void saveCoins(int coins) {
        mPrefs.edit().putInt(COIN_VALUE_KEY, coins).apply();
    }

    public HashSet<String> getCapturedPlaces() {
        HashSet<String> places = new HashSet<String>();
        int maxCaptured = mPrefs.getInt(CAPTURED_PLACES_NUM_KEY, 0);
        for (int i = 1; i < maxCaptured + 1; i++) {
            String placeId = mPrefs.getString(CAPTURE_PLACE_KEY + i, "");
            if (placeId != "") {
                places.add(placeId);
            }
        }
        return places;
    }

    public void clearCapturedPlaces() {
        mPrefs.edit().putInt(CAPTURED_PLACES_NUM_KEY, 0).apply();
    }

    public boolean isDebugModeOn() {
        return mPrefs.getBoolean(DEBUG_MODE_ON, false);
    }

    public void setDebugModeOn(boolean debugModeOn) {
        mPrefs.edit().putBoolean(DEBUG_MODE_ON, debugModeOn).apply();
    }

    public void saveSecret(Secret secret) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(SECRET_KEY + secret.getId(), mGson.toJson(secret));
        editor.apply();
    }

    public Secret getSecret(String id) {
        String json = mPrefs.getString(SECRET_KEY + id, "");
        return mGson.fromJson(json, Secret.class);
    }

    public List<Mystery> getMysteries() {
        String json = mPrefs.getString(MISSIONS_KEY, "");
        return getMysteriesFromJson(json);
    }

    private List<Mystery> getMysteriesFromJson(String json) {
        Type listType = new TypeToken<ArrayList<Mystery>>() {
        }.getType();
        List<Mystery> mysteries = mGson.fromJson(json, listType);
        return mysteries;
    }

    public List<Mystery> getHardcodedMysteries() {
        try {
            InputStream is = mContext.getAssets().open("demo.json");
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer);
            return getMysteriesFromJson(json);
        } catch (IOException e) {
            return null;
        }
    }

    public void saveMissions(List<Mystery> mysteries, Map<String, Secret> secrets) {
        SharedPreferences.Editor editor = mPrefs.edit();
        String json = mGson.toJson(mysteries);
        Log.i("BB4", json);
        editor.putString(MISSIONS_KEY, json);
        for (Mystery mystery : mysteries) {
            for (String secretId : mystery.getChildren()) {
                Secret secret = secrets.get(secretId);
                saveSecret(secret);
            }
        }
        editor.apply();
    }

}