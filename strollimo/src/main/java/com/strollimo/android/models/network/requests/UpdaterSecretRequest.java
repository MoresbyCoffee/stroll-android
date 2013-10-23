package com.strollimo.android.models.network.requests;

import com.google.gson.annotations.Expose;
import com.strollimo.android.models.Secret;

public class UpdaterSecretRequest extends BaseRequest {
    @Expose
    private Secret body;

    public UpdaterSecretRequest(RequestHeader header, Secret secret) {
        super(header, "updateAccomplishable");
        body = secret;
    }
}
