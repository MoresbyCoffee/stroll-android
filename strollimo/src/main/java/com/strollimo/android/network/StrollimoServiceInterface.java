package com.strollimo.android.network;

import com.strollimo.android.network.request.*;
import com.strollimo.android.network.response.*;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

public interface StrollimoServiceInterface {
    @POST("/rest/accomplishables")
    void getMysteries(@Body GetMysteriesRequest body, Callback<GetMysteriesResponse> callback);

    @POST("/rest/accomplishables")
    GetMysteriesResponse getMysteries(@Body GetMysteriesRequest body);

    @POST("/rest/accomplishables")
    GetSecretsResponse getSecrets(@Body GetSecretsRequest body);

    @POST("/rest/accomplishables")
    PickupSecretResponse getPickupSecret(@Body PickupSecretRequest body);

    @POST("/rest/accomplishables")
    void getSecrets(@Body GetSecretsRequest body, Callback<GetSecretsResponse> callback);

    @POST("/rest/accomplishables")
    void updateMystery(@Body UpdaterMysteryRequest body, Callback<UpdateMysteryResponse> callback);

    @POST("/rest/accomplishables")
    void updateSecret(@Body UpdaterSecretRequest body, Callback<UpdateSecretResponse> callback);

    @POST("/rest/accomplishables")
    void pickupSecret(@Body PickupSecretRequest body, Callback<PickupSecretResponse> callback);

    @POST("/rest/accomplishables")
    void getPickupStatus(@Body GetPickupStatusRequest body, Callback<GetPickupStatusResponse> callback);
}
