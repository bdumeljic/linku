package com.bdlabs_linku.linku;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bdlabs_linku.linku.Adapters.SectionsPagerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class EventsActivity extends ActionBarActivity implements MapEventsFragment.OnFragmentInteractionListener, EventsFragment.OnFragmentInteractionListener, ActionBar.TabListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

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

    public Location mCurrentLocation;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 100000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;

    public EventsAdapter mEventsAdapter;
    // Set up a progress dialog
    public ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLastUpdateTime = "";

        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.progress_load_events));
        dialog.setCanceledOnTouchOutside(false);

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
                        buildGoogleApiClient();
                        mGoogleApiClient.connect();
                        dialog.show();
                        refreshView();
                    }
                }
            });
        } else {
            buildGoogleApiClient();
            dialog.show();
            refreshView();
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle bundle) {
        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        Log.i(TAG, "Connected to GoogleApiClient");
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            //updateUI();
        }

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (LocationListener) this);
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        if (mSectionsPagerAdapter != null && mSectionsPagerAdapter.getFragmentForPosition(1) != null) {
            ((MapEventsFragment) mSectionsPagerAdapter.getFragmentForPosition(1)).newLocation(location);
        }
        Log.i(TAG, "Location changed to " + mCurrentLocation);
    }

    public Location getLastLocation() {
        return mCurrentLocation;
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

    private void refreshView() {
        setContentView(R.layout.activity_events);

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

        // Set up the ViewPager with the sections adapter.
        mViewPager = (FragmentViewPager) findViewById(R.id.pager);

        // Create the adapter that will return a fragment for each of the
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), mViewPager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPlayServices();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }


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
            case R.id.action_about:
                Intent intentAbout = new Intent(EventsActivity.this, AboutActivity.class);
                startActivity(intentAbout);
                break;
            case R.id.action_scan:
                IntentIntegrator intentScan = new IntentIntegrator(this);
                intentScan.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                intentScan.setCaptureLayout(R.layout.custom_capture_layout);
                intentScan.setPrompt(getResources().getString(R.string.scanning));
                if (getResources().getConfiguration().orientation == 2) {
                    intentScan.setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    intentScan.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }

                intentScan.initiateScan();
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
        }

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                super.onActivityResult(requestCode, resultCode, data);
                showUserDialog(result.getContents());
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showUserDialog(String userId) {
        final UserDialog userDialog = new UserDialog(this);
        userDialog.show(getSupportFragmentManager(), "userdialog");

        ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
        userParseQuery.getInBackground(userId, new GetCallback<ParseUser>() {
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    userDialog.setUser(object);
                } else {
                    Log.d("user", "Error: " + e.getMessage());
                    if (userDialog != null) {
                        userDialog.dismiss();
                    }
                }
            }
        });

        ParseQuery<Event> eventQuery = ParseQuery.getQuery(Event.classNameString);
        eventQuery.whereMatchesQuery(Event.creatorString, userParseQuery);
        eventQuery.whereGreaterThan(Event.timeString, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 2)));
        eventQuery.whereLessThan(Event.timeString, new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 2)));
        eventQuery.findInBackground(new FindCallback<Event>() {
            public void done(List<Event> eventList, ParseException e) {
                if (e == null) {
                    Log.d("event", "Retrieved " + eventList.size() + " scores");
                    userDialog.setEvent(eventList);
                } else {
                    Log.d("event", "Error: " + e.getMessage());
                    if (userDialog != null) {
                        userDialog.dismiss();
                    }
                }
            }
        });
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
