package com.bdlabs_linku.linku;

import android.app.Activity;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapEventsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapEventsFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "MapEventsFragment";

    private EventsActivity mActivity;

    MapFragment mMapFragment;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private double latitude;
    private double longitude;

    private List<Event> mEvents;
    private boolean mEventsOnMap = false;
    private boolean mFirstZoom = false;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POS = "pager_position";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int pagerPos;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pagerPosition Position of fragment in view pager.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapEventsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapEventsFragment newInstance(int pagerPosition, String param2) {
        MapEventsFragment fragment = new MapEventsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POS, pagerPosition);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MapEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pagerPos = getArguments().getInt(ARG_POS);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_holder, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mActivity = (EventsActivity) activity;

        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            Log.d("MAP", "No map found, setting map.");
            mMapFragment = ((MapFragment) mActivity.getFragmentManager().findFragmentByTag("Map"));
            mMapFragment.getMapAsync(this);
        }
    }

    public void setEvents(List<Event> events) {
        mEvents = events;
        putEventsOnMap();
    }

    public void putEventsOnMap() {
        if(mMap != null && mEvents != null) {
            for(Event event : mEvents) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                String formattedDate = dateFormat.format(event.getTime().getTime());

                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(event.getCategoryMap()))
                        .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                        .position(new LatLng(event.getLocationGeo().getLatitude(), event.getLocationGeo().getLongitude()))
                        .title(event.getTitle())
                        .snippet("at " + formattedDate));
            }

            mEventsOnMap = true;
        }
    }

    public void newLocation(Location loc) {
        if (loc != null && !mFirstZoom) {
            latitude = loc.getLatitude();
            longitude = loc.getLongitude();

            LatLng latLng = new LatLng(latitude, longitude);

            // Move to the current location in Google Map
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

            mFirstZoom = true;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "map ready");
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        Location mCurrentLocation = mActivity.getLastLocation();

        if (mCurrentLocation != null) {
            LatLng myLocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 13));
        }

        putEventsOnMap();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
