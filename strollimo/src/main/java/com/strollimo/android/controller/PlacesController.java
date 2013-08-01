package com.strollimo.android.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import com.novoda.imageloader.core.ImageManager;
import com.strollimo.android.StrollimoPreferences;
import com.strollimo.android.model.Mystery;
import com.strollimo.android.model.Secret;
import com.strollimo.android.network.AmazonUrl;

import java.text.ParseException;
import java.util.*;

public class PlacesController {
    private final StrollimoPreferences mPrefs;
    private final ImageManager mImageManager;
    private final PhotoUploadController mPhotoUploadController;
    private Map<String, Mystery> mMysteries;
    private Context mContext;
    private LinkedHashMap<String, Secret> mSecrets = new LinkedHashMap<String, Secret>();

    public PlacesController(Context context, StrollimoPreferences prefs, ImageManager imageManager, PhotoUploadController photoUploadController) {
        mContext = context;
        mMysteries = new HashMap<String, Mystery>();
        mPrefs = prefs;
        mImageManager = imageManager;
        mPhotoUploadController = photoUploadController;
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
                    myMystery.addChild(secretId);
                    addSecret(secret, myMystery);
                }
            }
        }
        preloadImages(new ArrayList<Mystery>(mMysteries.values()));
    }

    public Mystery getFirstMystery() {
        if (mMysteries.size() > 0) {
            return mMysteries.values().iterator().next();
        } else {
            return null;
        }
    }

    private void preloadImages(final List<Mystery> mysteries) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                for (Mystery mystery : mysteries) {
                    if (!TextUtils.isEmpty(mystery.getImgUrl())) {
                        mImageManager.cacheImage(mystery.getImgUrl(), 800, 600);
                    }
                }
            }
        }).start();
    }

    public void start() {

    }

    public void addMystery(Mystery mystery) {
        mMysteries.put(mystery.getId(), mystery);
    }

    public interface UploadCallback {
        void onSuccess();
        void onError(String errorMsg);
    }
    public void asynUploadMystery(final Mystery mystery, Bitmap photo, final UploadCallback callback) {
        AmazonUrl amazonUrl = null;
        try {
            amazonUrl = AmazonUrl.fromUrl(mystery.getImgUrl());
        } catch (ParseException e) {
            if (callback != null) {
                callback.onError("Image URL is not an valid Amazon URL");
            }
        }

        mImageManager.getCacheManager().put(mystery.getImgUrl(), photo);
        mPhotoUploadController.asyncUploadPhotoToAmazon(amazonUrl, photo, new PhotoUploadController.Callback() {
            @Override
            public void onSuccess() {
                addMystery(mystery);
                saveAllData();
                if (callback != null) {
                    callback.onSuccess();
                }
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
        return mMysteries.get(id);
    }

    public Map<String, Secret> getAllSecrets() {
        return mSecrets;
    }

    public List<Mystery> getAllMysteries() {
        return new ArrayList<Mystery>(mMysteries.values());
    }

    public int getMysteriesCount() {
        return mMysteries.size();
    }

    public void saveAllData() {
        mPrefs.saveMissions(getAllMysteries(), getAllSecrets());
    }

    public void loadDemoData() {
        for (Mystery mystery : mPrefs.getHardcodedMysteries()) {
            addMystery(mystery);
        }
    }
}
