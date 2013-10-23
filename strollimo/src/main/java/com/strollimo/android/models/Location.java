package com.strollimo.android.models;

import com.google.gson.annotations.Expose;

public class Location {
    @Expose
    private double lat;
    @Expose
    private double lng;
    @Expose
    private double radius;

    public Location(double lat, double lng) {
        this(lat, lng, 0.0);
    }

    public Location(double lat, double lng, double radius) {
        this.lat = lat;
        this.lng = lng;
        this.radius = radius;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
