package com.strollimo.android;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserService {
    public static final String CAPTURE_PLACE_KEY = "CAPTURED_PLACE_";
    public static final String CAPTURED_PLACES_NUM_KEY = "CAPTURED_PLACES_NUM";
    private final SharedPreferences mPrefs;
    private Set<Integer> mCapturedPlaces;
    private int mCapturedPlaceNum;
    private int mAllCoins;
    private List<Level> mLevels;
    private String COIN_VALUE_KEY = "COIN_VALUE_KEY";

    public UserService(SharedPreferences prefs) {
        mPrefs = prefs;
        mCapturedPlaces = new HashSet<Integer>();
        mLevels = new ArrayList<Level>();
        mLevels.add(new Level(5, "novice explorer"));
        mLevels.add(new Level(10, "explorer"));
        mLevels.add(new Level(15, "seasoned explorer"));
    }

    public void loadPlaces() {
        mCapturedPlaces = new HashSet<Integer>();
        mAllCoins = mPrefs.getInt(COIN_VALUE_KEY, 0);
        mCapturedPlaceNum = mPrefs.getInt(CAPTURED_PLACES_NUM_KEY, 0);
        for (int i = 1; i < mCapturedPlaceNum + 1; i++) {
            int placeId = mPrefs.getInt(CAPTURE_PLACE_KEY + i, -1);
            if (placeId != -1) {
                mCapturedPlaces.add(placeId);
            }
        }
    }

    public void reset() {
        mCapturedPlaceNum = 0;
        mPrefs.edit().putInt(CAPTURED_PLACES_NUM_KEY, mCapturedPlaceNum).commit();
        mAllCoins = 0;
        mPrefs.edit().putInt(COIN_VALUE_KEY, mAllCoins).commit();
        mCapturedPlaces.clear();
    }

    public boolean capturePlace(Place place) {
        mCapturedPlaces.add(place.mId);
        mCapturedPlaceNum++;
        String currentLevel = getCurrentLevel();
        mAllCoins += place.mCoinValue;
        String updatedLevel = getCurrentLevel();

        mPrefs.edit().putInt(CAPTURED_PLACES_NUM_KEY, mCapturedPlaceNum).commit();
        mPrefs.edit().putInt(CAPTURE_PLACE_KEY + mCapturedPlaceNum, place.mId).commit();
        mPrefs.edit().putInt(COIN_VALUE_KEY + mCapturedPlaceNum, place.mId).commit();
        if (currentLevel.equals(updatedLevel)) {
            return false;
        } else {
            return true;
        }
    }

    public String getCurrentLevel() {
        for (int i=1; i<mLevels.size(); i++) {
            if (mAllCoins >= mLevels.get(i-1).mCoins && mAllCoins < mLevels.get(i).mCoins) {
                return mLevels.get(i-1).mName;
            }
        }
        return "";
    }

    public String getNextLevel() {
        for (Level level : mLevels) {
            if (mAllCoins < level.mCoins) {
                return level.mName;
            }
        }
        return "";
    }

    public boolean isPlaceCaptured(int placeId) {
        return mCapturedPlaces.contains(placeId);
    }

    public int getFoundPlacesNum() {
        return mCapturedPlaceNum;
    }

    public int getAllCoins() {
        return mAllCoins;
    }

    private static class Level {
        public int mCoins;
        public String mName;
        public Level(int coins, String name) {
            mCoins = coins;
            mName = name;
        }
    }
}
