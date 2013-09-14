package com.strollimo.android.network.response;

import com.google.gson.annotations.Expose;
import com.strollimo.android.model.BaseAccomplishable;

import java.util.HashMap;
import java.util.Map;

public class GetPickupStatusResponse extends BaseResponse {
    @Expose
    private Map<String, String> body;

    public Map<String, BaseAccomplishable.PickupState> getSecretStatuses() {
        Map<String, BaseAccomplishable.PickupState> result = new HashMap<String, BaseAccomplishable.PickupState>();
        if (body != null) {
            for (String secretId : body.keySet()) {
                result.put(secretId, BaseAccomplishable.PickupState.valueOf(body.get(secretId)));
            }
        }
        return result;
    }
}
