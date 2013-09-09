package com.strollimo.android.network;

import com.strollimo.android.network.request.GetMysteriesRequest;
import com.strollimo.android.network.request.GetSecretsRequest;
import com.strollimo.android.network.request.PickupSecretRequest;
import com.strollimo.android.network.request.UpdaterMysteryRequest;
import com.strollimo.android.network.request.UpdaterSecretRequest;
import com.strollimo.android.network.response.GetMysteriesResponse;
import com.strollimo.android.network.response.GetSecretsResponse;
import com.strollimo.android.network.response.PickupSecretResponse;
import com.strollimo.android.network.response.UpdateMysteryResponse;
import com.strollimo.android.network.response.UpdateSecretResponse;
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
    void getSecrets(@Body GetSecretsRequest body, Callback<GetSecretsResponse> callback);

    @POST("/rest/accomplishables")
    void updateMystery(@Body UpdaterMysteryRequest body, Callback<UpdateMysteryResponse> callback);

    @POST("/rest/accomplishables")
    void updateSecret(@Body UpdaterSecretRequest body, Callback<UpdateSecretResponse> callback);

    @POST("/rest/accomplishables")
    void pickupSecret(@Body PickupSecretRequest body, Callback<PickupSecretResponse> callback);
}
