package org.moresbycoffee.stroll.android;

public class Place {
    public Place(int id, String title, double lat, double lon, String code) {
        mId = id;
        mTitle = title;
        mLat = lat;
        mLon = lon;
        mCode = code;
    }
    public int mId;
    public double mLat;
    public double mLon;
    public String mTitle;
    public String mCode;

    public boolean isScannedCodeValid(String scannedCode) {
        if (scannedCode == null) {
            return false;
        } else {
            return scannedCode.equals(mCode);
        }
    }
}
