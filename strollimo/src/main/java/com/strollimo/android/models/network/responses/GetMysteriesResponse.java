package com.strollimo.android.models.network.responses;

import com.google.gson.annotations.Expose;
import com.strollimo.android.models.Mystery;

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
