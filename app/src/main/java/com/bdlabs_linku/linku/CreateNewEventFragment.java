package com.bdlabs_linku.linku;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateNewEventFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateNewEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class CreateNewEventFragment extends Fragment {
    private OnFragmentInteractionListener mListener ;

    // Input values from view
    EditText mEditName;
    EditText mEditDescription;
    EditText mEditLocation;
    Button mEditDay;
    Button mEditTime;
    DatePicker mDatePicker;
    TimePicker mTimePicker;
    Calendar mEventDate;
    EditText mAttendees;

    int day = -1;
    int month = -1;
    int year = -1;
    int hour = -1;
    int minute = -1;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment CreateNewEventFragment.
     */
    public static CreateNewEventFragment newInstance() {
        CreateNewEventFragment fragment = new CreateNewEventFragment();
        return fragment;
    }
    public CreateNewEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create_new_event, container, false);

        // Connect to the view
        mEditName = (EditText) view.findViewById(R.id.event_title_input);
        mEditDescription = (EditText) view.findViewById(R.id.event_description_input);
        mEditLocation = (EditText) view.findViewById(R.id.event_location_input);
        mEditDay = (Button) view.findViewById(R.id.event_day_input);
        mEditTime = (Button) view.findViewById(R.id.event_time_input);

        mTimePicker = (TimePicker) view.findViewById(R.id.event_time);
        mDatePicker = (DatePicker) view.findViewById(R.id.event_date);
        mAttendees = (EditText) view.findViewById(R.id.attendees_event);


        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
        if(mEditName.getText().toString().matches("")) {
            Toast.makeText(getActivity(), "Missing event title", Toast.LENGTH_SHORT).show();
        }
        else if (day == -1 || hour == -1) {
            Toast.makeText(getActivity(), "Missing event date and time", Toast.LENGTH_SHORT).show();
        }
        // add description to model
        else if (mEditDescription.getText().toString().matches("")) {
            Toast.makeText(getActivity(), "Missing event description", Toast.LENGTH_SHORT).show();
        }
        // add place to model
        else if (mEditLocation.getText().toString().matches("")) {
            Toast.makeText(getActivity(), "Missing event location", Toast.LENGTH_SHORT).show();
        }
        else {
            mEventDate = Calendar.getInstance();
            mEventDate.set(year, month, day, hour, minute);

            // we don't need this , put a random 10 people just for debugging
            int maxAttendees = 10;

            // Add event to the model
            EventModel.addEvent(
                    new EventModel.Event(
                            EventModel.EVENTS.size(),
                            mEditName.getText().toString(),
                            mEventDate,
                            maxAttendees));
            getActivity().finish();
        }
    }

    public void setDay(int dayOfMonth, int monthOfYear, int yearPicked) {
        day = dayOfMonth;
        month = monthOfYear;
        year = yearPicked;
        mEditDay.setText(dayOfMonth + " / " + monthOfYear);
        mEditDay.setTextColor(getResources().getColor(R.color.body_dark));
    }

    public void setTime(int hourPicked, int minutePicked) {
        hour = hourPicked;
        minute = minutePicked;
        mEditTime.setText(hour + ":" + minute);
        mEditTime.setTextColor(getResources().getColor(R.color.body_dark));
    }

}
