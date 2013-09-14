package com.strollimo.android.network.response;

import com.google.gson.annotations.Expose;
import com.strollimo.android.model.BaseAccomplishable;

import java.util.Locale;

public class PickupSecretResponse extends BaseResponse {
    @Expose
    private String body;

    public BaseAccomplishable.PickupState getSecretStatus() {
        try {
            return BaseAccomplishable.PickupState.valueOf(body.toUpperCase(Locale.getDefault()));
        } catch (Exception e) {
            return null;
        }
    }
}
