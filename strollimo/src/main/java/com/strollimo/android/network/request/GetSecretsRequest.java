package com.strollimo.android.network.request;

import com.google.gson.annotations.Expose;

public class GetSecretsRequest extends BaseRequest {
    @Expose
    private RequestBody body;

    public GetSecretsRequest(RequestHeader header, String childrenOf) {
        super(header, "getAccomplishables");
        this.body = new RequestBody(childrenOf);
    }

    private static class RequestBody {
        @Expose
        private String childrenOf;

        public RequestBody(String childrenOf) {
            this.childrenOf = childrenOf;
        }
    }
}
