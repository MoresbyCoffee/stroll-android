package com.strollimo.android.network;

import com.strollimo.android.network.request.GetAccomplishablesRequest;
import com.strollimo.android.network.request.UpdateAccomplishableRequest;
import com.strollimo.android.network.response.GetAccomplishablesResponse;
import com.strollimo.android.network.response.UpdateAccomplishableResponse;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

public interface StrollimoServiceInterface {
    @POST("/rest/accomplishables")
    void getAccomplisables(@Body GetAccomplishablesRequest body, Callback<GetAccomplishablesResponse> callback);

    @POST("/rest/accomplishables")
    void updateAccomplishable(@Body UpdateAccomplishableRequest body, Callback<UpdateAccomplishableResponse> callback);
}
