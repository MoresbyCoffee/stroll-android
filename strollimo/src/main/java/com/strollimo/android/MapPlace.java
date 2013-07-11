package com.strollimo.android;

import com.google.android.gms.maps.model.Marker;
import com.strollimo.android.model.Place;

public class MapPlace {
    private Marker mMarker;
    private Place mPlace;

    public MapPlace(Place place, Marker marker) {
        mPlace = place;
        mMarker = marker;
    }

    public Marker getMarker() {
        return mMarker;
    }

    public void setMarker(Marker marker) {
        this.mMarker = marker;
    }

    public Place getPlace() {
        return mPlace;
    }

    public void setPlace(Place place) {
        this.mPlace = place;
    }
}
