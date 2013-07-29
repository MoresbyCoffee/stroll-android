package com.strollimo.android.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Secret {
    @Expose
    private String name;
    @Expose
    private String shortDesc;
    @Expose
    private List<PickupMode> pickupModes = new ArrayList<PickupMode>();
    @Expose
    private String imgUrl;
    @Expose
    private String id;
    @Expose
    private String type;
    @Expose
    private Location loc;

    public Secret(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public List<PickupMode> getPickupModes() {
        return pickupModes;
    }

    public void addPickupMode(PickupMode pickupMode) {
        pickupModes.add(pickupMode);
    }

    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

}
