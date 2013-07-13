package com.strollimo.android.model;

import java.util.List;

public class ImageComparisonPickupMode implements PickupMode {
    private List<String> mPickupImages;

    public List<String> getPickupImages() {
        return mPickupImages;
    }

    public void setPickupImages(List<String> mPickupImages) {
        this.mPickupImages = mPickupImages;
    }
}
