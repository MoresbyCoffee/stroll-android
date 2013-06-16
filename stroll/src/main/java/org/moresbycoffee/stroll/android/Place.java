package org.moresbycoffee.stroll.android;

public class Place {
    public Place(int id, String title, double lat, double lon) {
        mId = id;
        mTitle = title;
        mLat = lat;
        mLon = lon;
    }
    public int mId;
    public double mLat;
    public double mLon;
    public String mTitle;
}
