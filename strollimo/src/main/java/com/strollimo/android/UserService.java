package com.strollimo.android;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserService {
    private final StrollimoPreferences mPrefs;
    private Set<Integer> mCapturedPlaces;
    private int mCapturedPlaceNum;
    private int mAllCoins;
    private List<Level> mLevels;

    public UserService(StrollimoPreferences prefs) {
        mPrefs = prefs;
        mCapturedPlaces = new HashSet<Integer>();
        mLevels = new ArrayList<Level>();
        mLevels.add(new Level(5, "novice explorer"));
        mLevels.add(new Level(10, "explorer"));
        mLevels.add(new Level(15, "seasoned explorer"));
    }

    public void loadPlaces() {
        mCapturedPlaces = mPrefs.getCapturedPlaces();
        mCapturedPlaceNum = mCapturedPlaces.size();
        mAllCoins = mPrefs.getCoins();
    }

    public void reset() {
        mCapturedPlaceNum = 0;
        mAllCoins = 0;
        mCapturedPlaces.clear();
        mPrefs.clearCapturedPlaces();
        mPrefs.saveCoins(0);
    }

    public boolean capturePlace(Place place) {
        mCapturedPlaces.add(place.getmId());
        mCapturedPlaceNum++;
        String currentLevel = getCurrentLevel();
        mAllCoins += place.getmCoinValue();
        String updatedLevel = getCurrentLevel();

        mPrefs.saveNewPlace(mCapturedPlaceNum, place);
        if (currentLevel.equals(updatedLevel)) {
            return false;
        } else {
            return true;
        }
    }

    public String getCurrentLevel() {
        for (int i = 1; i < mLevels.size(); i++) {
            if (mAllCoins >= mLevels.get(i - 1).mCoins && mAllCoins < mLevels.get(i).mCoins) {
                return mLevels.get(i - 1).mName;
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
