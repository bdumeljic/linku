package com.bdlabs_linku.linku;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapEventsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapEventsFragment extends Fragment implements LocationListener {

    EventsActivity mActivity;
    MapFragment mMapFragment;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    LocationManager locationManager;
    String provider;

    double latitude;
    double longitude;


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

        //setUpMapIfNeeded();
        //locationManager.requestLocationUpdates(provider, 500, 1, this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_holder, container, false);

        view.findViewById(R.id.add_event_btn_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getActivity(),CreateNewEventActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        return view;
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
        Log.d("MAP", "Resuming map");
        setUpMapIfNeeded();
    }

    public void onPause(){
        super.onPause();
        locationManager.removeUpdates(this);
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
            mMap = ((MapFragment) mActivity.getFragmentManager().findFragmentByTag("Map")).getMap();

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                Log.d("MAP", "Have map now");

                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.setMyLocationEnabled(true);
                setUpMap();
            }
        } else {
            Log.d("MAP", "There was a map.");

            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setMyLocationEnabled(true);
            setUpMap();
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_sports))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(48.820054,2.338449))
                .title("Gym")
                .snippet("Going to Cité Universitaire's gym at h13:00"));

        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_sports))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(48.835453, 2.382456))
                .title("Football Match")
                .snippet("Having a football match at h17:00"));
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_nightlife))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(48.864267, 2.314326))
                .title("Showcase")
                .snippet("Going to the club at h23:00"));
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_culture))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(48.860745, 2.337585))
                .title("Louvre")
                .snippet("Visiting Paris' biggest museum at h8:30"));
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_coffee))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(48.864798, 2.377710))
                .title("CoffeeBreak")
                .snippet("Meeting to talk and drink a coffee at h16:30"));
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_study))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(48.862134, 2.354248))
                .title("Project")
                .snippet("Study and make the presentation for BDLabs project h13:00"));

        // Enable MyLocation Layer of Google Map
        mMap.setMyLocationEnabled(true);

        // Get LocationManager object from System Service LOCATION_SERVICE
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Get the name of the best provider
        provider = locationManager.getBestProvider(criteria, true);

        // Get Current Location
        Location myLocation = locationManager.getLastKnownLocation(provider);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);



        if(myLocation != null) {
            // set map type

            // Get latitude of the current location
            latitude = myLocation.getLatitude();

            // Get longitude of the current location
            longitude = myLocation.getLongitude();

            // Create a LatLng object for the current location
            LatLng latLng = new LatLng(latitude, longitude);

            // Show the current location in Google Map
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            // Zoom in the Google Map
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        }
    }

    @Override
    public void onLocationChanged(Location loc) {
        latitude = loc.getLatitude();
        // Get longitude of the current location
        longitude = loc.getLongitude();

        LatLng latLng = new LatLng(latitude, longitude);

        // Show the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        // mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("You are here!").snippet("Consider yourself located"));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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
        public void onFragmentInteraction(Uri uri);
    }

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;
        private View layoutInflater;

        MyInfoWindowAdapter() {
            myContentsView = getLayoutInflater(null).inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoContents(Marker marker) {
            TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
            tvTitle.setText(marker.getTitle());
            TextView tvSnippet = ((TextView)myContentsView.findViewById(R.id.snippet));
            tvSnippet.setText(marker.getSnippet());

            //   ImageView ivIcon = ((ImageView)myContentsView.findViewById(R.id.icon));
            //   ivIcon.setImageDrawable(getResources().getDrawable(R.drawable.sports_icon));

            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }
    }
}
