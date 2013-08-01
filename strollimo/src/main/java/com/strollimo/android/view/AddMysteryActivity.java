package com.strollimo.android.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.novoda.imageloader.core.ImageManager;
import com.strollimo.android.AppGlobals;
import com.strollimo.android.R;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.controller.PhotoUploadController;
import com.strollimo.android.controller.PlacesController;
import com.strollimo.android.model.Mystery;
import com.strollimo.android.network.AmazonUrl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

public class AddMysteryActivity extends Activity {
    public static final int REQUEST_PICK_IMAGE = 52;
    private EditText mIdEditText;
    private EditText mNameEditText;
    private EditText mShortDescEditText;
    private PlacesController mPlacesController;
    private ImageView mPhotoImageView;
    private ImageManager mImageManager;
    private PhotoUploadController mPhotoUploadController;
    private MapView mMapView;
    private LocationClient mLocationClient;
    private GoogleMap mMap;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoUploadController = StrollimoApplication.getService(PhotoUploadController.class);
        mImageManager = StrollimoApplication.getService(ImageManager.class);
        mPlacesController = StrollimoApplication.getService(PlacesController.class);
        setContentView(R.layout.add_mystery_activity);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        setUpMapIfNecessary();
        setUpLocationClientIfNecessary();

        mIdEditText = (EditText) findViewById(R.id.id_edit_text);
        mNameEditText = (EditText) findViewById(R.id.name_edit_text);
        mShortDescEditText = (EditText) findViewById(R.id.short_desc_edit_text);

        mIdEditText.setText(Long.toString(Math.abs(new Random().nextLong())));
        mNameEditText.setText("test title");
        mShortDescEditText.setText("test desc");
        mPhotoImageView = (ImageView) findViewById(R.id.photo_holder);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_secret, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_secret) {
            addClicked();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void addClicked() {
        final double lat = mMap.getCameraPosition().target.latitude;
        final double lng = mMap.getCameraPosition().target.longitude;
        final String id = mIdEditText.getText().toString();
        final String name = mNameEditText.getText().toString();
        final AmazonUrl amazonUrl = AmazonUrl.createMysteryUrl(id);
        Mystery mystery = new Mystery(id, name, lat, lng, amazonUrl.getUrl());
        mystery.setShortDesc(mShortDescEditText.getText().toString());

        Bitmap photo = ((BitmapDrawable) mPhotoImageView.getDrawable()).getBitmap();

        progressDialog = ProgressDialog.show(this, "", "Uploading image...");

        mPlacesController.asynUploadMystery(mystery, photo, new PlacesController.UploadCallback() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                finish();
            }

            @Override
            public void onError(String errorMsg) {
                progressDialog.dismiss();
                Toast.makeText(AddMysteryActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                finish();
            }
        });

    }

    public void replaceImageClicked(View view) {
        Intent pickImageIntent = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImageIntent, REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_PICK_IMAGE:
                Uri imageUri = data.getData();
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), imageUri);
                    mPhotoImageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                break;
            default:
        }
    }

    private void setUpMapIfNecessary() {
        if (mMap == null) {
            mMap = mMapView.getMap();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setCompassEnabled(false);
        }
    }

    private void setUpLocationClientIfNecessary() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(this, new GooglePlayServicesClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle bundle) {
                    Location loc = mLocationClient.getLastLocation();
                    CameraPosition pos;
                    if (loc != null) {
                        pos = CameraPosition.builder().target(new LatLng(loc.getLatitude(), loc.getLongitude())).zoom(16f).build();
                    } else {
                        pos = CameraPosition.builder().target(AppGlobals.LONDON_SOMERSET_HOUSE).zoom(16f).build();
                    }
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
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

}
