package com.strollimo.android.ui.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.core.AccomplishableController;
import com.strollimo.android.core.UserController;
import com.strollimo.android.models.Mystery;
import com.strollimo.android.models.Secret;
import com.strollimo.android.ui.fragments.MysterySecretsFragment;
import com.strollimo.android.ui.fragments.SecretCardFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SecretSlideAdapter extends FragmentStatePagerAdapter {
    private final Context mContext;
    private final AccomplishableController mAccomplishableController;
    private final UserController mUserController;
    private Mystery mMystery;
    private MysterySecretsFragment.OnSecretClickListener mOnSecretClickListener;
    private List<WeakReference<SecretCardFragment>> mFragments;

    public SecretSlideAdapter(FragmentManager fm, Context context, Mystery mystery) {
        super(fm);
        mFragments = new ArrayList<WeakReference<SecretCardFragment>>(mystery.getChildren().size());
        mMystery = mystery;
        mContext = context;
        mAccomplishableController = StrollimoApplication.getService(AccomplishableController.class);
        mUserController = StrollimoApplication.getService(UserController.class);
    }

    @Override
    public Fragment getItem(int position) {
        Secret secret = mAccomplishableController.getSecretById(mMystery.getChildren().get(position));
        SecretCardFragment fragment = new SecretCardFragment(secret, position + 1, mUserController, mOnSecretClickListener);
        mFragments.add(position, new WeakReference<SecretCardFragment>(fragment));
        return fragment;
    }

    @Override
    public int getCount() {
        return mMystery.getChildren().size();
    }

    public void setOnSecretClickListener(MysterySecretsFragment.OnSecretClickListener onSecretClickListener) {
        mOnSecretClickListener = onSecretClickListener;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        refreshViews();
    }

    public void refreshViews() {
        for (WeakReference<SecretCardFragment> cardFragmentWeakReference : mFragments) {
            SecretCardFragment cardFragment = cardFragmentWeakReference.get();
            if (cardFragment != null) {
                cardFragment.refreshView();
            }
        }
    }
}