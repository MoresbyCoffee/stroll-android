package org.moresbycoffee.stroll.android;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

public class StrollMapActivity extends Activity {
    private GoogleMap mMap;
    private LocationClient mLocationClient;
    private Map<String, Integer> mMarkers = new HashMap<String, Integer>();
    private PlacesService mPlacesService;
    private UserService mUserService;
    private boolean firstStart = true;
    private ImageView mPlaceImage;
    private TextView mPlaceTitle;
    private View mRibbonPanel;
    private Place mCurrentPlace;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstStart = true;
        setContentView(R.layout.stroll_map_layout);
        mPlacesService = ((StrollApplication) getApplication()).getService(PlacesService.class);
        mUserService = ((StrollApplication) getApplication()).getService(UserService.class);
        mPlaceImage = (ImageView)findViewById(R.id.place_image);
        mPlaceTitle = (TextView)findViewById(R.id.place_title);
        mRibbonPanel = findViewById(R.id.ribbon_panel);
        mRibbonPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchDetailsActivity();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMap.clear();
        for (Place place : mPlacesService.getAllPlaces()) {
            addPlaceToMap(place);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpMapIfNecessary();

        setUpLocationClientIfNecessary();
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
            Integer placeId = mMarkers.get(marker.getId());
            mCurrentPlace = mPlacesService.getPlaceById(placeId);
            launchDetailsActivity();
        }
    };

    private void launchDetailsActivity() {
        if (mCurrentPlace != null) {
            this.startActivity(DetailsActivity.createDetailsIntent(this, mCurrentPlace.mId));
        }
    }

    private void setUpLocationClientIfNecessary() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(this, new GooglePlayServicesClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle bundle) {
                    if (firstStart) {
                        Location loc = mLocationClient.getLastLocation();
                        CameraPosition pos = CameraPosition.builder().target(new LatLng(loc.getLatitude(), loc.getLongitude())).zoom(16f).tilt(45).build();
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
            mCurrentPlace = mPlacesService.getPlaceById(placeId);
            displayRibbon(mCurrentPlace);
            return false;
        }
    };

    private void displayRibbon(Place place) {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_right);
        mRibbonPanel.setVisibility(View.VISIBLE);
        mPlaceImage.setImageBitmap(place.getBitmap());
        mPlaceTitle.setText(place.mTitle);
        mRibbonPanel.startAnimation(anim);
    }

    private void hideRibbon() {
        if (mCurrentPlace != null) {
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
        mCurrentPlace = null;
    }

    private GoogleMap.OnMapClickListener mOnMapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            hideRibbon();
        }
    };
}