package com.strollimo.android;

import android.content.SharedPreferences;
import com.strollimo.android.model.Mystery;
import com.strollimo.android.model.Secret;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class StrollimoPreferences {
    private static final String COIN_VALUE_KEY = "COIN_VALUE_KEY";
    private static final String CAPTURE_PLACE_KEY = "CAPTURED_PLACE_";
    private static final String CAPTURED_PLACES_NUM_KEY = "CAPTURED_PLACES_NUM";
    public static final String USE_BARCODE_KEY = "USE_BARCODE";
    public static final String DEBUG_MODE_ON = "DEBUG_MODE_ON";
    public static final String SID = "sid_";
    public static final String STITLE = "stitle_";
    public static final String SDESC = "sdesc_";
    public static final String SIMAGE = "simage_";
    public static final String MMISSIONS = "mmissions_";
    public static final String MLAT = "mlat_";
    public static final String MLON = "mlon_";
    public static final String MTITLE = "mtitle_";
    public static final String MSECRET = "msecret_";
    private SharedPreferences mPrefs;

    public StrollimoPreferences(SharedPreferences prefs) {
        mPrefs = prefs;
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

    public void saveMission(int capturedPlaces, Mystery mystery) {
        mPrefs.edit().putInt(CAPTURED_PLACES_NUM_KEY, capturedPlaces).commit();
        mPrefs.edit().putString(CAPTURE_PLACE_KEY + capturedPlaces, mystery.getId()).commit();
        mPrefs.edit().putString(COIN_VALUE_KEY + capturedPlaces, mystery.getId()).commit();
    }

    public void setDebugModeOn(boolean debugModeOn) {
        mPrefs.edit().putBoolean(DEBUG_MODE_ON, debugModeOn).apply();
    }

    public boolean isDebugModeOn() {
        return mPrefs.getBoolean(DEBUG_MODE_ON, false);
    }

    public void saveSecret(Secret secret) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(SID +secret.getId(), secret.getId());
        editor.putString(STITLE +secret.getId(), secret.getTitle());
        editor.putString(SDESC +secret.getId(), secret.getShortDesc());
        editor.putString(SIMAGE +secret.getId(), secret.getImageUrl());
        editor.apply();
    }

    public Secret getSecret(String id) {
        String title = mPrefs.getString(STITLE+ id, "");
        String desc = mPrefs.getString(SDESC+ id, "");
        String imageUrl = mPrefs.getString(SIMAGE+ id, "");
        Secret secret = new Secret(id, title);
        secret.setShortDesc(desc);
        secret.setImageUrl(imageUrl);
        return secret;
    }

    public Mystery getMission(String id) {
        double lat = Double.parseDouble(mPrefs.getString(MLAT + id, "0"));
        double lon = Double.parseDouble(mPrefs.getString(MLON + id, "0"));
        String title = mPrefs.getString(MTITLE + id, "");
        int i = 0;
        Mystery mystery = new Mystery(id, title, lat, lon);
        while (mPrefs.getString(MSECRET +id+"_"+i, "") != "") {
            String secretId = mPrefs.getString(MSECRET +id+"_"+i, "");
            Secret secret = getSecret(secretId);
            mystery.addSecret(secret);
            i++;
        }
        return mystery;
    }

    public List<Mystery> getMissions() {
        ArrayList<Mystery> mysteries = new ArrayList<Mystery>();
        int i = 0;
        while (mPrefs.getString(MMISSIONS +i, "") != "") {
            String missionId = mPrefs.getString(MMISSIONS+i, "");
            mysteries.add(getMission(missionId));
            i++;
        }
        return mysteries;
    }

    public void saveMission2(int order, Mystery mystery) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(MMISSIONS +order, mystery.getId());
        editor.putString(MLAT + mystery.getId(), Double.toString(mystery.getLat()));
        editor.putString(MLON + mystery.getId(), Double.toString(mystery.getLon()));
        editor.putString(MTITLE + mystery.getId(), mystery.getTitle());
        for (int i=0; i< mystery.getSecrets().size(); i++) {
            editor.putString(MSECRET + mystery.getId() + "_" + i, mystery.getSecrets().get(i).getId());
            saveSecret(mystery.getSecrets().get(i));
        }
        editor.apply();

    }

    public void saveMissions(List<Mystery> mysteries) {
        for (int i=0; i< mysteries.size(); i++) {
            saveMission2(i, mysteries.get(i));
        }
    }

}
