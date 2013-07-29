package com.strollimo.android.view;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.strollimo.android.R;

import java.util.HashMap;

public class MainActivity extends FragmentActivity {

    public enum MenuItemFragment {
        MAP("Map", MapFragment.class),
        DEBUG("Debug", DebugFragment.class),
        PROFILE("Profile", null),
        QUESTS("Quests", null),
        ACHIEVEMENTS("Achievements", null),
        ABOUT("About", null);

        private String mLabel;
        private Class<? extends Fragment> mFragment;

        private MenuItemFragment(String label, Class<? extends Fragment> fragment) {
            mLabel = label;
            mFragment = fragment;
        }

        public String getLabel() {
            return mLabel;
        }

        public Class<? extends Fragment> getFragment() {
            return mFragment;
        }

        public static String[] getLabels(){
            MenuItemFragment[] items = values();
            String[] labels = new String[items.length];
            for (int i = 0; i < items.length; i++) {
                labels[i] = items[i].getLabel();
            }
            return labels;
        }
    }

    private ActionBar mActionBar;

    private String mTitle = "Strollimo";

    private String mDrawerTitle = "Menu";
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private HashMap<Class<Fragment>, Fragment> mFragmentCache = new HashMap<Class<Fragment>, Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActionBar = getActionBar();
        mActionBar.setTitle(mTitle);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList = (ListView) findViewById(R.id.main_drawer_list);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.menu_item, MenuItemFragment.getLabels()));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        selectItem(this, 0);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(view.getContext(), position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(Context context, int position) {
        // Create a new fragment and specify the planet to show based on position
        Class<Fragment> fragmentClass = (Class<Fragment>) MenuItemFragment.values()[position].getFragment();
        if (fragmentClass != null) {
            Fragment fragment;
//            if (mFragmentCache.containsKey(fragmentClass)) {
//                fragment = mFragmentCache.get(fragmentClass);
//            } else {
                String fragmentName = fragmentClass.getName();
                fragment = Fragment.instantiate(context, fragmentName);
                mFragmentCache.put(fragmentClass, fragment);
//            }


            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit();

            // Highlight the selected item, update the title, and close the drawer
            mDrawerList.setItemChecked(position, true);
        }
        //setName(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

//    @Override
//    public void setName(CharSequence title) {
//        mTitle = title;
//        getActionBar().setName(mTitle);
//    }

}
