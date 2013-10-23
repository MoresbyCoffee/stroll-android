package com.strollimo.android.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.strollimo.android.models.BaseAccomplishable;
import com.strollimo.android.models.Mystery;
import com.strollimo.android.models.Secret;
import com.strollimo.android.models.network.AmazonUrl;
import com.strollimo.android.models.network.responses.GetMysteriesResponse;
import com.strollimo.android.models.network.responses.GetSecretsResponse;
import com.strollimo.android.models.network.responses.UpdateMysteryResponse;
import com.strollimo.android.models.network.responses.UpdateSecretResponse;
import com.strollimo.android.ui.activities.MainActivity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AccomplishableController {
    private static final String TAG = AccomplishableController.class.getSimpleName();

    private final PreferencesController mPrefs;
    private final PhotoUploadController mPhotoUploadController;
    private final EndpointsController mEndpointsController;
    private Map<String, Mystery> mMysteriesMapById;
    private Context mContext;
    private LinkedHashMap<String, Secret> mSecrets = new LinkedHashMap<String, Secret>();

    public AccomplishableController(Context context, PreferencesController prefs,
                                    PhotoUploadController photoUploadController, EndpointsController endpointsController) {
        mContext = context;
        mMysteriesMapById = new LinkedHashMap<String, Mystery>();
        mPrefs = prefs;
        mPhotoUploadController = photoUploadController;
        mEndpointsController = endpointsController;
        preloadPlaces();
    }

    public void preloadPlaces() {
        List<Mystery> mysteries = mPrefs.getMysteries();
        if (mysteries != null) {
            for (Mystery mystery : mysteries) {
                addMystery(mystery);
                Mystery myMystery = getMysteryById(mystery.getId());
                for (String secretId : mystery.getChildren()) {
                    Secret secret = mPrefs.getSecret(secretId);
                    if (secret == null) {
                        Log.e(TAG, "Error - secret is not available: " + secretId);
                    } else {
                        myMystery.addChild(secretId);
                        addSecret(secret, myMystery);
                    }
                }
            }
        }
    }


    public void asyncSyncMysteries(final String env, final OperationCallback callback) {
        new AsyncTask<Void, Void, List<Mystery>>(){

            @Override
            protected List<Mystery> doInBackground(Void... voids) {
                try {
                    GetMysteriesResponse getMysteriesResponse = mEndpointsController.getMysteries(env);
                    if (getMysteriesResponse != null && "success".equals(getMysteriesResponse.getState())) {
                        for (Mystery mystery : getMysteriesResponse.getBody()) {
                            addMystery(mystery);
                            syncSecrets(mystery);
                            saveAllData();

                        }
                        return getMysteriesResponse.getBody();
                    } else {
                    }
                    return null;
                } catch (RetrofitError ex) {
                    Log.e(TAG, "Error getting the mysteries", ex);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Mystery> mysteries) {
                super.onPostExecute(mysteries);
                if (mysteries == null) {
                    if (callback != null) {
                        callback.onError("Can't download mysteries");
                    }
                    return;
                }
                if (callback != null) {
                    callback.onSuccess();
                }
            }
        }.execute();
    }


    private void syncSecrets(final Mystery mystery) {
        GetSecretsResponse getSecretsResponse = mEndpointsController.getSecrets(mystery.getId());
        if ("success".equals(getSecretsResponse.getState())) {
            for (Secret secret : getSecretsResponse.getBody()) {
                addSecret(secret, mystery);
            }
        } else {
            // TODO: we need something stronger than this :)
            Log.i(TAG, "Error while getting secrets");
        }
    }

    public void clearMysteries() {
        for (Secret secret : mSecrets.values()) {
            if (secret != null) {
                mPrefs.clearSecret(secret.getId());
            }
        }
        mPrefs.clearMysteries();
        mSecrets.clear();
        mMysteriesMapById.clear();
    }

    public Mystery getFirstMystery() {
        if (mMysteriesMapById.size() > 0) {
            return mMysteriesMapById.values().iterator().next();
        } else {
            return null;
        }
    }

    public void start() {

    }

    public void addMystery(Mystery mystery) {
        mMysteriesMapById.put(mystery.getId(), mystery);
    }

    public void asynUploadMystery(final Mystery mystery, Bitmap photo, final OperationCallback callback) {
        AmazonUrl amazonUrl = null;
        try {
            amazonUrl = AmazonUrl.fromUrl(mystery.getImgUrl());
        } catch (ParseException e) {
            if (callback != null) {
                callback.onError("Image URL is not an valid Amazon URL");
            }
        }

        // TODO save image in cache
        // mImageManager.getCacheManager().put(mystery.getImgUrl(), photo);
        mPhotoUploadController.asyncUploadPhotoToAmazon(amazonUrl, photo, new PhotoUploadController.Callback() {
            @Override
            public void onSuccess() {
                mEndpointsController.updateMystery(mystery, new Callback<UpdateMysteryResponse>() {
                    @Override
                    public void success(UpdateMysteryResponse updateMysteryResponse, Response response) {
                        if ("success".equals(updateMysteryResponse.getState())) {
                            addMystery(mystery);
                            saveAllData();
                            if (callback != null) {
                                callback.onSuccess();
                            }
                        } else {
                            if (callback != null) {
                                callback.onError("Uploading to strollimo server failed with pickupState: " + updateMysteryResponse.getState());
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    }
                });
            }

            @Override
            public void onError(Exception ex) {
                if (callback != null) {
                    callback.onError("Uploading photo failed");
                }
            }
        });

    }

    public void asynUploadSecret(final Secret secret, final Mystery mystery, Bitmap photo, final OperationCallback callback) {
        AmazonUrl amazonUrl = null;
        try {
            amazonUrl = AmazonUrl.fromUrl(secret.getImgUrl());
        } catch (ParseException e) {
            if (callback != null) {
                callback.onError("Image URL is not an valid Amazon URL");
            }
        }

        //TODO save image in cache
        //mImageManager.getCacheManager().put(secret.getImgUrl(), photo);
        mPhotoUploadController.asyncUploadPhotoToAmazon(amazonUrl, photo, new PhotoUploadController.Callback() {
            @Override
            public void onSuccess() {
                addSecret(secret, mystery);
                saveAllData();

                mEndpointsController.updateMystery(mystery, new Callback<UpdateMysteryResponse>() {
                    @Override
                    public void success(UpdateMysteryResponse updateMysteryResponse, Response response) {
                        if ("success".equals(updateMysteryResponse.getState())) {
                            mEndpointsController.updateSecret(secret, new Callback<UpdateSecretResponse>() {

                                @Override
                                public void success(UpdateSecretResponse updateSecretResponse, Response response) {
                                    if ("success".equals(updateSecretResponse.getState())) {
                                        if (callback != null) {
                                            callback.onSuccess();
                                        }
                                    } else {
                                        if (callback != null) {
                                            callback.onError("Uploading to strollimo server failed with pickupState: " + updateSecretResponse.getState());
                                        }
                                    }
                                }

                                @Override
                                public void failure(RetrofitError retrofitError) {
                                    if (callback != null) {
                                        callback.onSuccess();
                                    }
                                }
                            });

                        } else {
                            if (callback != null) {
                                callback.onError("Uploading to strollimo server failed with pickupState: " + updateMysteryResponse.getState());
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    }
                });
            }

            @Override
            public void onError(Exception ex) {
                if (callback != null) {
                    callback.onError("Uploading photo failed");
                }
            }
        });

    }

    public void addSecret(Secret secret, Mystery mystery) {
        mSecrets.put(secret.getId(), secret);
        mystery.addChild(secret.getId());
    }

    public Secret getSecretById(String id) {
        return mSecrets.get(id);
    }

    public Mystery getMysteryById(String id) {
        return mMysteriesMapById.get(id);
    }

    public Map<String, Secret> getAllSecrets() {
        return mSecrets;
    }

    public List<Mystery> getAllMysteries() {
        return new ArrayList<Mystery>(mMysteriesMapById.values());
    }

    public int getMysteriesCount() {
        return mMysteriesMapById.size();
    }

    public void saveAllData() {
        mPrefs.saveMissions(getAllMysteries(), getAllSecrets());
    }

    public Mystery getNextMisteryOf(Mystery mystery) {
        List<Mystery> mysteryList = new ArrayList<Mystery>(mMysteriesMapById.values());
        int newIndex = mysteryList.indexOf(mystery) + 1;
        if (newIndex >= mysteryList.size()) {
            newIndex = 0;
        }
        return mysteryList.get(newIndex);
    }

    public Mystery getPrevMisteryOf(Mystery mystery) {
        List<Mystery> mysteryList = new ArrayList<Mystery>(mMysteriesMapById.values());
        int newIndex = mysteryList.indexOf(mystery) - 1;
        if (newIndex < 0) {
            newIndex = mysteryList.size() - 1;
        }
        return mysteryList.get(newIndex);
    }

    public boolean isMysteryFinished(String mysteryId) {
        Mystery mystery = mMysteriesMapById.get(mysteryId);
        if (mystery == null) {
            return false;
        }

        for (String secretId : mystery.getChildren()) {
            Secret secret = mSecrets.get(secretId);
            if (secret != null && secret.getPickupState() != BaseAccomplishable.PickupState.ACCOMPLISHED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether the quest is completed (every secret is different than unpicked).
     * @return - true if the quest is completed, false otherwise
     */
    public boolean isQuestCompleted() {
        for (Mystery mystery : getAllMysteries()) {
            for (String secretId : mystery.getChildren()) {
                Secret secret = getSecretById(secretId);
                if (secret == null) {
                    Log.e(MainActivity.class.getSimpleName(), "Error - secret is not available isQuestComplete the images: " + secretId);
                    continue;
                }
                BaseAccomplishable.PickupState state = secret.getPickupState();
                if (state == null) {
                    return false;
                }
                switch (state) {
                    case UNPICKED:
                        return false;
                    default:
                        // keep going
                }
            }
        }

        return true;
    }

    public interface OperationCallback {
        void onSuccess();

        void onError(String errorMsg);
    }
}
