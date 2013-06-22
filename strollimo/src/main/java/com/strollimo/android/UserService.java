package com.strollimo.android;

import java.util.HashSet;
import java.util.Set;

public class UserService {
    private Set<Integer> mCapturedPlaces;

    public UserService() {
        mCapturedPlaces = new HashSet<Integer>();
    }

    public void capturePlace(int placeId) {
        mCapturedPlaces.add(placeId);
    }

    public boolean isPlaceCaptured(int placeId) {
        return mCapturedPlaces.contains(placeId);
    }
}
