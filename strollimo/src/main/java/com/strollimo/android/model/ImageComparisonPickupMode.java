package com.strollimo.android.model;

import java.util.ArrayList;
import java.util.List;

public class ImageComparisonPickupMode implements PickupMode {
    public final static String TYPE = "imgComp";

    private String type = TYPE;
    private List<String> urls = new ArrayList<String>();

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public void addUrl(String url) {
        urls.add(url);
    }

    public String getType() {
        return type;
    }

}
