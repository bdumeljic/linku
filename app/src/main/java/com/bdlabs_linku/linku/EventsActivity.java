package com.bdlabs_linku.linku;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.List;
import java.util.Locale;

public class EventsActivity extends ActionBarActivity implements MapEventsFragment.OnFragmentInteractionListener, EventsFragment.OnFragmentInteractionListener, ActionBar.TabListener {

    static final int EDIT_EVENT = 2;
    public static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
    public static final int CREATE_EVENT = 1;

    public static final String EVENT_ID = "EventID";

    static final String USER_LOC = "user_location";

    private static final String TAG = "EventsActivity";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    private FragmentViewPager mViewPager;

    public Location mUserLocation;
    public ProviderLocationTracker mLocationTracker;
    public LocationTracker.LocationUpdateListener mLoclistener = new LocationTracker.LocationUpdateListener() {
        @Override
        public void onUpdate(Location oldLoc, long oldTime, Location newLoc, long newTime) {
            mUserLocation = newLoc;
            Log.d(TAG, "loc update " + mUserLocation.toString());
        }
    };

    public EventsAdapter mEventsAdapter;
    // Set up a progress dialog
    public ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.progress_load_events));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setLogo(R.drawable.ic_hedge);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        if (!isNetworkAvailable()) {
            setContentView(R.layout.no_internet);
            final Button refreshView = (Button) findViewById(R.id.refresh_button);
            refreshView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (isNetworkAvailable()) {
                        refreshView();
                    }
                }
            });
        } else {
            refreshView();
        }
    }

    private void refreshView() {
        setContentView(R.layout.activity_events);

        // Start location tracking.
        mLocationTracker = new ProviderLocationTracker(getApplicationContext(), ProviderLocationTracker.ProviderType.NETWORK);
        if(mLocationTracker.getLocationManager() != null) {
            //mLocationTracker.start(mLoclistener);
        }

        // Start loading events.
        mEventsAdapter = new EventsAdapter(this, null);
        mEventsAdapter.getParseAdapter().addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<Event>() {
            @Override
            public void onLoading() {
                dialog.show();
            }

            @Override
            public void onLoaded(List<Event> events, Exception e) {
                mEventsAdapter.notifyDataSetChanged();

                dialog.dismiss();

                if (mSectionsPagerAdapter.getFragmentForPosition(0).isAdded()) {
                    ((EventsFragment) mSectionsPagerAdapter.getFragmentForPosition(0)).setEmptyText();
                }

                if (mSectionsPagerAdapter.getFragmentForPosition(1).isAdded()) {
                    ((MapEventsFragment) mSectionsPagerAdapter.getFragmentForPosition(1)).setEvents(events);
                }
            }
        });
        mEventsAdapter.getParseAdapter().loadObjects();

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        findViewById(R.id.add_event_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), CreateNewEventActivity.class);
                startActivityForResult(intent, CREATE_EVENT);
            }
        });

        // Create the adapter that will return a fragment for each of the
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (FragmentViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Disable swiping.
        /*mViewPager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });*/

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPlayServices();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.events, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                // Call the Parse log out method
                ParseUser.logOut();
                // Start and intent for the dispatch activity
                Intent intent = new Intent(EventsActivity.this, DispatchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.action_refresh:
                mEventsAdapter.getParseAdapter().loadObjects();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_RECOVER_PLAY_SERVICES:
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Google Play Services must be installed.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            case CREATE_EVENT:
                if (resultCode == RESULT_OK) {
                    if(data.getStringExtra(EVENT_ID) != null) {
                        // Event was added successfully, update list
                        Log.d(TAG, "event added " + data.toString());
                        mEventsAdapter.getParseAdapter().loadObjects();
                    }
                }

                break;
            // TODO notifyItemChanged(int position)
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public Location getLastLocation() {
        return mUserLocation;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * Make sure Google Play Services are available so map can run.
     * @return
     */
    private void checkPlayServices() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
                showErrorDialog(status);
            } else {
                Toast.makeText(this, "This device is not supported.",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    void showErrorDialog(int code) {
        GooglePlayServicesUtil.getErrorDialog(code, this,
                REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    public FragmentViewPager getViewPager() {
        return mViewPager;
    }

    public SectionsPagerAdapter getPagerAdapter() {
        return mSectionsPagerAdapter;
    }

    /**
     * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch(position) {
                case 0:
                    return EventsFragment.newInstance();
                case 1:
                    return MapEventsFragment.newInstance(position,"");
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_feed).toUpperCase(l);
                case 1:
                    return getString(R.string.title_map).toUpperCase(l);
            }
            return null;
        }

        /**
         * @return may return null if the fragment has not been instantiated yet for that position - this depends on if the fragment has been viewed
         * yet OR is a sibling covered by {@link android.support.v4.view.ViewPager#setOffscreenPageLimit(int)}. Can use this to call methods on
         * the current positions fragment.
         */
        public @Nullable Fragment getFragmentForPosition(int position) {
            String tag = mViewPager.makeFragmentName(mViewPager.getId(), getItemId(position));
            return getSupportFragmentManager().findFragmentByTag(tag);
        }
    }

    // event fragment
    @Override
    public void onFragmentInteraction(String name) {
        //Toast.makeText(getApplicationContext(), name + ". Great choice!", Toast.LENGTH_SHORT).show();
    }

    // map fragment
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
