package com.strollimo.android.network.request;

public class BaseRequest {
    private RequestHeader header;
    private String action;

    public BaseRequest(RequestHeader header, String action) {
        this.header = header;
        this.action = action;
    }
}
