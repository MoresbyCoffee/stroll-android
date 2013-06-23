package com.strollimo.android;

import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class UserService {
    public static final String CAPTURE_PLACE_KEY = "CAPTURED_PLACE_";
    public static final String CAPTURED_PLACES_NUM_KEY = "CAPTURED_PLACES_NUM";
    private final SharedPreferences mPrefs;
    private Set<Integer> mCapturedPlaces;
    private int mCapturedPlaceNum;

    public UserService(SharedPreferences prefs) {
        mPrefs = prefs;
        mCapturedPlaces = new HashSet<Integer>();
    }

    public void loadPlaces() {
        mCapturedPlaces = new HashSet<Integer>();
        mCapturedPlaceNum = mPrefs.getInt(CAPTURED_PLACES_NUM_KEY, 0);
        for (int i = 0; i < mCapturedPlaceNum; i++) {
            int placeId = mPrefs.getInt(CAPTURE_PLACE_KEY + i, -1);
            if (placeId != -1) {
                mCapturedPlaces.add(placeId);
            }
        }
    }

    public void reset() {
        mCapturedPlaceNum = 0;
        mPrefs.edit().putInt(CAPTURED_PLACES_NUM_KEY, mCapturedPlaceNum).commit();
        mCapturedPlaces.clear();
    }

    public void capturePlace(int placeId) {
        mCapturedPlaces.add(placeId);
        mPrefs.edit().putInt(CAPTURE_PLACE_KEY + mCapturedPlaceNum, placeId).commit();
        mCapturedPlaceNum++;
        mPrefs.edit().putInt(CAPTURED_PLACES_NUM_KEY, mCapturedPlaceNum).commit();
    }

    public boolean isPlaceCaptured(int placeId) {
        return mCapturedPlaces.contains(placeId);
    }
}
