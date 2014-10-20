package com.bdlabs_linku.linku;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
    private OnFragmentInteractionListener mListener;

    // Input values from view
    EditText mEditName;
    DatePicker mDatePicker;
    TimePicker mTimePicker;
    Calendar mEventDate;
    EditText mAttendees;

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

        Button saveButton = (Button) view.findViewById(R.id.btn_save_event);

        // Connect to the view
        mEditName = (EditText) view.findViewById(R.id.event_name_input);
        mTimePicker = (TimePicker) view.findViewById(R.id.event_time);
        mDatePicker = (DatePicker) view.findViewById(R.id.event_date);
        mAttendees = (EditText) view.findViewById(R.id.attendees_event);

        // Save the current input as an event. Check for missing values before saving.
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mEditName.getText().toString().matches("")) {
                    Toast.makeText(getActivity(), "Missing event name", Toast.LENGTH_SHORT).show();
                } else if(mAttendees.getText().toString().matches("")) {
                    Toast.makeText(getActivity(), "Missing maximum number of attendees", Toast.LENGTH_SHORT).show();
                } else {
                    int day = mDatePicker.getDayOfMonth();
                    int month = mDatePicker.getMonth();
                    int year = mDatePicker.getYear();
                    int hour = mTimePicker.getCurrentHour();
                    int min = mTimePicker.getCurrentMinute();

                    mEventDate = Calendar.getInstance();
                    mEventDate.set(year, month, day, hour, min);

                    int maxAttendees = Integer.parseInt(mAttendees.getText().toString());

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

}
