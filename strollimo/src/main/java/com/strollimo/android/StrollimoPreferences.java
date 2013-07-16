package com.strollimo.android;

import android.content.SharedPreferences;

import com.strollimo.android.model.Mission;

import java.util.HashSet;

public class StrollimoPreferences {
    private static final String COIN_VALUE_KEY = "COIN_VALUE_KEY";
    private static final String CAPTURE_PLACE_KEY = "CAPTURED_PLACE_";
    private static final String CAPTURED_PLACES_NUM_KEY = "CAPTURED_PLACES_NUM";
    public static final String USE_BARCODE_KEY = "USE_BARCODE";
    public static final String DEBUG_MODE_ON = "DEBUG_MODE_ON";
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

    public HashSet<Integer> getCapturedPlaces() {
        HashSet<Integer> places = new HashSet<Integer>();
        int maxCaptured = mPrefs.getInt(CAPTURED_PLACES_NUM_KEY, 0);
        for (int i = 1; i < maxCaptured + 1; i++) {
            int placeId = mPrefs.getInt(CAPTURE_PLACE_KEY + i, -1);
            if (placeId != -1) {
                places.add(placeId);
            }
        }
        return places;
    }

    public void clearCapturedPlaces() {
        mPrefs.edit().putInt(CAPTURED_PLACES_NUM_KEY, 0).apply();
    }

    public void saveNewPlace(int capturedPlaces, Mission mission) {
        mPrefs.edit().putInt(CAPTURED_PLACES_NUM_KEY, capturedPlaces).commit();
        mPrefs.edit().putInt(CAPTURE_PLACE_KEY + capturedPlaces, mission.getId()).commit();
        mPrefs.edit().putInt(COIN_VALUE_KEY + capturedPlaces, mission.getId()).commit();
    }

    public void setDebugModeOn(boolean debugModeOn) {
        mPrefs.edit().putBoolean(DEBUG_MODE_ON, debugModeOn).apply();
    }

    public boolean isDebugModeOn() {
        return mPrefs.getBoolean(DEBUG_MODE_ON, false);
    }


}
