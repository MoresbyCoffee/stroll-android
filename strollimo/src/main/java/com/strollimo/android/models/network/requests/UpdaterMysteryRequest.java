package com.strollimo.android.models.network.requests;

import com.google.gson.annotations.Expose;
import com.strollimo.android.models.Mystery;

public class UpdaterMysteryRequest extends BaseRequest {
    @Expose
    private Mystery body;

    public UpdaterMysteryRequest(RequestHeader header, Mystery mystery) {
        super(header, "updateAccomplishable");
        body = mystery;
    }
}
