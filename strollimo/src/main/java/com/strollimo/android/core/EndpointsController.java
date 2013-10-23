package com.strollimo.android.core;

import com.google.gson.Gson;
import com.strollimo.android.models.Mystery;
import com.strollimo.android.models.Secret;
import com.strollimo.android.models.network.requests.GetMysteriesRequest;
import com.strollimo.android.models.network.responses.GetMysteriesResponse;
import com.strollimo.android.models.network.requests.GetPickupStatusRequest;
import com.strollimo.android.models.network.responses.GetPickupStatusResponse;
import com.strollimo.android.models.network.requests.GetSecretsRequest;
import com.strollimo.android.models.network.responses.GetSecretsResponse;
import com.strollimo.android.models.network.requests.PickupSecretRequest;
import com.strollimo.android.models.network.responses.PickupSecretResponse;
import com.strollimo.android.models.network.requests.RequestHeader;
import com.strollimo.android.models.network.responses.UpdateMysteryResponse;
import com.strollimo.android.models.network.responses.UpdateSecretResponse;
import com.strollimo.android.models.network.requests.UpdaterMysteryRequest;
import com.strollimo.android.models.network.requests.UpdaterSecretRequest;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;

import java.util.List;

public class EndpointsController {
    private EndpointsInterface service;
    private RequestHeader mRequestHeader;
    private Gson mGson;
    private PreferencesController mPrefs;

    public EndpointsController(Gson gson, PreferencesController prefs) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setServer(prefs.getStrollimoUrl())
                .setConverter(new GsonConverter(gson))
                .build();
        mGson = gson;
        mPrefs = prefs;
        service = restAdapter.create(EndpointsInterface.class);
        String deviceId = prefs.getDeviceUUID();
        mRequestHeader = new RequestHeader(deviceId);
    }

    public void reset(PreferencesController.SystemEnv env) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setServer(env.getUrl())
                .setConverter(new GsonConverter(mGson))
                .build();
        service = restAdapter.create(EndpointsInterface.class);
        String deviceId = mPrefs.getDeviceUUID();
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

    public PickupSecretResponse getPickupSecret(String secretId, String capturedSecretUrl) {
        PickupSecretRequest request = new PickupSecretRequest(mRequestHeader, secretId, "imgComp", capturedSecretUrl);
        return service.getPickupSecret(request);
    }

    public void getPickupStatus(List<String> secretsIds, Callback<GetPickupStatusResponse> callback) {
        GetPickupStatusRequest request = new GetPickupStatusRequest (mRequestHeader, secretsIds);
        service.getPickupStatus(request, callback);
    }
}
