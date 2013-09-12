package com.strollimo.android.network.response;

import com.google.gson.annotations.Expose;
import com.strollimo.android.model.BaseAccomplishable;

import java.util.Locale;

public class PickupSecretResponse extends BaseResponse {
    @Expose
    private String body;

    public BaseAccomplishable.Status getSecretStatus() {
        try {
            return BaseAccomplishable.Status.valueOf(body.toUpperCase(Locale.getDefault()));
        } catch (Exception e) {
            return null;
        }
    }
}
