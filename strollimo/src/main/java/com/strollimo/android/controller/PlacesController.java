package com.strollimo.android.controller;

import android.content.Context;
import com.strollimo.android.R;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.StrollimoPreferences;
import com.strollimo.android.model.Mission;
import com.strollimo.android.model.Secret;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlacesController {
    private final StrollimoPreferences mPrefs;
    private Map<String, Mission> mPlaces;
    private Context mContext;
    private List<Mission> mMissions = new ArrayList<Mission>();
    private Map<String, Secret> mSecrets = new HashMap<String, Secret>();

    public PlacesController(Context context) {
        mContext = context;
        mPlaces = new HashMap<String, Mission>();
        mPrefs = StrollimoApplication.getService(StrollimoPreferences.class);
        Mission lostInTime = new Mission("1", "Lost in time", 51.504055, -0.019859, "id:1", context.getResources().getDrawable(R.drawable.canary2));
        addMission(lostInTime);
        addMission(new Mission("2", "The mystery of the Bridge", 51.501757, -0.020514, "id:2", context.getResources().getDrawable(R.drawable.canary3)));
        addMission(new Mission("3", "The hidden 'Canary'", 51.507040, -0.022413, "id:3", context.getResources().getDrawable(R.drawable.canary4)));
        addMission(new Mission("4", "Amsterdam", 51.494996, -0.01649, "id:4", context.getResources().getDrawable(R.drawable.dock)));
        addMission(new Mission("5", "Floating Chinese", 51.49708, -0.016147, "id:5", context.getResources().getDrawable(R.drawable.lotus)));
        addMission(new Mission("6", "The Golden Egg", 51.505722, -0.027047, "id:6", context.getResources().getDrawable(R.drawable.westferry_circus)));
        for (Mission mission : mPrefs.getMissions()) {
            Mission hardcoded = mPlaces.get(mission.getId());
            for (Secret secret : mission.getSecrets()) {
                hardcoded.addSecret(secret);
                mSecrets.put(secret.getId(), secret);
            }
        }

    }

    private void addMission(Mission mission) {
        mPlaces.put(mission.getId(), mission);
        mMissions.add(mission);
    }

    public void addSecret(Secret secret, Mission mission) {
        mSecrets.put(secret.getId(), secret);
        mission.addSecret(secret);
    }


    public Secret getSecretById(String id) {
        return mSecrets.get(id);
    }
    public Mission getPlaceById(String id) {
        return mPlaces.get(id);
    }

    public List<Mission> getAllPlaces() {
        return mMissions;
    }

    public int getPlacesCount() {
        return mPlaces.size();
    }
}
