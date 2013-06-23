package com.strollimo.android;

import android.content.Context;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PlacesService {
    private Map<Integer, Place> mPlaces;
    private Context mContext;

    public PlacesService(Context context) {
        mContext = context;
        mPlaces = new HashMap<Integer, Place>();


        mPlaces.put(1, new Place(1, "Szabadsag bridge", 47.48586, 19.054885, "id:1", context.getResources().getDrawable(R.drawable.szabadsag_bridge)));
        mPlaces.put(2, new Place(2, "Gellert square", 47.4847, 19.052868, "id:2", context.getResources().getDrawable(R.drawable.gellert_square2)));
        mPlaces.put(3, new Place(3, "Gellért hill - Statue of Liberty", 47.486759, 19.047933, "id:3", context.getResources().getDrawable(R.drawable.gellert_hill)));
        mPlaces.put(4, new Place(4, "Great Market Hall", 47.486904, 19.05864, "id:4", context.getResources().getDrawable(R.drawable.great_market_hall)));
        mPlaces.put(5, new Place(5, "National Museum", 47.49121, 19.062696, "id:5", context.getResources().getDrawable(R.drawable.national_museum)));
        mPlaces.put(6, new Place(6, "Kalvin square", 47.48992, 19.061687, "id:6", context.getResources().getDrawable(R.drawable.kalvin_square)));
        mPlaces.put(7, new Place(7, "Raday Street, Budapest's Soho", 47.487354, 19.063082, "id:7", context.getResources().getDrawable(R.drawable.raday_street)));
    }

    public Place getPlaceById(int id) {
        return mPlaces.get(id);
    }

    public Collection<Place> getAllPlaces() {
        return mPlaces.values();
    }

    public int getPlacesCount() {
        return mPlaces.size();
    }
}
