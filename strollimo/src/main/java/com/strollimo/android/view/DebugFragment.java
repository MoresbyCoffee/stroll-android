package com.strollimo.android.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import com.novoda.imageloader.core.ImageManager;
import com.strollimo.android.AwsActivity;
import com.strollimo.android.R;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.StrollimoPreferences;
import com.strollimo.android.controller.PlacesController;
import com.strollimo.android.model.Mystery;
import com.strollimo.android.network.StrollimoApi;
import com.strollimo.android.network.response.UpdateAccomplishableResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DebugFragment extends Fragment {
    private View mView;
    private Switch mSwitch;
    private StrollimoPreferences mPrefs;
    private PlacesController mPlacesController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPrefs = StrollimoApplication.getService(StrollimoPreferences.class);
        mPlacesController = StrollimoApplication.getService(PlacesController.class);
        if (mView == null) {
            mView = inflater.inflate(R.layout.debug_layout, container, false);
        } else {
            ViewGroup parentViewGroup = (ViewGroup) mView.getParent();
            if (parentViewGroup != null) {
                parentViewGroup.removeAllViews();
            }
        }

        mView.findViewById(R.id.aws_test_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), AwsActivity.class));
            }
        });
        mSwitch = (Switch) mView.findViewById(R.id.debug_mode_switch);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                mPrefs.setDebugModeOn(checked);
            }
        });
        mView.findViewById(R.id.save_data_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlacesController.loadDemoData();
            }
        });
        mView.findViewById(R.id.clear_image_cache_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StrollimoApplication.getService(ImageManager.class).getCacheManager().clean();
            }
        });
        mView.findViewById(R.id.crash_app_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                throw new NullPointerException("Crash test");
            }
        });
        mView.findViewById(R.id.test_something_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testSomething();
            }
        });

        return mView;
    }

    private void testSomething() {
        Mystery mystery = new Mystery("55", "BBtest", 0.4, 0.5, "image URL");
        mystery.setShortDesc("something");
        StrollimoApplication.getService(StrollimoApi.class).updateMystery(mystery, new Callback<UpdateAccomplishableResponse>() {
            @Override
            public void success(UpdateAccomplishableResponse updateAccomplishableResponse, Response response) {
                Log.i("BB", "success");
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.i("BB", "failure");

            }
        });
//        new StrollimoApi().getAccomplishables(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSwitch.setChecked(mPrefs.isDebugModeOn());
    }
}
