package com.strollimo.android.models.network.requests;

import com.google.gson.annotations.Expose;

public class PickupSecretRequest extends BaseRequest {
    @Expose
    private RequestBody body;

    public PickupSecretRequest(RequestHeader header, String id, String type, String url) {
        super(header, "pickup");
        this.body = new RequestBody(id, type, url);
    }

    private static class RequestBody {
        @Expose
        private String id;
        @Expose
        private String type;
        @Expose
        private String url;

        public RequestBody(String id, String Type, String url) {
            this.id = id;
            this.type = type;
            this.url = url;
        }
    }
}
