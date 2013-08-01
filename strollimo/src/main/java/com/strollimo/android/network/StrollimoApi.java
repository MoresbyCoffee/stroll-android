package com.strollimo.android.network;

import com.google.gson.Gson;
import com.strollimo.android.StrollimoPreferences;
import com.strollimo.android.model.Mystery;
import com.strollimo.android.model.Secret;
import com.strollimo.android.network.request.*;
import com.strollimo.android.network.response.GetMysteriesResponse;
import com.strollimo.android.network.response.GetSecretsResponse;
import com.strollimo.android.network.response.UpdateMysteryResponse;
import com.strollimo.android.network.response.UpdateSecretResponse;
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

    public void getMysteries(Callback<GetMysteriesResponse> callback) {
        GetMysteriesRequest request = new GetMysteriesRequest(mRequestHeader, true);
        service.getMysteries(new GetMysteriesRequest(mRequestHeader, true), callback);
    }

    public void getSecrets(String mysteryId, Callback<GetSecretsResponse> callback) {
        GetSecretsRequest request = new GetSecretsRequest(mRequestHeader, mysteryId);
        service.getSecrets(request, callback);
    }

    public void updateMystery(Mystery mystery, Callback<UpdateMysteryResponse> callback) {
        UpdaterMysteryRequest request = new UpdaterMysteryRequest(mRequestHeader, mystery);
        service.updateMystery(request, callback);
    }

    public void updateSecret(Secret secret, Callback<UpdateSecretResponse> callback) {
        UpdaterSecretRequest request = new UpdaterSecretRequest(mRequestHeader, secret);
        service.updateSecret(request, callback);
    }
}
