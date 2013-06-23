package com.strollimo.android;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.*;
import com.strollimo.android.dialog.DemoFinishedDialog;

import java.util.HashMap;
import java.util.Map;

public class StrollMapActivity extends Activity {
    private GoogleMap mMap;
    private LocationClient mLocationClient;
    private Map<String, Integer> mMarkers;
    private PlacesService mPlacesService;
    private UserService mUserService;
    private boolean firstStart = true;
    private ImageView mPlaceImage;
    private TextView mPlaceTitle;
    private View mRibbonPanel;
    private Place mSelectedPlace;
    private Marker mSelectedMarker;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstStart = true;
        setContentView(R.layout.stroll_map_layout);
        mPlacesService = ((StrollimoApplication) getApplication()).getService(PlacesService.class);
        mUserService = ((StrollimoApplication) getApplication()).getService(UserService.class);
        mPlaceImage = (ImageView)findViewById(R.id.place_image);
        mPlaceTitle = (TextView)findViewById(R.id.place_title);
        mRibbonPanel = findViewById(R.id.ribbon_panel);
        findViewById(R.id.ribbon_touch_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("BB", "on click");
                launchDetailsActivity();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSelectedMarker != null) {
            refreshSelectedMarker();
        }
        if (mUserService.getFoundPlacesNum() == mPlacesService.getPlacesCount()) {
            new DemoFinishedDialog().show(getFragmentManager(), "dialog");
        }
    }

    private void refreshSelectedMarker() {
        if (mSelectedPlace != null) {
            if (mUserService.isPlaceCaptured(mSelectedPlace.mId)) {
                mSelectedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pink_flag));
            }
        }
        mSelectedMarker.showInfoWindow();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("BB", "onStart");
        setUpMapIfNecessary();

        setUpLocationClientIfNecessary();
        setUpMapMarkersIfNecessary();
    }

    private void setUpMapMarkersIfNecessary() {
        if (mMarkers != null) {
            return;
        }

        mMarkers = new HashMap<String, Integer>();
        mMap.clear();
        for (Place place : mPlacesService.getAllPlaces()) {
            addPlaceToMap(place);
        }
    }

    private void setUpMapIfNecessary() {
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            mMap.setMyLocationEnabled(true);
            mMap.setOnInfoWindowClickListener(onInfoWindowClickListener);
            mMap.setOnMarkerClickListener(mOnMarkerClickListener);
            mMap.setOnMapClickListener(mOnMapClickListener);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setCompassEnabled(false);
        }
    }

    private void addPlaceToMap(Place place) {
        BitmapDescriptor bitmapDescriptor;
        if (mUserService.isPlaceCaptured(place.mId)) {
            bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.pink_flag);
        } else {
            bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.azure_flag);
        }

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(place.mLat, place.mLon))
                .title(place.mTitle).icon(bitmapDescriptor));
        mMarkers.put(marker.getId(), place.mId);
    }

    @Override
    protected void onStop() {
        mLocationClient.disconnect();
        super.onStop();
    }

    private GoogleMap.OnInfoWindowClickListener onInfoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            Log.i("BB", "on info window selected");
            launchDetailsActivity();
        }
    };

    private void launchDetailsActivity() {
        if (mSelectedPlace != null) {
            this.startActivity(DetailsActivity.createDetailsIntent(this, mSelectedPlace.mId));
        }
    }

    private void setUpLocationClientIfNecessary() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(this, new GooglePlayServicesClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle bundle) {
                    if (firstStart) {
                        Location loc = mLocationClient.getLastLocation();
                        Place place = mPlacesService.getPlaceById(1);
                        CameraPosition pos = CameraPosition.builder().target(new LatLng(place.mLat, place.mLon)).zoom(16f).tilt(45).build();
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
                        firstStart = false;
                    }
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
        }
        if (!mLocationClient.isConnected()) {
            mLocationClient.connect();
        }
    }
    private GoogleMap.OnMarkerClickListener mOnMarkerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            Integer placeId = mMarkers.get(marker.getId());
            mSelectedPlace = mPlacesService.getPlaceById(placeId);
            mSelectedMarker = marker;
            displayRibbon(mSelectedPlace);
            return false;
        }
    };

    private void displayRibbon(Place place) {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_right);
        mRibbonPanel.setVisibility(View.VISIBLE);
        mPlaceImage.setImageBitmap(place.getBitmap());
        mPlaceTitle.setText(place.mTitle.toUpperCase());
        mRibbonPanel.startAnimation(anim);
    }

    private void hideRibbon() {
        if (mSelectedPlace != null) {
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_out_to_left);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mRibbonPanel.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mRibbonPanel.startAnimation(anim);
        }
        mSelectedPlace = null;
        if (mSelectedMarker != null) {
            mSelectedMarker = null;
        }
    }

    private GoogleMap.OnMapClickListener mOnMapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            hideRibbon();
        }
    };
}