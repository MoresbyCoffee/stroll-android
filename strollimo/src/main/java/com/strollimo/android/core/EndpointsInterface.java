package com.strollimo.android.core;

import com.strollimo.android.models.network.requests.GetMysteriesRequest;
import com.strollimo.android.models.network.responses.GetMysteriesResponse;
import com.strollimo.android.models.network.requests.GetPickupStatusRequest;
import com.strollimo.android.models.network.responses.GetPickupStatusResponse;
import com.strollimo.android.models.network.requests.GetSecretsRequest;
import com.strollimo.android.models.network.responses.GetSecretsResponse;
import com.strollimo.android.models.network.requests.PickupSecretRequest;
import com.strollimo.android.models.network.responses.PickupSecretResponse;
import com.strollimo.android.models.network.responses.UpdateMysteryResponse;
import com.strollimo.android.models.network.responses.UpdateSecretResponse;
import com.strollimo.android.models.network.requests.UpdaterMysteryRequest;
import com.strollimo.android.models.network.requests.UpdaterSecretRequest;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

public interface EndpointsInterface {
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
