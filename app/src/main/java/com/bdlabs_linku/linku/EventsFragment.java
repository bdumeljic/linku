package com.bdlabs_linku.linku;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * A fragment representing a list of Event Items.
 *
 * Activities containing this fragment MUST implement the {@link com.bdlabs_linku.linku.ObservableScrollView.Callbacks}
 * interface.
 */
public class EventsFragment extends Fragment implements RecyclerView.OnItemTouchListener {

    private static final String TAG = "EventsFragment";
    private OnFragmentInteractionListener mListener;

    private EventsActivity mActivity;

    private RecyclerView mList;
    private RecyclerView.LayoutManager mLayoutManager;
    private GestureDetector detector;

    public static EventsFragment newInstance() {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events_list, container, false);

        detector = new GestureDetector(getActivity(), new RecyclerViewOnGestureListener());

        mList = (RecyclerView) view.findViewById(R.id.list);
        mList.addOnItemTouchListener(this);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mList.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mList.setAdapter(mActivity.mEventsAdapter);

        return view;
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

    /**
     * The default name for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText() {
        View emptyView = getView().findViewById(R.id.empty_events);
        if(emptyView != null) {
            if (mActivity.mEventsAdapter.isEmpty()) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.INVISIBLE);
            }
        }
    }

    private class RecyclerViewOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            View view = mList.findChildViewUnder(e.getX(), e.getY());
            int position = mList.getChildPosition(view);

            // handle single tap
            Intent intent = new Intent(getActivity(), ViewEventActivity.class);
            intent.putExtra(ViewEventActivity.EVENT_ID, mActivity.mEventsAdapter.getItem(position).getObjectId());
            intent.putExtra(EventsActivity.USER_LOC, mActivity.getLastLocation());
            startActivity(intent);

            return super.onSingleTapConfirmed(e);
        }

        public void onLongPress(MotionEvent e) {
            View view = mList.findChildViewUnder(e.getX(), e.getY());
            int position = mList.getChildPosition(view);

            // handle long press

            super.onLongPress(e);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        detector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * " "
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String name);
    }
}
