package com.strollimo.android.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.strollimo.android.R;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.controller.AccomplishableController;
import com.strollimo.android.controller.UserService;
import com.strollimo.android.model.Mystery;
import com.strollimo.android.model.Secret;
import com.strollimo.android.network.AmazonS3Controller;

public class SecretListAdapter extends BaseAdapter {
    public static final int WIDTH = 200;
    public static final int HEIGHT = 150;
    private final Context mContext;
    private final AccomplishableController mAccomplishableController;
    private final UserService mUserService;
    private Mystery mMystery;

    public SecretListAdapter(Context context, Mystery mystery) {
        mMystery = mystery;
        mContext = context;
        mAccomplishableController = StrollimoApplication.getService(AccomplishableController.class);
        mUserService = StrollimoApplication.getService(UserService.class);

    }

    @Override
    public int getCount() {
        return mMystery.getChildren().size();
    }

    @Override
    public Secret getItem(int i) {
        return mAccomplishableController.getSecretById(mMystery.getChildren().get(i));
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.secret_list_item, viewGroup, false);
        }
        TextView secretTitle = ((TextView)view.findViewById(R.id.secret_title));
        Secret secret = mAccomplishableController.getSecretById(mMystery.getChildren().get(i));
        secretTitle.setText(secret.getName());
        ImageView secretPhoto = ((ImageView)view.findViewById(R.id.secret_photo));
        if (mUserService.isSecretCaptured(secret.getId())) {
            view.findViewById(R.id.captured).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.captured).setVisibility(View.GONE);
        }

        String imageUrl = StrollimoApplication.getService(AmazonS3Controller.class).getUrl(secret.getImgUrl());

        Glide.load(imageUrl).centerCrop().animate(android.R.anim.fade_in).placeholder(R.drawable.closed).into(secretPhoto);

        return view;
    }

}
