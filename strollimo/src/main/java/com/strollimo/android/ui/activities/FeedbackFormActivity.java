package com.strollimo.android.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.ViewFlipper;

import com.strollimo.android.R;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.core.PreferencesController;
import com.strollimo.android.utils.Analytics;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by marcoc on 26/09/2013.
 */
public class FeedbackFormActivity extends AbstractTrackedActivity {

    private ViewFlipper mViewFlipper;
    private PreferencesController mPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreferences = StrollimoApplication.getService(PreferencesController.class);

        setContentView(R.layout.feedback_activity);

        mViewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
        mViewFlipper.setInAnimation(this, R.anim.slide_in_from_right);
        mViewFlipper.setOutAnimation(this, R.anim.slide_out_to_left);

        Button next = (Button) findViewById(R.id.button_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFirstPageCompleted();
            }
        });

        Button close = (Button) findViewById(R.id.button_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAllCompleted();
            }
        });

    }

    private void onFirstPageCompleted() {
        RatingBar ratingLike = (RatingBar) findViewById(R.id.rating_like);
        RatingBar ratingUseAgain = (RatingBar) findViewById(R.id.rating_use_again);
        RatingBar ratingRecommend = (RatingBar) findViewById(R.id.rating_recommend);


        Map<String,String> params = new HashMap<String, String>();
        params.put(Analytics.Event.FEEDBACK_LIKE.toString(), String.valueOf(ratingLike.getRating()));
        params.put(Analytics.Event.FEEDBACK_USE.toString(), String.valueOf(ratingUseAgain.getRating()));
        params.put(Analytics.Event.FEEDBACK_RECOMMEND.toString(), String.valueOf(ratingRecommend.getRating()));
        Analytics.track(Analytics.Event.FEEDBACK_PAGE1, params);

        Analytics.track(Analytics.Event.FEEDBACK_LIKE, String.valueOf(ratingLike.getRating()));
        Analytics.track(Analytics.Event.FEEDBACK_USE, String.valueOf(ratingUseAgain.getRating()));
        Analytics.track(Analytics.Event.FEEDBACK_RECOMMEND, String.valueOf(ratingRecommend.getRating()));


        mViewFlipper.showNext();
    }

    private void onAllCompleted() {

        RatingBar improve_points = (RatingBar) findViewById(R.id.improve_collect_points_achievements);
        RatingBar improve_find_photos = (RatingBar) findViewById(R.id.improve_find_photos);
        RatingBar improve_discover = (RatingBar) findViewById(R.id.improve_discover_places);
        RatingBar improve_facts = (RatingBar) findViewById(R.id.improve_facts_city);
        RatingBar improve_fun = (RatingBar) findViewById(R.id.improve_fun_friends);
        RatingBar improve_getting_out = (RatingBar) findViewById(R.id.improve_getting_out);


        Map<String,String> params = new HashMap<String, String>();
        params.put(Analytics.Event.FEEDBACK_COLLECT.toString(), String.valueOf(improve_points.getRating()));
        params.put(Analytics.Event.FEEDBACK_FIND_PHOTOS.toString(), String.valueOf(improve_find_photos.getRating()));
        params.put(Analytics.Event.FEEDBACK_DISCOVER_PLACES.toString(), String.valueOf(improve_discover.getRating()));
        params.put(Analytics.Event.FEEDBACK_LEARNING_FACTS.toString(), String.valueOf(improve_facts.getRating()));
        params.put(Analytics.Event.FEEDBACK_FUN.toString(), String.valueOf(improve_fun.getRating()));
        params.put(Analytics.Event.FEEDBACK_GET_OUT.toString(), String.valueOf(improve_getting_out.getRating()));
        Analytics.track(Analytics.Event.FEEDBACK_PAGE2, params);

        Analytics.track(Analytics.Event.FEEDBACK_COLLECT, String.valueOf(improve_points.getRating()));
        Analytics.track(Analytics.Event.FEEDBACK_FIND_PHOTOS, String.valueOf(improve_find_photos.getRating()));
        Analytics.track(Analytics.Event.FEEDBACK_DISCOVER_PLACES, String.valueOf(improve_discover.getRating()));
        Analytics.track(Analytics.Event.FEEDBACK_LEARNING_FACTS, String.valueOf(improve_facts.getRating()));
        Analytics.track(Analytics.Event.FEEDBACK_FUN, String.valueOf(improve_fun.getRating()));
        Analytics.track(Analytics.Event.FEEDBACK_GET_OUT, String.valueOf(improve_getting_out.getRating()));

        mPreferences.setFeedbackCompleted(true);
        finish();
    }
}
