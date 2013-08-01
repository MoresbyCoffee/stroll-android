package com.strollimo.android.network.response;

import com.google.gson.annotations.Expose;
import com.strollimo.android.model.Mystery;

import java.util.List;

public class GetMysteriesResponse extends BaseResponse {
    @Expose
    private List<Mystery> body;

    public List<Mystery> getBody() {
        return body;
    }

    public void setBody(List<Mystery> body) {
        this.body = body;
    }
}
