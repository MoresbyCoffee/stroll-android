package com.strollimo.android.controller;

import android.content.Context;
import com.novoda.imageloader.core.ImageManager;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.StrollimoPreferences;
import com.strollimo.android.model.Mystery;
import com.strollimo.android.model.Secret;
import com.strollimo.android.network.AmazonS3Controller;

import java.util.*;

public class PlacesController {
    private final StrollimoPreferences mPrefs;
    private final ImageManager mImageManager;
    private Map<String, Mystery> mMysteries;
    private Context mContext;
    private Map<String, Secret> mSecrets = new LinkedHashMap<String, Secret>();

    public PlacesController(Context context) {
        mContext = context;
        mMysteries = new HashMap<String, Mystery>();
        mPrefs = StrollimoApplication.getService(StrollimoPreferences.class);
        mImageManager = StrollimoApplication.getService(ImageManager.class);        preloadPlaces();
    }

    public void preloadPlaces() {
        Mystery lostInTime = new Mystery("1_lost_in_time", "Lost in time", 51.504055, -0.019859, AmazonS3Controller.mysteryUrl("canary2.jpeg").getUrl());
        addMystery(lostInTime);
        addMystery(new Mystery("2_mystery_of_bridge", "The mystery of the Bridge", 51.501757, -0.020514, AmazonS3Controller.mysteryUrl("canary3.png").getUrl()));
        addMystery(new Mystery("3_hidden_canary", "The hidden 'Canary'", 51.507040, -0.022413, AmazonS3Controller.mysteryUrl("canary4.jpg").getUrl()));
        addMystery(new Mystery("4_amsterdam", "Amsterdam", 51.494996, -0.01649, AmazonS3Controller.mysteryUrl("dock.jpg").getUrl()));
        addMystery(new Mystery("5_floating_chinese", "Floating Chinese", 51.49708, -0.016147, AmazonS3Controller.mysteryUrl("lotus.jpg").getUrl()));
        addMystery(new Mystery("6_golden_egg", "The Golden Egg", 51.505722, -0.027047, AmazonS3Controller.mysteryUrl("westferry_circus.jpg").getUrl()));
        List<Mystery> mysteries = mPrefs.getMysteries();
        if (mysteries != null) {
            for (Mystery mystery : mysteries) {
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

    private void preloadImages(final List<Mystery> mysteries) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                for (Mystery mystery : mysteries) {
                    mImageManager.cacheImage(mystery.getImgUrl(), 800, 600);
                }
            }
        }).start();
    }

    public void start() {

    }

    public void addMystery(Mystery mystery) {
        mMysteries.put(mystery.getId(), mystery);
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
}
