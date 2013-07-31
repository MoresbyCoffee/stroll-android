package com.strollimo.android.network.request;

import com.strollimo.android.model.Mystery;

public class UpdateAccomplishableRequest extends BaseRequest {
    private Mystery body;

    public UpdateAccomplishableRequest(RequestHeader header, Mystery mystery) {
        super(header, "updateAccomplishable");
        body = mystery;
    }
}
