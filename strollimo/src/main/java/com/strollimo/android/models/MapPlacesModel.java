package com.strollimo.android.models;

import com.google.android.gms.maps.model.Marker;
import com.strollimo.android.core.AccomplishableController;
import com.strollimo.android.core.UserController;

import java.util.ArrayList;
import java.util.List;

public class MapPlacesModel {
    private AccomplishableController mAccomplishableController;
    private UserController mUserController;
    private MapPlace mSelectedMapPlace;
    private List<MapPlace> mMapPlaces;

    public MapPlacesModel(UserController userController, AccomplishableController accomplishableController) {
        mUserController = userController;
        mMapPlaces = new ArrayList<MapPlace>();
        mAccomplishableController = accomplishableController;
    }

    public void refreshSelectedMarker() {
        if (mSelectedMapPlace == null) {
            return;
        }
        if (isSelectedPlaceCaptured()) {
            // TODO: icon for captured place
            //getSelectedMarker().setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pink_flag));
        }
    }

    public void onMarkerClick(Marker marker) {
        Mystery mystery = getPlaceForMarker(marker);
        mSelectedMapPlace = new MapPlace(mystery, marker);
    }

    public void selectMapPlaceByPlace(Mystery mystery) {
        Marker marker = getMarkerForPlace(mystery);
        mSelectedMapPlace = new MapPlace(mystery, marker);
    }

    public Mystery getSelectedPlace() {
        return mSelectedMapPlace == null ? null : mSelectedMapPlace.getPlace();
    }

    public Marker getSelectedMarker() {
        return mSelectedMapPlace == null ? null : mSelectedMapPlace.getMarker();
    }

    public Mystery getPlaceForMarker(Marker marker) {
        if (marker == null) {
            return null;
        }

        for (MapPlace mapPlace : mMapPlaces) {
            if (marker.equals(mapPlace.getMarker())) {
                return mapPlace.getPlace();
            }
        }
        return null;
    }

    public Marker getMarkerForPlace(Mystery mystery) {
        if (mystery == null) {
            return null;
        }

        for (MapPlace mapPlace : mMapPlaces) {
            if (mystery.getId() == mapPlace.getPlace().getId()) {
                return mapPlace.getMarker();
            }
        }
        return null;
    }

    public boolean isSelectedPlaceCaptured() {
        return mAccomplishableController.isMysteryFinished(getSelectedPlace().getId());
    }

    public void clearSelectedPlace() {
        mSelectedMapPlace = null;
    }

    public void add(Mystery mystery, Marker marker) {
        mMapPlaces.add(new MapPlace(mystery, marker));
    }

}
