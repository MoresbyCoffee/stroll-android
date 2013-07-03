package com.strollimo.android;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.util.Random;

public class Place {
    private int mId;
    private double mLat;
    private double mLon;
    private String mTitle;
    private String mCode;
    private Drawable mImage;
    private int mCoinValue;

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

    public int getId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public double getmLat() {
        return mLat;
    }

    public void setmLat(double mLat) {
        this.mLat = mLat;
    }

    public double getmLon() {
        return mLon;
    }

    public void setmLon(double mLon) {
        this.mLon = mLon;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmCode() {
        return mCode;
    }

    public void setmCode(String mCode) {
        this.mCode = mCode;
    }

    public Drawable getmImage() {
        return mImage;
    }

    public void setmImage(Drawable mImage) {
        this.mImage = mImage;
    }

    public int getmCoinValue() {
        return mCoinValue;
    }

    public void setmCoinValue(int mCoinValue) {
        this.mCoinValue = mCoinValue;
    }
}
