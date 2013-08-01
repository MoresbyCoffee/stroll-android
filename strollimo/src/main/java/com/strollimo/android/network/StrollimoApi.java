package com.strollimo.android.network;

import android.util.Log;
import com.google.gson.Gson;
import com.strollimo.android.StrollimoPreferences;
import com.strollimo.android.model.Mystery;
import com.strollimo.android.network.request.GetAccomplishablesRequest;
import com.strollimo.android.network.request.RequestHeader;
import com.strollimo.android.network.request.UpdateAccomplishableRequest;
import com.strollimo.android.network.response.GetAccomplishablesResponse;
import com.strollimo.android.network.response.UpdateAccomplishableResponse;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class StrollimoApi {
    public static final String ENDPOINT = "http://stroll.moresby.cloudbees.net";
    private StrollimoServiceInterface service;
    private RequestHeader mRequestHeader;
    private Gson mGson;

    public StrollimoApi(Gson gson, StrollimoPreferences prefs) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setServer(ENDPOINT)
                .setConverter(new GsonConverter(gson))
                .build();
        mGson = gson;
        service = restAdapter.create(StrollimoServiceInterface.class);
        String deviceId = prefs.getDeviceUUID();
        mRequestHeader = new RequestHeader(deviceId);
    }

    public void getAccomplishables(boolean topLevel, Callback<GetAccomplishablesResponse> callback) {
        GetAccomplishablesRequest request = new GetAccomplishablesRequest(mRequestHeader, topLevel);
        service.getAccomplisables(new GetAccomplishablesRequest(mRequestHeader, true), callback);
    }

    public void updateMystery(Mystery mystery, Callback<UpdateAccomplishableResponse> callback) {
        UpdateAccomplishableRequest request = new UpdateAccomplishableRequest(mRequestHeader, mystery);
        Log.i("BB", mGson.toJson(request));
        service.updateAccomplishable(request, callback);
    }

}
