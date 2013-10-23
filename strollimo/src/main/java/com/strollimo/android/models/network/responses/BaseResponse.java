package com.strollimo.android.models.network.responses;

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
