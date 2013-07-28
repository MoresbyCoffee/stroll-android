package com.strollimo.android.controller;

import android.content.Context;
import com.strollimo.android.R;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.StrollimoPreferences;
import com.strollimo.android.model.Mystery;
import com.strollimo.android.model.Secret;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlacesController {
    private final StrollimoPreferences mPrefs;
    private Map<String, Mystery> mPlaces;
    private Context mContext;
    private List<Mystery> mMysteries = new ArrayList<Mystery>();
    private Map<String, Secret> mSecrets = new HashMap<String, Secret>();

    public PlacesController(Context context) {
        mContext = context;
        mPlaces = new HashMap<String, Mystery>();
        mPrefs = StrollimoApplication.getService(StrollimoPreferences.class);
        Mystery lostInTime = new Mystery("1_lost_in_time", "Lost in time", 51.504055, -0.019859, "id:1", context.getResources().getDrawable(R.drawable.canary2));
        addMission(lostInTime);
        addMission(new Mystery("2_mystery_of_bridge", "The mystery of the Bridge", 51.501757, -0.020514, "id:2", context.getResources().getDrawable(R.drawable.canary3)));
        addMission(new Mystery("3_hidden_canary", "The hidden 'Canary'", 51.507040, -0.022413, "id:3", context.getResources().getDrawable(R.drawable.canary4)));
        addMission(new Mystery("4_amsterdam", "Amsterdam", 51.494996, -0.01649, "id:4", context.getResources().getDrawable(R.drawable.dock)));
        addMission(new Mystery("5_floating_chinese", "Floating Chinese", 51.49708, -0.016147, "id:5", context.getResources().getDrawable(R.drawable.lotus)));
        addMission(new Mystery("6_golden_egg", "The Golden Egg", 51.505722, -0.027047, "id:6", context.getResources().getDrawable(R.drawable.westferry_circus)));
        for (Mystery mystery : mPrefs.getMissions()) {
            Mystery hardcoded = mPlaces.get(mystery.getId());
            for (Secret secret : mystery.getSecrets()) {
                hardcoded.addSecret(secret);
                mSecrets.put(secret.getId(), secret);
            }
        }

    }

    private void addMission(Mystery mystery) {
        mPlaces.put(mystery.getId(), mystery);
        mMysteries.add(mystery);
    }

    public void addSecret(Secret secret, Mystery mystery) {
        mSecrets.put(secret.getId(), secret);
        mystery.addSecret(secret);
    }


    public Secret getSecretById(String id) {
        return mSecrets.get(id);
    }
    public Mystery getPlaceById(String id) {
        return mPlaces.get(id);
    }

    public List<Mystery> getAllPlaces() {
        return mMysteries;
    }

    public int getPlacesCount() {
        return mPlaces.size();
    }
}
