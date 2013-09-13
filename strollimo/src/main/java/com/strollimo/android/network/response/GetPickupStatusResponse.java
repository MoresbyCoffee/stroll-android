package com.strollimo.android.network.response;

import com.google.gson.annotations.Expose;
import com.strollimo.android.model.BaseAccomplishable;

import java.util.HashMap;
import java.util.Map;

public class GetPickupStatusResponse extends BaseResponse {
    @Expose
    private Map<String, String> body;

    public Map<String, BaseAccomplishable.Status> getSecretStatuses() {
        Map<String, BaseAccomplishable.Status> result = new HashMap<String, BaseAccomplishable.Status>();
        if (body != null) {
            for (String secretId : body.keySet()) {
                result.put(secretId, BaseAccomplishable.Status.valueOf(body.get(secretId)));
            }
        }
        return result;
    }
}
