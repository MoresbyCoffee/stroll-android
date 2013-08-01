package com.strollimo.android.network.request;

import com.google.gson.annotations.Expose;
import com.strollimo.android.model.Secret;

public class UpdaterSecretRequest extends BaseRequest {
    @Expose
    private Secret body;

    public UpdaterSecretRequest(RequestHeader header, Secret secret) {
        super(header, "updateAccomplishable");
        body = secret;
    }
}
