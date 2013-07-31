package com.strollimo.android.network.request;

public class GetAccomplishablesRequest {
    private RequestHeader header;
    private String action = "getAccomplishables";
    private RequestBody body;

    public GetAccomplishablesRequest(RequestHeader header, boolean topLevel) {
        this.header = header;
        this.body = new RequestBody(topLevel);
    }

    private static class RequestBody {
        private boolean topLevel;
        public RequestBody(boolean topLevel) {
            this.topLevel = topLevel;
        }
    }
}
