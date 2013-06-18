package org.moresbycoffee.stroll.android;

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
        mPlaces.put(1, new Place(1, "Lost in time", 51.504055, -0.019859, "id:3", context.getResources().getDrawable(R.drawable.canary2)));
        mPlaces.put(2, new Place(2, "The mystery of the Bridge", 51.501757, -0.020514, "id:2", context.getResources().getDrawable(R.drawable.canary3)));
        mPlaces.put(3, new Place(3, "The hidden 'Canary'", 51.507040, -0.022413, "id:1", context.getResources().getDrawable(R.drawable.canary4)));
    }

    public Place getPlaceById(int id) {
        return mPlaces.get(id);
    }

    public Collection<Place> getAllPlaces() {
        return mPlaces.values();
    }
}
