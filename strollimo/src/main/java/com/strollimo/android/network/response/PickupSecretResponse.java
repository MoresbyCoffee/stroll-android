package com.strollimo.android.network.response;

import com.google.gson.annotations.Expose;
import com.strollimo.android.model.Secret;

import java.util.List;
import java.util.Locale;

public class PickupSecretResponse extends BaseResponse {
    @Expose
    private String body;

    public Secret.SecretStatus getSecretStatus() {
        try {
            return Secret.SecretStatus.valueOf(body.toUpperCase(Locale.getDefault()));
        } catch (Exception e) {
            return null;
        }
    }
}
