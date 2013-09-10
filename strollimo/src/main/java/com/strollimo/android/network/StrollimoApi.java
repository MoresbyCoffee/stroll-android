package com.strollimo.android.network;

import com.google.gson.Gson;
import com.strollimo.android.StrollimoPreferences;
import com.strollimo.android.model.Mystery;
import com.strollimo.android.model.Secret;
import com.strollimo.android.network.request.*;
import com.strollimo.android.network.response.*;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
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

    public void getMysteries(String envTag, Callback<GetMysteriesResponse> callback) {
        GetMysteriesRequest request = new GetMysteriesRequest(mRequestHeader, true, envTag);
        service.getMysteries(request, callback);
    }

    public GetMysteriesResponse getMysteries(String envTag) throws RetrofitError {
        GetMysteriesRequest request = new GetMysteriesRequest(mRequestHeader, true, envTag);
        return service.getMysteries(request);
    }

    public void getSecrets(String mysteryId, Callback<GetSecretsResponse> callback) {
        GetSecretsRequest request = new GetSecretsRequest(mRequestHeader, mysteryId);
        service.getSecrets(request, callback);
    }

    public GetSecretsResponse getSecrets(String mysteryId) {
        GetSecretsRequest request = new GetSecretsRequest(mRequestHeader, mysteryId);
        return service.getSecrets(request);
    }

    public void updateMystery(Mystery mystery, Callback<UpdateMysteryResponse> callback) {
        UpdaterMysteryRequest request = new UpdaterMysteryRequest(mRequestHeader, mystery);
        service.updateMystery(request, callback);
    }

    public void updateSecret(Secret secret, Callback<UpdateSecretResponse> callback) {
        UpdaterSecretRequest request = new UpdaterSecretRequest(mRequestHeader, secret);
        service.updateSecret(request, callback);
    }

    public void pickupSecret(Secret secret, String capturedSecretUrl, Callback<PickupSecretResponse> callback) {
        PickupSecretRequest request = new PickupSecretRequest(mRequestHeader, secret.getId(), "imgComp", capturedSecretUrl);
        service.pickupSecret(request, callback);
    }
}
