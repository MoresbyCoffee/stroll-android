package org.moresbycoffee.stroll.android;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PlacesService {
    private Map<Integer, Place> mPlaces;

    public PlacesService() {
        mPlaces = new HashMap<Integer, Place>();
        mPlaces.put(1, new Place(1, "The hidden 'Canary'", 51.49916, -0.021254, "id:3"));
        mPlaces.put(2, new Place(2, "Black eyes on Lighterman's Road", 51.498933, -0.018861, "id:2"));
    }

    public Place getPlaceById(int id) {
        return mPlaces.get(id);
    }

    public Collection<Place> getAllPlaces() {
        return mPlaces.values();
    }
}
