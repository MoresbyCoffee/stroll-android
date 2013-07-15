package com.strollimo.android.model;

import android.app.Activity;
import android.content.Context;
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
        Mission mission = getPlaceForMarker(marker);
        mSelectedMapPlace = new MapPlace(mission, marker);
    }

    public void selectMapPlaceByPlace(Mission mission) {
        Marker marker = getMarkerForPlace(mission);
        mSelectedMapPlace = new MapPlace(mission, marker);
    }

    public Mission getSelectedPlace() {
        return mSelectedMapPlace == null ? null : mSelectedMapPlace.getPlace();
    }

    public Marker getSelectedMarker() {
        return mSelectedMapPlace == null ? null : mSelectedMapPlace.getMarker();
    }

    public Mission getPlaceForMarker(Marker marker) {
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

    public Marker getMarkerForPlace(Mission mission) {
        if (mission == null) {
            return null;
        }

        for (MapPlace mapPlace : mMapPlaces) {
            if (mission.getId() == mapPlace.getPlace().getId()) {
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

    public void add(Mission mission, Marker marker) {
        mMapPlaces.add(new MapPlace(mission, marker));
    }

    public Mission getNextPlaceFor(Activity activity, Mission mission) {
        if (mission == null) {
            return null;
        }

        int currentId = mission.getId();
        PlacesController placesController = StrollimoApplication.getService(PlacesController.class);
        if (currentId >= placesController.getPlacesCount()) {
            return null;
        }
        return placesController.getPlaceById(currentId + 1);
    }

    public Mission getPreviousPlaceFor(Activity activity, Mission mission) {
        if (mission == null) {
            return null;
        }

        int currentId = mission.getId();
        PlacesController placesController = StrollimoApplication.getService(PlacesController.class);
        if (currentId <= 1) {
            return null;
        }
        return placesController.getPlaceById(currentId - 1);
    }
}
