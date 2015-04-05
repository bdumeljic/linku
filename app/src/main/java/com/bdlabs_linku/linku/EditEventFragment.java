package com.bdlabs_linku.linku;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.parse.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditEventFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditEventFragment extends Fragment {
    private static final String TAG = "EditEventFragment";
    private OnFragmentInteractionListener mListener ;

    // Input values from view
    EditText mEditTitle;
    EditText mEditDescription;
    AutoCompleteTextView mEditLocation;
    Button mEditDay;
    Button mEditTime;
    Date mEventDate;
    Spinner mCategorySpinner;

    int day = -1;
    int month = -1;
    int year = -1;
    int hour = -1;
    int minute = -1;

    private Event mEvent;
    private String mEventId;

    public ArrayAdapter<String> adapter;

    EditEventActivity mActivity;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment CreateNewEventFragment.
     */
    public static CreateNewEventFragment newInstance() {
        CreateNewEventFragment fragment = new CreateNewEventFragment();
        return fragment;
    }
    public EditEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_edit_event, container, false);

        // Connect to the view
        mEditTitle = (EditText) view.findViewById(R.id.event_title_input);
        mEditDescription = (EditText) view.findViewById(R.id.event_description_input);
        mEditLocation = (AutoCompleteTextView) view.findViewById(R.id.event_location_input);
        mEditDay = (Button) view.findViewById(R.id.event_day_input);
        mEditTime = (Button) view.findViewById(R.id.event_time_input);
        mCategorySpinner = (Spinner) view.findViewById(R.id.category);
        ArrayAdapter<CharSequence> categoryAdapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_item, (List) Event.CATEGORIES);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(categoryAdapter);

        mEventId = mActivity.getEventId();

        mEditTitle.setText(mActivity.getEventTitle());
        mEditDescription.setText(mActivity.getEventDescription());
        mEditLocation.setText(mActivity.getEventLocation());

        //set the timepicker values
        String[] time = mActivity.getEventTime().split(":");
        this.setTime(Integer.parseInt(time[0]), Integer.parseInt(time[1]));
        mEditTime.setText(mActivity.getEventTime());

        //set the datepicker values
        String[] parts = mActivity.getEventDay().split("-");

        Log.d("YEARRRR" , "" + Integer.parseInt(parts[1]));
        this.setDay(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[0]));

        mEditLocation.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.location_list));

        mCategorySpinner.setSelection(mActivity.getEventCategory());

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (EditEventActivity) activity;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void saveEvent() {
        if(validateEvent()) {

            final String title = mEditTitle.getText().toString().trim();

            // Set up a progress dialog
            final ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage(getString(R.string.progress_edit_event));
            dialog.show();

            ParseQuery<Event> query = Event.getQuery();
            query.getInBackground(mEventId, new GetCallback<Event>() {
                public void done(Event event, ParseException e) {
                    if (e == null) {
                        event.setTitle(mEditTitle.getText().toString().trim());
                        event.setDescription(mEditDescription.getText().toString().trim());
                        event.setTime(mEventDate);
                        Log.d("mEventDate", "" + mEventDate);
                        event.setCategory(mCategorySpinner.getSelectedItemPosition());
                        event.setLocation(convertLocation(mEditLocation.getText().toString()));

                        // Save the post
                        event.saveInBackground();
                        dialog.dismiss();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("eventId", event.getObjectId());
                        resultIntent.putExtra("EventTitle", event.getTitle());
                        resultIntent.putExtra("EventDescription", event.getDescription());
                        resultIntent.putExtra("EventDate", event.getTime());
                        resultIntent.putExtra("EventLocation", mEditLocation.getText().toString());
                        resultIntent.putExtra("EventCategory", event.getCategory());
                        getActivity().setResult(Activity.RESULT_OK, resultIntent);
                        getActivity().finish();

                    } else {
                        // something went wrong
                    }
                }
            });
        }
    }

    public ParseGeoPoint convertLocation(String address) {

        ParseGeoPoint geoPoint = new ParseGeoPoint();
        Geocoder gc=new Geocoder(this.getActivity(), Locale.FRANCE);
        List<Address> addresses;
        try {
            addresses=gc.getFromLocationName(address, 5);
            if (addresses.size() > 0) {
                double latitude=addresses.get(0).getLatitude();
                double longitude=addresses.get(0).getLongitude();
                geoPoint.setLatitude(latitude);
                geoPoint.setLongitude(longitude);
            }
        }
        catch (  IOException e) {
            e.printStackTrace();
        }
        return geoPoint;

    }

    public boolean validateEvent() {
        if(mEditTitle.getText().toString().matches("")) {
            Toast.makeText(getActivity(), "Event doesn't have a name.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (day == -1 || hour == -1) {
            Toast.makeText(getActivity(), "Event date or time are not set.", Toast.LENGTH_SHORT).show();
            return false;
        }
        // add description to model
        else if (mEditDescription.getText().toString().matches("")) {
            Toast.makeText(getActivity(), "There is no event description.", Toast.LENGTH_SHORT).show();
            return false;
        }
        // add place to model
        else if (mEditLocation.getText().toString().matches("")) {
            Log.d("LOC","location: " + mEditLocation.getText().toString());
            Toast.makeText(getActivity(), "Location is not provided.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            mEventDate = new Date(year -1900, month, day, hour, minute);
            return true;
        }
    }

    public void setDay(int dayOfMonth, int monthOfYear, int yearPicked) {
        day = dayOfMonth;
        month = monthOfYear;
        year = yearPicked;
        mEditDay.setText(day + " / " + (month + 1) + " / " + year);
        Log.d("DATEPICKED", "" + day + " / " + (month + 1) + " / " + year);
        mEditDay.setTextColor(getResources().getColor(R.color.body_dark));
    }

    public void setTime(int hourPicked, int minutePicked) {
        hour = hourPicked;
        minute = minutePicked;

        Date time = new Date();
        time.setHours(hour);
        time.setMinutes(minute);
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String formattedTime = timeFormat.format(time.getTime());

        mEditTime.setText(formattedTime);
        mEditTime.setTextColor(getResources().getColor(R.color.body_dark));
    }
}
