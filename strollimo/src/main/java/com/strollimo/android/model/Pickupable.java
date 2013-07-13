package com.strollimo.android.model;

import java.util.ArrayList;
import java.util.List;

public class Pickupable {
    private String mType;
    private String mId;
    private String mName;
    private String mShortDesc;
    private String mImageUrl;
    private List<PickupMode> mPickupModes = new ArrayList<PickupMode>();

    public String getmType() {
        return mType;
    }

    public void setType(String mType) {
        this.mType = mType;
    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getShortDesc() {
        return mShortDesc;
    }

    public void setShortDesc(String mShortDesc) {
        this.mShortDesc = mShortDesc;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public List<PickupMode> getPickupModes() {
        return mPickupModes;
    }

    public void addPickupMode(PickupMode pickupMode) {
        mPickupModes.add(pickupMode);
    }
}
