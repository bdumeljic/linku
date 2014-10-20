package com.bdlabs_linku.linku;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewEventFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ViewEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ViewEventFragment extends Fragment {

    public final static String EVENT_POSITION = "Event_Position";
    private Image mEventImage;
    private TextView mEventName;
    private TextView mEventTime;
    private TextView mEventDistance;
    private TextView mEventAttendees;

    private int mEventId;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mEventId Event ID for item in Events ArrayList.
     * @return A new instance of fragment ViewEventFragment.
     */
    public static ViewEventFragment newInstance(int mEventId) {
        ViewEventFragment fragment = new ViewEventFragment();
        Bundle args = new Bundle();

        // Get ID of event that is shown in this activity
        args.putInt(EVENT_POSITION, mEventId);
        fragment.setArguments(args);
        return fragment;
    }
    public ViewEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get ID of event that is shown in this activity
        if (getArguments() != null) {
            mEventId = getArguments().getInt(EVENT_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_event, container, false);

        // Set background image of the event item
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), EventModel.EVENTS.get(mEventId).image);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bmp);
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        view.setBackgroundDrawable(bitmapDrawable);

        // Set event name
        mEventName = (TextView) view.findViewById(R.id.event_name);
        mEventName.setText(EventModel.EVENTS.get(mEventId).name);

        // Set event time
        mEventTime = (TextView) view.findViewById(R.id.time);
        DateFormat dateFormat = new SimpleDateFormat("HH:mm, d MMM yyyy");
        String formattedDate = dateFormat.format(EventModel.EVENTS.get(mEventId).time.getTime());
        mEventTime.setText(formattedDate);

        // Set event distance from user's current location
        // TODO add location
        mEventDistance = (TextView) view.findViewById(R.id.distance);
        mEventDistance.setText("2 km away");

        // Set number of people attending
        mEventAttendees = (TextView) view.findViewById(R.id.attendees);
        mEventAttendees.setText((Integer.toString(EventModel.EVENTS.get(mEventId).attendees)) + " attendees");


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
        public void onFragmentInteraction(int mEventId);
    }

}
