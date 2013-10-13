package com.strollimo.android.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.strollimo.android.R;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.model.Mystery;
import com.strollimo.android.network.AmazonS3Controller;
import com.strollimo.android.util.Analytics;
import com.strollimo.android.util.DebugModeController;

public class MysterySplashFragment extends Fragment {

    private Mystery mCurrentMystery;
    private ViewPager mMainViewPager;

    public MysterySplashFragment() {

    }

    public MysterySplashFragment(Mystery mystery, ViewPager mainViewPager) {
        mCurrentMystery = mystery;
        mMainViewPager = mainViewPager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.mystery_open_layout, container, false);


        ((TextView) rootView.findViewById(R.id.title)).setText(mCurrentMystery.getName().toUpperCase());
        ProgressNetworkImageView detailsPhoto = (ProgressNetworkImageView) rootView.findViewById(R.id.detailed_photo);
        String imageUrl = StrollimoApplication.getService(AmazonS3Controller.class).getUrl(mCurrentMystery.getImgUrl());
        detailsPhoto.setImageUrl(imageUrl, rootView.findViewById(R.id.detailed_photo_progress));
        new DebugModeController(detailsPhoto, getActivity());

        return rootView;
    }
}
