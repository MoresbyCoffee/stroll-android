package com.strollimo.android.models.network.requests;

import com.google.gson.annotations.Expose;

public class RequestHeader {
    @Expose
    private String deviceId;

    public RequestHeader(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
