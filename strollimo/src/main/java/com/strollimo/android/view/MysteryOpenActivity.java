package com.strollimo.android.view;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import com.strollimo.android.LogTags;
import com.strollimo.android.R;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.StrollimoPreferences;
import com.strollimo.android.controller.AccomplishableController;
import com.strollimo.android.model.Mystery;
import com.strollimo.android.model.Secret;
import com.strollimo.android.network.AmazonS3Controller;
import com.strollimo.android.util.Analytics;
import com.strollimo.android.util.DebugModeController;

public class MysteryOpenActivity extends AbstractTrackedFragmentActivity {
    private static final String TAG = MysteryOpenActivity.class.getSimpleName();

    public static final String PLACE_ID_EXTRA = "place_id";

    private AccomplishableController mAccomplishableController;
    private StrollimoPreferences mPrefs;
    private Mystery mCurrentMystery;

    private ViewPager mMainViewPager;

    private Secret mSelectedSecret;
    private ViewPager mSecretsViewPager;
    private PagerAdapter mSecretsPagerAdapter;
    private MysterySecretsFragment mMysterySecretsFragment;

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
        mPrefs = ((StrollimoApplication) getApplication()).getService(StrollimoPreferences.class);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        String title = mCurrentMystery == null ? "Error" : mCurrentMystery.getName().toUpperCase();
        actionBar.setTitle(title);

        mMainViewPager = new ViewPager(this) {

            private float lastX;

            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                final int action = ev.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = ev.getX();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (getCurrentItem() != 0) {
                            if (mMysterySecretsFragment.getViewPager()!=null && mMysterySecretsFragment.getViewPager().getCurrentItem() == 0) {
                                if (lastX <= ev.getX()) {
                                    return super.onInterceptTouchEvent(ev);
                                } else {
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        }
                }

                return super.onInterceptTouchEvent(ev);
            }
        };
        mMainViewPager.setPageTransformer(true, new ViewPager.PageTransformer() {

            @Override
            public void transformPage(View view, float position) {
                int pageWidth = view.getWidth();

                if (position < -1) { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    view.setAlpha(0);

                } else if (position <= 0) { // [-1,0]
                    // Use the default slide transition when moving to the left page
                    view.setTranslationX(0);

                } else if (position <= 1) { // (0,1]
                    // Counteract the default slide transition
                    view.setTranslationX(pageWidth * -position);

                } else { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    view.setAlpha(0);
                }
            }
        });
        mMainViewPager.setId(R.id.mystery_main_viewpager);

        mMysterySecretsFragment = new MysterySecretsFragment(mCurrentMystery);
        mSecretsPagerAdapter = mMysterySecretsFragment.getPagerAdapter();
        mSecretsViewPager = mMysterySecretsFragment.getViewPager();
        MysterySplashFragment mysterySplashFragment = new MysterySplashFragment(mCurrentMystery, mMainViewPager);
        Fragment[]  mainFragments = new Fragment[]{mysterySplashFragment, mMysterySecretsFragment};
        MysteryPagerAdapter mainAdapter = new MysteryPagerAdapter(getSupportFragmentManager(), mainFragments);
        mMainViewPager.setAdapter(mainAdapter);

        setContentView(mMainViewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LogTags.ACCOMPLISHABLES_TAG, "Showing mystery: " + mCurrentMystery.getId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mPrefs.isDebugModeOn()) {
            getMenuInflater().inflate(R.menu.main_options, menu);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mPrefs.isDebugModeOn()) {
            menu.findItem(R.id.use_barcode).setChecked(mPrefs.isUseBarcode());
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.use_barcode:
                boolean checked = item.isChecked();
                mPrefs.setUseBarcode(!checked);
                item.setChecked(!checked);
                return true;
            case R.id.add_secret:
                launchAddSecret();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void launchAddSecret() {
        Intent intent = new Intent(this, AddSecretActivity.class);
        intent.putExtra(PLACE_ID_EXTRA, mCurrentMystery.getId());
        startActivity(intent);
    }

    private static class MysteryPagerAdapter extends FragmentStatePagerAdapter {

        Fragment[] mFragments;

        public MysteryPagerAdapter(FragmentManager fm, Fragment... fragments) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int i) {
           return mFragments[i];
        }

        @Override
        public int getCount() {
            return mFragments.length;
        }
    }

    private static class MysterySplashFragment extends Fragment {

        private Mystery mCurrentMystery;
        private ViewPager mMainViewPager;

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
            detailsPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Analytics.track(Analytics.Event.OPEN_MYSTERY_SECRETS);

                    mMainViewPager.setCurrentItem(1, true);
                }
            });


            rootView.findViewById(R.id.open_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Analytics.track(Analytics.Event.OPEN_MYSTERY_SECRETS);

                    mMainViewPager.setCurrentItem(1, true);
                }
            });
            return rootView;
        }
    }
}
