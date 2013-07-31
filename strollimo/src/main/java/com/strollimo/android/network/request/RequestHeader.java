package com.strollimo.android.network.request;

public class RequestHeader {
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
