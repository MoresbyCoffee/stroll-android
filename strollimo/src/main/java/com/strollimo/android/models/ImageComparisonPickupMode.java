package com.strollimo.android.models;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class ImageComparisonPickupMode implements PickupMode {
    public final static String TYPE = "imgComp";

    @Expose
    private String type = TYPE;
    @Expose
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
