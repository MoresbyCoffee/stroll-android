package org.moresbycoffee.stroll.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DetailsActivity extends Activity {

    public static final String PLACE_ID_EXTRA = "place_id";
    private TextView mStatusTextView;
    private TextView mTitleTextView;
    private PlacesService mPlacesService;
    private Place mCurrentPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlacesService = ((StrollApplication)getApplication()).getService(PlacesService.class);
        setContentView(R.layout.details_screen);
        mStatusTextView = (TextView)findViewById(R.id.status);
        mTitleTextView = (TextView)findViewById(R.id.title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurrentPlace = mPlacesService.getPlaceById(getIntent().getIntExtra(PLACE_ID_EXTRA, 0));
        mTitleTextView.setText(mCurrentPlace == null ? "Error" : mCurrentPlace.mTitle);
        mStatusTextView.setText("Uncaptured");
    }

    public static Intent createDetailsIntent(Context context, int placeId) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra(PLACE_ID_EXTRA, placeId);
        return intent;
    }

}
