package com.strollimo.android;

import android.app.Activity;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.strollimo.android.controller.PlacesController;
import com.strollimo.android.model.Place;

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
        Place place = getPlaceForMarker(marker);
        mSelectedMapPlace = new MapPlace(place, marker);
    }

    public void selectMapPlaceByPlace(Place place) {
        Marker marker = getMarkerForPlace(place);
        mSelectedMapPlace = new MapPlace(place, marker);
    }

    public Place getSelectedPlace() {
        return mSelectedMapPlace == null ? null : mSelectedMapPlace.getPlace();
    }

    public Marker getSelectedMarker() {
        return mSelectedMapPlace == null ? null : mSelectedMapPlace.getMarker();
    }

    public Place getPlaceForMarker(Marker marker) {
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

    public Marker getMarkerForPlace(Place place) {
        if (place == null) {
            return null;
        }

        for (MapPlace mapPlace : mMapPlaces) {
            if (place.getId() == mapPlace.getPlace().getId()) {
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

    public void add(Place place, Marker marker) {
        mMapPlaces.add(new MapPlace(place, marker));
    }

    public Place getNextPlaceFor(Activity activity, Place place) {
        if (place == null) {
            return null;
        }

        int currentId = place.getId();
        PlacesController placesController = ((StrollimoApplication) activity.getApplication()).getService(PlacesController.class);
        if (currentId >= placesController.getPlacesCount()) {
            return null;
        }
        return placesController.getPlaceById(currentId + 1);
    }

    public Place getPreviousPlaceFor(Activity activity, Place place) {
        if (place == null) {
            return null;
        }

        int currentId = place.getId();
        PlacesController placesController = ((StrollimoApplication) activity.getApplication()).getService(PlacesController.class);
        if (currentId <= 1) {
            return null;
        }
        return placesController.getPlaceById(currentId - 1);
    }
}
