package com.strollimo.android.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class BaseAccomplishable {
    @Expose
    protected String id;
    @Expose
    protected String name;
    @Expose
    protected Location loc;
    @Expose
    protected String imgUrl;
    @Expose
    protected boolean topLevel;
    @Expose
    protected Status status;
    @Expose
    private String shortDesc;
    @Expose
    private List<String> envTags = new ArrayList<String>();
    @Expose
    private String type;

    public BaseAccomplishable(String id, String name, double lat, double lng, String imgUrl, String type, boolean topLevel, Status status) {
        this.id = id;
        this.name = name;
        this.loc = new Location(lat, lng);
        this.topLevel = topLevel;
        this.imgUrl = imgUrl;
        this.type = type;
        this.status = status;
    }

    public BaseAccomplishable(String id, String name, String type, boolean topLevel) {
        this(id, name, 0, 0, null, type, topLevel, Status.UNPICKED);
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

    public Location getLocation() {
        return loc;
    }

    public void setLocation(Location loc) {
        this.loc = loc;
    }

    public List<String> getEnvTags() {
        return envTags;
    }

    public void addEnvTag(String envTag) {
        envTags.add(envTag);
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public boolean isTopLevel() {
        return topLevel;
    }

    public void setTopLevel(boolean topLevel) {
        this.topLevel = topLevel;
    }

    public String getType() {
        return type;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        UNPICKED,
        IN_PROGRESS,
        PENDING,
        REJECTED,
        ACCOMPLISHED;
    }
}
