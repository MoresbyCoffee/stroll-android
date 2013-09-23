package com.strollimo.android.view;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.strollimo.android.LogTags;
import com.strollimo.android.R;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.controller.AccomplishableController;
import com.strollimo.android.controller.VolleyImageLoader;
import com.strollimo.android.model.MixpanelEvent;
import com.strollimo.android.model.Mystery;
import com.strollimo.android.model.Secret;
import com.strollimo.android.network.AmazonS3Controller;

public class MysteryOpenActivity extends AbstractTrackedActivity {
    private static final String TAG = MysteryOpenActivity.class.getSimpleName();

    public static final String PLACE_ID_EXTRA = "place_id";
    private AccomplishableController mAccomplishableController;
    private Mystery mCurrentMystery;

    public static Intent createDetailsIntent(Context context, String mysteryId) {
        Intent intent = new Intent(context, MysteryOpenActivity.class);
        intent.putExtra(PLACE_ID_EXTRA, mysteryId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAccomplishableController = ((StrollimoApplication) getApplication()).getService(AccomplishableController.class);
        mCurrentMystery = mAccomplishableController.getMysteryById(getIntent().getStringExtra(PLACE_ID_EXTRA));

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.mystery_open_layout);
        findViewById(R.id.open_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StrollimoApplication.getMixpanel().track(MixpanelEvent.OPEN_MYSTERY_SECRETS.toString(), null);

                startActivity(DetailsActivity.createDetailsIntent(MysteryOpenActivity.this, mCurrentMystery.getId()));
            }
        });
        ((TextView) findViewById(R.id.title)).setText(mCurrentMystery.getName().toUpperCase());
        ProgressNetworkImageView detailsPhoto = (ProgressNetworkImageView) findViewById(R.id.detailed_photo);
        String imageUrl = StrollimoApplication.getService(AmazonS3Controller.class).getUrl(mCurrentMystery.getImgUrl());
        detailsPhoto.setImageUrl(imageUrl, findViewById(R.id.detailed_photo_progress));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LogTags.ACCOMPLISHABLES_TAG, "Showing mystery: " + mCurrentMystery.getId());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
