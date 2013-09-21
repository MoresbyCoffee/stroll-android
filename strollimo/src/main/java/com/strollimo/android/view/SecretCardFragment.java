package com.strollimo.android.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.strollimo.android.R;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.StrollimoPreferences;
import com.strollimo.android.controller.SecretStatusPollingService;
import com.strollimo.android.controller.UserService;
import com.strollimo.android.controller.VolleyImageLoader;
import com.strollimo.android.model.BaseAccomplishable;
import com.strollimo.android.model.Secret;
import com.strollimo.android.network.AmazonS3Controller;
import com.strollimo.android.network.AmazonUrl;

public class SecretCardFragment extends Fragment {
    private final static String TAG = SecretCardFragment.class.getSimpleName();

    private final DetailsActivity.OnSecretClickListener mOnSecretClickListener;
    private final UserService mUserService;
    private final int mSecretOrderNum;
    private final StrollimoPreferences mPrefs;
    private Secret mSecret;
    private TextView mSecretTitle;
    private ProgressNetworkImageView mSecretPhoto;
    private TextView mSecretOrder;
    private View mStatusPanel;
    private ImageView mCapturedImg;
    private ImageView mStatusIcon;
    private ProgressBar mStatusPending;
    private ImageView mCaptureButton;

    public SecretCardFragment(Secret secret, int position, UserService userService, DetailsActivity.OnSecretClickListener onSecretClickListener) {
        mPrefs = StrollimoApplication.getService(StrollimoPreferences.class);
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
        mCaptureButton = (ImageView)rootView.findViewById(R.id.capture_button);
        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSecretClickListener != null) {
                    mOnSecretClickListener.onSecretClicked(mSecret);
                }
            }
        });
        mSecretTitle = (TextView) rootView.findViewById(R.id.secret_title);
        mSecretPhoto = (ProgressNetworkImageView) rootView.findViewById(R.id.secret_photo);
        mSecretOrder = (TextView) rootView.findViewById(R.id.secret_order);
        mStatusPanel = rootView.findViewById(R.id.status_panel);
        mCapturedImg = (ImageView)rootView.findViewById(R.id.captured_img);
        mStatusIcon = (ImageView)rootView.findViewById(R.id.status_icon);
        mStatusPending = (ProgressBar)rootView.findViewById(R.id.status_pending);
        mSecretOrder.setText("" + mSecretOrderNum);
        mSecretTitle.setText(mSecret.getName().toUpperCase());
        String imageUrl = StrollimoApplication.getService(AmazonS3Controller.class).getUrl(mSecret.getImgUrl());
        mSecretPhoto.setImageUrl(imageUrl, rootView.findViewById(R.id.secret_photo_progress));
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(SecretStatusPollingService.ACTION_SECRET_STATUS_UPDATED);
        getActivity().registerReceiver(mSecretStatusUpdatedReceiver, filter);
        refreshView();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mSecretStatusUpdatedReceiver);
    }

    private BroadcastReceiver mSecretStatusUpdatedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshView();
        }
    };

    public void refreshView() {
        BaseAccomplishable.PickupState pickupState = mSecret.getPickupState();
        if (pickupState == null) {
            pickupState = BaseAccomplishable.PickupState.UNPICKED;
        }
        final AmazonUrl pickupPhotoUrl = AmazonUrl.createPickupPhotoUrl(mSecret.getId(), mPrefs.getDeviceUUID());
        String imageUrl = StrollimoApplication.getService(AmazonS3Controller.class).getUrl(pickupPhotoUrl.getUrl());
        VolleyImageLoader.getInstance().get(imageUrl, ImageLoader.getImageListener(mCapturedImg, R.drawable.transparent_bg, R.drawable.transparent_bg));

        switch (pickupState) {
            case UNPICKED:
                mStatusPanel.setVisibility(View.GONE);
                mCaptureButton.setEnabled(true);
                break;
            case PENDING:
                mCaptureButton.setEnabled(false);
                mStatusPanel.setVisibility(View.VISIBLE);
                mStatusIcon.setVisibility(View.GONE);
                mStatusPending.setVisibility(View.VISIBLE);
                break;
            case REJECTED:
                mCaptureButton.setEnabled(true);
                mStatusPanel.setVisibility(View.VISIBLE);
                mStatusPending.setVisibility(View.GONE);
                mStatusIcon.setVisibility(View.VISIBLE);
                mStatusIcon.setImageResource(R.drawable.failure);
                break;
            case ACCOMPLISHED:
                mCaptureButton.setEnabled(false);
                mStatusPanel.setVisibility(View.VISIBLE);
                mStatusPending.setVisibility(View.GONE);
                mStatusIcon.setVisibility(View.VISIBLE);
                mStatusIcon.setImageResource(R.drawable.captured);
                break;
            case IN_PROGRESS:
            default:
                mCaptureButton.setEnabled(false);
                break;
        }
    }
}
