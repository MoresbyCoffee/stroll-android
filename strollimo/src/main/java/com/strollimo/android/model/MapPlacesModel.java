package com.strollimo.android.model;

import android.app.Activity;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.strollimo.android.R;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.controller.PlacesController;
import com.strollimo.android.controller.UserService;

import java.util.ArrayList;
import java.util.List;

public class MapPlacesModel {
    private UserService mUserService;
    private MapPlace mSelectedMapPlace;
    private List<MapPlace> mMapPlaces;

    public MapPlacesModel(UserService userService) {
        mUserService = userService;
        mMapPlaces = new ArrayList<MapPlace>();
    }

    public void refreshSelectedMarker() {
        if (mSelectedMapPlace == null) {
            return;
        }
        if (isSelectedPlaceCaptured()) {
            getSelectedMarker().setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pink_flag));
        }
        getSelectedMarker().showInfoWindow();
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
        if (getSelectedPlace() != null && mUserService.isPlaceCaptured(getSelectedPlace().getId())) {
            return true;
        } else {
            return false;
        }
    }

    public void hideSelectedPlace() {
        mSelectedMapPlace = null;
    }

    public void add(Mystery mystery, Marker marker) {
        mMapPlaces.add(new MapPlace(mystery, marker));
    }

    public Mystery getNextPlaceFor(Activity activity, Mystery mystery) {
        if (mystery == null) {
            return null;
        }

        String currentId = mystery.getId();
        PlacesController placesController = StrollimoApplication.getService(PlacesController.class);
        return placesController.getMysteryById(currentId);
    }

    public Mystery getPreviousPlaceFor(Activity activity, Mystery mystery) {
        if (mystery == null) {
            return null;
        }

        String currentId = mystery.getId();
        PlacesController placesController = StrollimoApplication.getService(PlacesController.class);
        return placesController.getMysteryById(currentId);
    }
}
