package com.strollimo.android.model;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Mystery {
    private String mId;
    private double mLat;
    private double mLon;
    private String mTitle;
    private String mCode;
    private Drawable mImage;
    private int mCoinValue;
    private List<Secret> mSecrets = new ArrayList<Secret>();

    public Mystery(String id, String title, double lat, double lon) {
        mId = id;
        mTitle = title;
        mLat = lat;
        mLon = lon;
    }

    public Mystery(String id, String title, double lat, double lon, String code, Drawable image) {
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

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public double getLat() {
        return mLat;
    }

    public void setLat(double mLat) {
        this.mLat = mLat;
    }

    public double getLon() {
        return mLon;
    }

    public void setLon(double mLon) {
        this.mLon = mLon;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String mCode) {
        this.mCode = mCode;
    }

    public Drawable getImage() {
        return mImage;
    }

    public void setImage(Drawable mImage) {
        this.mImage = mImage;
    }

    public int getCoinValue() {
        return mCoinValue;
    }

    public void setCoinValue(int mCoinValue) {
        this.mCoinValue = mCoinValue;
    }

    public List<Secret> getSecrets() {
        return mSecrets;
    }

    public void addSecret(Secret secret) {
        mSecrets.add(secret);
    }

}
