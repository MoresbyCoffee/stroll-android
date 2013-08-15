package com.strollimo.android.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.strollimo.android.R;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.controller.UserService;
import com.strollimo.android.controller.VolleyImageLoader;
import com.strollimo.android.model.Secret;
import com.strollimo.android.network.AmazonS3Controller;

public class SecretCardFragment extends Fragment {
    private final DetailsActivity.OnSecretClickListener mOnSecretClickListener;
    private final UserService mUserService;
    private final int mSecretOrderNum;
    private Secret mSecret;
    private TextView mSecretTitle;
    private ImageView mSecretPhoto;
    private ImageView mCapturedView;
    private TextView mSecretOrder;

    public SecretCardFragment(Secret secret, int position, UserService userService, DetailsActivity.OnSecretClickListener onSecretClickListener) {
        mUserService = userService;
        mSecret = secret;
        mOnSecretClickListener = onSecretClickListener;
        mSecretOrderNum = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.secret_card, container, false);
        if (mSecret == null) {
            return rootView;
        }

        rootView.findViewById(R.id.capture_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSecretClickListener != null) {
                    mOnSecretClickListener.onSecretClicked(mSecret);
                }
            }
        });
        mSecretTitle = (TextView) rootView.findViewById(R.id.secret_title);
        mSecretPhoto = (ImageView) rootView.findViewById(R.id.secret_photo);
        mCapturedView = (ImageView) rootView.findViewById(R.id.captured);
        mSecretOrder = (TextView) rootView.findViewById(R.id.secret_order);

        mSecretOrder.setText("" + mSecretOrderNum);
        mSecretTitle.setText(mSecret.getName().toUpperCase());
        String imageUrl = StrollimoApplication.getService(AmazonS3Controller.class).getUrl(mSecret.getImgUrl());
        //Glide.load(imageUrl).centerCrop().animate(android.R.anim.fade_in).placeholder(R.drawable.closed).into(mSecretPhoto);
        VolleyImageLoader.getInstance().get(imageUrl, ImageLoader.getImageListener(mSecretPhoto, R.drawable.closed, R.drawable.closed));
        refreshView();
        return rootView;
    }

    public void refreshView() {
        boolean isCaptured = mUserService.isSecretCaptured(mSecret.getId());
        if (isCaptured) {
            mCapturedView.setVisibility(View.VISIBLE);
        } else {
            mCapturedView.setVisibility(View.GONE);
        }
    }
}
