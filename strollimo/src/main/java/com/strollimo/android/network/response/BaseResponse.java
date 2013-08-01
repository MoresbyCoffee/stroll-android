package com.strollimo.android.network.response;

import com.google.gson.annotations.Expose;

public class BaseResponse {
    @Expose
    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
