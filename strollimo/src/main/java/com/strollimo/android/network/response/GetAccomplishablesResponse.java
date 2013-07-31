package com.strollimo.android.network.response;

import com.strollimo.android.model.Mystery;

import java.util.List;

public class GetAccomplishablesResponse {
    private String state;
    private List<Mystery> body;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<Mystery> getBody() {
        return body;
    }

    public void setBody(List<Mystery> body) {
        this.body = body;
    }
}
