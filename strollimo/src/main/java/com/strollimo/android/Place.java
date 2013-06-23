package com.strollimo.android;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.util.Random;

public class Place {
    public int mId;
    public double mLat;
    public double mLon;
    public String mTitle;
    public String mCode;
    public Drawable mImage;
    public int mCoinValue;

    public Place(int id, String title, double lat, double lon, String code, Drawable image) {
        mId = id;
        mTitle = title;
        mLat = lat;
        mLon = lon;
        mCode = code;
        mImage = image;
        mCoinValue = new Random().nextInt(3) + 1;
    }

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
