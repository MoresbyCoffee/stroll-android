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
        mPlaces.put(1, new Place(1, "Lost in time", 51.504055, -0.019859, "id:1", context.getResources().getDrawable(R.drawable.canary2)));
        mPlaces.put(2, new Place(2, "The mystery of the Bridge", 51.501757, -0.020514, "id:2", context.getResources().getDrawable(R.drawable.canary3)));
        mPlaces.put(3, new Place(3, "The hidden 'Canary'", 51.507040, -0.022413, "id:3", context.getResources().getDrawable(R.drawable.canary4)));
        mPlaces.put(4, new Place(4, "Amsterdam", 51.494996, -0.01649, "id:4", context.getResources().getDrawable(R.drawable.dock)));
        mPlaces.put(5, new Place(5, "Floating Chinese", 51.49708, -0.016147, "id:5", context.getResources().getDrawable(R.drawable.lotus)));
        mPlaces.put(6, new Place(6, "The Golden Egg", 51.505722, -0.027047, "id:6", context.getResources().getDrawable(R.drawable.westferry_circus)));
    }

    public Place getPlaceById(int id) {
        return mPlaces.get(id);
    }

    public Collection<Place> getAllPlaces() {
        return mPlaces.values();
    }
}
