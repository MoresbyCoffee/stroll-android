package com.strollimo.android.network.request;

import com.google.gson.annotations.Expose;
import com.strollimo.android.model.Mystery;

public class UpdaterMysteryRequest extends BaseRequest {
    @Expose
    private Mystery body;

    public UpdaterMysteryRequest(RequestHeader header, Mystery mystery) {
        super(header, "updateAccomplishable");
        body = mystery;
    }
}
