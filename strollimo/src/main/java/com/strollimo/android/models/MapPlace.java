package com.strollimo.android.models;

import com.google.android.gms.maps.model.Marker;

public class MapPlace {
    private Marker mMarker;
    private Mystery mMystery;

    public MapPlace(Mystery mystery, Marker marker) {
        mMystery = mystery;
        mMarker = marker;
    }

    public Marker getMarker() {
        return mMarker;
    }

    public void setMarker(Marker marker) {
        this.mMarker = marker;
    }

    public Mystery getPlace() {
        return mMystery;
    }

    public void setPlace(Mystery mystery) {
        this.mMystery = mystery;
    }
}
