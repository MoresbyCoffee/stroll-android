package com.strollimo.android.network;

import com.google.gson.Gson;
import com.strollimo.android.network.request.GetAccomplishablesRequest;
import com.strollimo.android.network.request.RequestHeader;
import com.strollimo.android.network.response.GetAccomplishablesResponse;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.POST;

public class RetrofitTest {
    StrollimoService service;
    private RequestHeader mRequestHeader;

    public RetrofitTest() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setServer("http://stroll.moresby.cloudbees.net")
                .setConverter(new GsonConverter(new Gson()))
                .build();


        service = restAdapter.create(StrollimoService.class);
        mRequestHeader = new RequestHeader("android");
    }

    public void call() {
        service.getAccomplisables(new GetAccomplishablesRequest(mRequestHeader, true), new Callback<GetAccomplishablesResponse>() {

            @Override
            public void success(GetAccomplishablesResponse topLevelResponse, Response response) {

            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }

    public interface StrollimoService {
        @POST("/rest/accomplishables")
        GetAccomplishablesResponse getAccomplisables(@Body GetAccomplishablesRequest body, Callback<GetAccomplishablesResponse> callback);
    }

//    {
//        "header" : { "deviceId" : "BarnysComputer" },
//        "action" : "getAccomplishables",
//            "body" : {
//        "topLevel" : true
//    }
}
