package com.strollimo.android.model;

import com.google.android.gms.maps.model.Marker;
import com.strollimo.android.model.Mission;

public class MapPlace {
    private Marker mMarker;
    private Mission mMission;

    public MapPlace(Mission mission, Marker marker) {
        mMission = mission;
        mMarker = marker;
    }

    public Marker getMarker() {
        return mMarker;
    }

    public void setMarker(Marker marker) {
        this.mMarker = marker;
    }

    public Mission getPlace() {
        return mMission;
    }

    public void setPlace(Mission mission) {
        this.mMission = mission;
    }
}
