package com.strollimo.android.models.network.responses;

import com.google.gson.annotations.Expose;
import com.strollimo.android.models.Secret;

import java.util.List;

public class GetSecretsResponse extends BaseResponse {
    @Expose
    private List<Secret> body;

    public List<Secret> getBody() {
        return body;
    }

    public void setBody(List<Secret> body) {
        this.body = body;
    }
}
