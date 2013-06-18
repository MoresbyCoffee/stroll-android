package org.moresbycoffee.stroll.android;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class Place {
    public Place(int id, String title, double lat, double lon, String code, Drawable image) {
        mId = id;
        mTitle = title;
        mLat = lat;
        mLon = lon;
        mCode = code;
        mImage = image;
    }

    public int mId;
    public double mLat;
    public double mLon;
    public String mTitle;
    public String mCode;
    public Drawable mImage;

    public Bitmap getBitmap() {
        return ((BitmapDrawable)mImage).getBitmap();
    }

    public boolean isScannedCodeValid(String scannedCode) {
        if (scannedCode == null) {
            return false;
        } else {
            return scannedCode.equals(mCode);
        }
    }
}
