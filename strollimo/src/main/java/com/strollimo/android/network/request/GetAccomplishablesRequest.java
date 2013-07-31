package com.strollimo.android.network.request;

public class GetAccomplishablesRequest extends BaseRequest {
    private RequestBody body;

    public GetAccomplishablesRequest(RequestHeader header, boolean topLevel) {
        super(header, "getAccomplishables");
        this.body = new RequestBody(topLevel);
    }

    private static class RequestBody {
        private boolean topLevel;

        public RequestBody(boolean topLevel) {
            this.topLevel = topLevel;
        }
    }
}
