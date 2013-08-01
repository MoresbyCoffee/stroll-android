package com.strollimo.android.network.request;

import com.google.gson.annotations.Expose;
import com.strollimo.android.model.Mystery;

public class UpdateAccomplishableRequest extends BaseRequest {
    @Expose
    private Mystery body;

    public UpdateAccomplishableRequest(RequestHeader header, Mystery mystery) {
        super(header, "updateAccomplishable");
        body = mystery;
    }
}
