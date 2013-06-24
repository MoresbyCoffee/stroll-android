package com.strollimo.android;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
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

import java.util.ArrayList;
import java.util.List;

public class StrollMapActivity extends Activity {
    private final int MAJOR_MOVE = 10;
    private GoogleMap mMap;
    private LocationClient mLocationClient;
    private PlacesService mPlacesService;
    private UserService mUserService;
    private boolean firstStart = true;
    private ImageView mPlaceImage;
    private TextView mPlaceTitle;
    private View mRibbonPanel;
    private GestureDetector mGestureDetector;
    private View mRibbonTouchView;
    private boolean mSwipeInProgress;
    private MapPlacesModel mMapPlacesModel;
    private Marker mSelectedMarker;
    private Place mSelectedPlace;
    private MapPlace mSelectedMapPlace;
    private List<MapPlace> mMapPlaces;

    private GoogleMap.OnInfoWindowClickListener onInfoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            Log.i("BB", "on info window selected");
            launchDetailsActivity();
        }
    };
    private GoogleMap.OnMarkerClickListener mOnMarkerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            mMapPlacesModel.onMarkerClick(marker);
            displayRibbon(mMapPlacesModel.getSelectedPlace());
            return false;
        }
    };
    private GoogleMap.OnMapClickListener mOnMapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            hideRibbon();
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstStart = true;
        mMapPlaces = new ArrayList<MapPlace>();
        setContentView(R.layout.stroll_map_layout);
        mPlacesService = ((StrollimoApplication) getApplication()).getService(PlacesService.class);
        mUserService = ((StrollimoApplication) getApplication()).getService(UserService.class);
        mPlaceImage = (ImageView) findViewById(R.id.place_image);
        mPlaceTitle = (TextView) findViewById(R.id.place_title);
        mRibbonPanel = findViewById(R.id.ribbon_panel);
        mRibbonTouchView = findViewById(R.id.ribbon_touch_view);

        findViewById(R.id.ribbon_touch_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mSwipeInProgress) {
                    Log.i("BB", "on click");
                    launchDetailsActivity();
                }
            }
        });
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.i("BB", "two");
                int dx = (int) (e2.getX() - e1.getX());
                // don't accept the fling if it's too short
                // as it may conflict with a button push
                if (Math.abs(dx) > MAJOR_MOVE && Math.abs(velocityX) > Math.abs(velocityY)) {
                    Log.i("BB", "three " + velocityX);
                    if (velocityX > 0) {
                        moveRight();
                    } else {
                        moveLeft();
                    }
                    mSwipeInProgress = true;
                }
                return false;
            }
        });
        mRibbonTouchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.i("BB", "one");
                return mGestureDetector.onTouchEvent(motionEvent);
            }
        });
    }

    private void moveLeft() {

        mSwipeInProgress = false;
    }

    private void moveRight() {
        mSwipeInProgress = false;
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
        if (mMapPlacesModel.isSelectedPlaceCaptured()) {
            mSelectedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pink_flag));
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
        if (mMapPlaces != null) {
            return;
        }

        mMapPlaces = new ArrayList<MapPlace>();
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
        mMapPlaces.add(new MapPlace(place, marker));
    }

    @Override
    protected void onStop() {
        mLocationClient.disconnect();
        super.onStop();
    }

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

    private static class MapPlace {
        public Place mPlace;
        public Marker mMarker;
        public MapPlace(Place place, Marker marker) {
            mPlace = place;
            mMarker = marker;
        }
    }

    private Place getPlaceForMarker(Marker marker) {
        if (marker == null) {
            return null;
        }

        for (MapPlace mapPlace : mMapPlaces) {
            if (marker.equals(mapPlace.mMarker)) {
                return mapPlace.mPlace;
            }
        }
        return null;
    }

    private class MapPlacesModel {

        public void onMarkerClick(Marker marker) {
            Place place = getPlaceForMarker(marker);
            mSelectedPlace = mPlacesService.getPlaceById(place.mId);
            mSelectedMarker = marker;
        }

        public Place getSelectedPlace() {
            return mSelectedPlace;
        }

        public boolean isSelectedPlaceCaptured() {
            if (mMapPlacesModel.getSelectedPlace() != null && mUserService.isPlaceCaptured(mMapPlacesModel.getSelectedPlace().mId)) {
                return true;
            } else {
                return false;
            }
        }
    }
}