package org.moresbycoffee.stroll.android;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

public class StrollMapActivity extends Activity {
    private GoogleMap mMap;
    private LocationClient mLocationClient;
    private Map<String, Integer> mMarkers = new HashMap<String, Integer>();
    private PlacesService mPlacesService;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stroll_map_layout);
        mPlacesService = ((StrollApplication) getApplication()).getService(PlacesService.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(true);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Integer placeId = mMarkers.get(marker.getId());
                Log.i("BB", String.format("place id: %d", placeId));
                Place place = mPlacesService.getPlaceById(placeId);
                Log.i("BB", String.format("place: %s", place.mTitle));
                StrollMapActivity.this.startActivity(DetailsActivity.createDetailsIntent(StrollMapActivity.this, place.mId));
            }
        });
        mLocationClient = new LocationClient(this, new GooglePlayServicesClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                Location loc = mLocationClient.getLastLocation();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 16f));
                for (Place place : mPlacesService.getAllPlaces()) {
                    addPlaceToMap(place);
                }
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        return false;
                    }
                });
            }

            @Override
            public void onDisconnected() {

            }
        }, new GooglePlayServicesClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {

            }
        }
        );
        mLocationClient.connect();
    }

    private void addPlaceToMap(Place place) {
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(place.mLat, place.mLon))
                .title(place.mTitle));
        mMarkers.put(marker.getId(), place.mId);
    }

    @Override
    protected void onStop() {
        mLocationClient.disconnect();
        super.onStop();
    }
}