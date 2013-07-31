package com.strollimo.android.network;

import com.google.gson.Gson;
import com.strollimo.android.model.Mystery;
import com.strollimo.android.network.request.GetAccomplishablesRequest;
import com.strollimo.android.network.request.RequestHeader;
import com.strollimo.android.network.request.UpdateAccomplishableRequest;
import com.strollimo.android.network.response.GetAccomplishablesResponse;
import com.strollimo.android.network.response.UpdateAccomplishableResponse;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class RetrofitTest {
    StrollimoServiceInterface service;
    private RequestHeader mRequestHeader;

    public RetrofitTest() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setServer("http://stroll.moresby.cloudbees.net")
                .setConverter(new GsonConverter(new Gson()))
                .build();


        service = restAdapter.create(StrollimoServiceInterface.class);
        mRequestHeader = new RequestHeader("android");
    }

    public void getAccomplishables(boolean topLevel, Callback<GetAccomplishablesResponse> callback) {
        GetAccomplishablesRequest request = new GetAccomplishablesRequest(mRequestHeader, topLevel);
        service.getAccomplisables(new GetAccomplishablesRequest(mRequestHeader, true), callback);
    }

    public void updateMystery(Mystery mystery, Callback<UpdateAccomplishableResponse> callback) {
        UpdateAccomplishableRequest request = new UpdateAccomplishableRequest(mRequestHeader, mystery);
        service.updateAccomplishable(request, callback);
    }

}
