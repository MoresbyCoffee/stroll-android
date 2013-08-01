package com.strollimo.android.network.request;

import com.google.gson.annotations.Expose;

public class GetMysteriesRequest extends BaseRequest {
    @Expose
    private RequestBody body;

    public GetMysteriesRequest(RequestHeader header, boolean topLevel) {
        super(header, "getAccomplishables");
        this.body = new RequestBody(topLevel);
    }

    private static class RequestBody {
        @Expose
        private boolean topLevel;

        public RequestBody(boolean topLevel) {
            this.topLevel = topLevel;
        }
    }
}
