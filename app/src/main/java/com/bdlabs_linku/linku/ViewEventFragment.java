package com.bdlabs_linku.linku;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM2 = "param2";


    public final static String EVENT_POSITION = "Event_Position";
    private Image mEventImage;
    private TextView mEventName;
    private TextView mEventTime;
    private TextView mEventDistance;
    private TextView mEventAttendees;

    // TODO: Rename and change types of parameters
    private int mEventId;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mEventId Parameter 1.
     * @return A new instance of fragment ViewEventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewEventFragment newInstance(int mEventId) {
        ViewEventFragment fragment = new ViewEventFragment();
        Bundle args = new Bundle();
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
        if (getArguments() != null) {
            mEventId = getArguments().getInt(EVENT_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_event, container, false);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), EventModel.EVENTS.get(mEventId).image);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bmp);
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        view.setBackgroundDrawable(bitmapDrawable);

        mEventName = (TextView) view.findViewById(R.id.event_name);
        mEventName.setText(EventModel.EVENTS.get(mEventId).name);

        mEventTime = (TextView) view.findViewById(R.id.time);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

        String formatted = format1.format(EventModel.EVENTS.get(mEventId).time.getTime());
        mEventTime.setText(formatted);

        mEventDistance = (TextView) view.findViewById(R.id.distance);
        mEventDistance.setText(EventModel.EVENTS.get(mEventId).location.getLatitude() + " , " + EventModel.EVENTS.get(mEventId).location.getLatitude());

        mEventAttendees = (TextView) view.findViewById(R.id.attendees);
        mEventAttendees.setText((Integer.toString(EventModel.EVENTS.get(mEventId).attendees)));


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
