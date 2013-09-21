package com.strollimo.android.network.request;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class GetPickupStatusRequest extends BaseRequest {
    @Expose
    private List<String> body ;

    public GetPickupStatusRequest(RequestHeader header, List<String> secretIds) {
        super(header, "getPickupStates");
        this.body = (secretIds == null ? new ArrayList<String>() : secretIds);
    }
}
