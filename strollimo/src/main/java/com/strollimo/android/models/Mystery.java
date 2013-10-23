package com.strollimo.android.models;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Mystery extends BaseAccomplishable {
    // TODO: change this on the server side
    public static final String TYPE = "mission";

    @Expose
    private List<String> children = new ArrayList<String>();

    private List<Secret> secrets = new ArrayList<Secret>();
    private String mCode;
    private int mCoinValue;

    public Mystery(String id, String name, double lat, double lng) {
        this(id, name, lat, lng, null);
    }

    public Mystery(String id, String name, double lat, double lng, String imgUrl) {
        super(id, name, lat, lng, imgUrl, TYPE, true, PickupState.UNPICKED);
        mCoinValue = new Random().nextInt(3) + 1;
    }

    public boolean isScannedCodeValid(String scannedCode) {
        if (scannedCode == null) {
            return false;
        } else {
            return scannedCode.equals(mCode);
        }
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String mCode) {
        this.mCode = mCode;
    }

    public int getCoinValue() {
        return mCoinValue;
    }

    public void setCoinValue(int mCoinValue) {
        this.mCoinValue = mCoinValue;
    }

    public void addChild(String secretId) {
        if (!children.contains(secretId)) {
            children.add(secretId);
        }
    }

    public List<String> getChildren() {
        return this.children;
    }

}
