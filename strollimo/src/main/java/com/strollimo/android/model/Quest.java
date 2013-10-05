package com.strollimo.android.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Quest extends BaseAccomplishable {
    // TODO: change this on the server side
    public static final String TYPE = "quest";

    @Expose
    private List<String> children = new ArrayList<String>();

    public Quest(String id, String name, double lat, double lng, String imgUrl) {
        super(id, name, lat, lng, imgUrl, TYPE, true, PickupState.UNPICKED);
    }
}
