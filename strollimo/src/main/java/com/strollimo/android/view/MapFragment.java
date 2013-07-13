package com.strollimo.android.view;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.*;
import com.strollimo.android.model.MapPlacesModel;
import com.strollimo.android.model.Mission;
import com.strollimo.android.controller.PlacesController;
import com.strollimo.android.R;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.StrollimoPreferences;
import com.strollimo.android.controller.UserService;
import com.strollimo.android.view.dialog.DemoFinishedDialog;

public class MapFragment extends Fragment {
    private View mView;
    private GoogleMap mMap;
    private LocationClient mLocationClient;
    private PlacesController mPlacesController;
    private StrollimoPreferences mPrefs;

    private UserService mUserService;
    private boolean firstStart = true;
    private ImageView mPlaceImage;
    private TextView mPlaceTitle;
    private View mRibbonPanel;
    private View mRibbonTouchView;
    private MapPlacesModel mMapPlacesModel;

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
            displayRibbon(mMapPlacesModel.getSelectedPlace(), true);
            return false;
        }
    };
    private GoogleMap.OnMapClickListener mOnMapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            hideRibbon();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mPrefs = ((StrollimoApplication)getActivity().getApplication()).getService(StrollimoPreferences.class);
            firstStart = true;
            View view = inflater.inflate(R.layout.stroll_map_layout, container);


            mPlacesController = ((StrollimoApplication) getActivity().getApplication()).getService(PlacesController.class);
            mUserService = ((StrollimoApplication) getActivity().getApplication()).getService(UserService.class);
            mPlaceImage = (ImageView) view.findViewById(R.id.place_image);
            mPlaceTitle = (TextView) view.findViewById(R.id.place_title);
            mRibbonPanel = view.findViewById(R.id.ribbon_panel);

            mRibbonPanel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("BB", "on click");
                    launchDetailsActivity();
                }
            });

            setSwipeToChangePlace(mRibbonPanel);
        } else {
            ViewGroup parentViewGroup = (ViewGroup) mView.getParent();
            if (parentViewGroup != null) {
                parentViewGroup.removeAllViews();
            }
        }
        return mView;
    }


    private void setSwipeToChangePlace(View dismissableRibbon) {
        dismissableRibbon.setOnTouchListener(new SwipeDismissTouchListener(
                dismissableRibbon,
                null,
                new SwipeDismissTouchListener.OnDirectionalDismissCallback() {
                    @Override
                    public void onDismiss(View view, Object token, DismissDirectionType dismissDirectionType) {
                        Mission toMission;
                        if (dismissDirectionType == DismissDirectionType.RIGHT) {
                            toMission = mMapPlacesModel.getNextPlaceFor(getActivity(), mMapPlacesModel.getSelectedPlace());
                        } else {
                            toMission = mMapPlacesModel.getPreviousPlaceFor(getActivity(), mMapPlacesModel.getSelectedPlace());
                        }
                        if (toMission != null) {
                            Marker marker = mMapPlacesModel.getMarkerForPlace(toMission);
                            marker.showInfoWindow();
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 250, null);
                            mMapPlacesModel.selectMapPlaceByPlace(toMission);
                            displayRibbon(mMapPlacesModel.getSelectedPlace(), dismissDirectionType != DismissDirectionType.RIGHT);
                        } else {
                            mMapPlacesModel.getSelectedMarker().hideInfoWindow();
                            mRibbonPanel.setVisibility(View.GONE);
                            mMapPlacesModel.hideSelectedPlace();
                        }
                    }
                }));
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapPlacesModel.refreshSelectedMarker();
        if (mUserService.getFoundPlacesNum() == mPlacesController.getPlacesCount()) {
            new DemoFinishedDialog().show(getActivity().getSupportFragmentManager(), "dialog");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("BB", "onStart");
        setUpMapIfNecessary();

        setUpLocationClientIfNecessary();
        setUpMapMarkersIfNecessary();
    }

    private void setUpMapIfNecessary() {
        if (mMap == null) {
            mMap = ((com.google.android.gms.maps.SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            mMap.setMyLocationEnabled(true);
            mMap.setOnInfoWindowClickListener(onInfoWindowClickListener);
            mMap.setOnMarkerClickListener(mOnMarkerClickListener);
            mMap.setOnMapClickListener(mOnMapClickListener);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setCompassEnabled(false);
        }
    }

    public void setUpMapMarkersIfNecessary() {
        if (mMapPlacesModel != null) {
            return;
        }

        mMapPlacesModel = new MapPlacesModel(mUserService);
        mMap.clear();
        for (Mission mission : mPlacesController.getAllPlaces()) {
            addPlaceToMap(mission);
        }
    }

    private void addPlaceToMap(Mission mission) {
        BitmapDescriptor bitmapDescriptor;
        if (mUserService.isPlaceCaptured(mission.getId())) {
            bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.pink_flag);
        } else {
            bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.azure_flag);
        }

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(mission.getLat(), mission.getLon()))
                .title(mission.getTitle()).icon(bitmapDescriptor));
        mMapPlacesModel.add(mission, marker);
    }

    @Override
    public void onStop() {
        mLocationClient.disconnect();
        super.onStop();
    }

    private void launchDetailsActivity() {
        Mission selectedMission = mMapPlacesModel.getSelectedPlace();
        if (selectedMission != null) {
            this.startActivity(DetailsActivity.createDetailsIntent(getActivity(), selectedMission.getId()));
        }
    }

    private void setUpLocationClientIfNecessary() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(getActivity(), new GooglePlayServicesClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle bundle) {
                    if (firstStart) {
                        Location loc = mLocationClient.getLastLocation();
                        Mission mission = mPlacesController.getPlaceById(1);
                        CameraPosition pos = CameraPosition.builder().target(new LatLng(mission.getLat(), mission.getLon())).zoom(16f).tilt(45).build();
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


    private void displayRibbon(Mission mission, boolean fromRight) {
        Animation anim = AnimationUtils.loadAnimation(getActivity(), fromRight ? R.anim.slide_in_from_right : R.anim.slide_in_from_left);
        mRibbonPanel.setVisibility(View.VISIBLE);
        mPlaceImage.setImageBitmap(mission.getBitmap());
        mPlaceTitle.setText(mission.getTitle().toUpperCase());
        mRibbonPanel.startAnimation(anim);
    }

    private void hideRibbon() {
        if (mMapPlacesModel.getSelectedPlace() != null) {
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_left);
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
        mMapPlacesModel.hideSelectedPlace();
    }

}