package com.strollimo.android.network.request;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class GetMysteriesRequest extends BaseRequest {
    @Expose
    private RequestBody body;

    public GetMysteriesRequest(RequestHeader header, boolean topLevel, String envTag) {
        super(header, "getAccomplishables");
        this.body = new RequestBody(topLevel, envTag);
    }

    private static class RequestBody {
        @Expose
        private boolean topLevel;
        @Expose
        List<String> envTags = new ArrayList<String>();

        public RequestBody(boolean topLevel, String envTag) {
            this.topLevel = topLevel;
            this.envTags.add(envTag);
        }
    }
}
