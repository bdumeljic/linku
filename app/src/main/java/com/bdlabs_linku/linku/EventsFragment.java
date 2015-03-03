package com.bdlabs_linku.linku;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.identity.intents.AddressConstants;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.text.SimpleDateFormat;

/**
 * A fragment representing a list of Event Items.
 *
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class EventsFragment extends Fragment implements ListView.OnItemClickListener {

    private static final String TAG = "EventsFragment";
    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView containing the events.
     */
    private ListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Event List Item Views.
     */
    //private EventsAdapter mAdapter;

    // Adapter for the Parse query
    private ParseQueryAdapter<Event> postsQueryAdapter;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // When this fragment is created the adapter is populated with events from the EventModel
        //mAdapter = new EventsAdapter(EventModel.EVENTS);

        // Set up a customized query
        ParseQueryAdapter.QueryFactory<Event> factory =
                new ParseQueryAdapter.QueryFactory<Event>() {
                    public ParseQuery<Event> create() {
                        ParseQuery<Event> query = Event.getQuery();
                        //query.include("user");
                        query.orderByDescending("createdAt");
                        //query.whereWithinKilometers("location", geoPointFromLocation(myLoc), radius
                         //       * METERS_PER_FEET / METERS_PER_KILOMETER);
                        query.setLimit(20);
                        return query;
                    }
                };

        // Set up the query adapter
        postsQueryAdapter = new ParseQueryAdapter<Event>(getActivity(), factory) {
            @Override
            public View getItemView(Event event, View view, ViewGroup parent) {
                if (view == null) {
                    view = View.inflate(getContext(), R.layout.events_list_item, null);
                }

                TextView title = (TextView) view.findViewById(R.id.event_name);
                title.setText(event.getTitle());

                TextView time = (TextView) view.findViewById(R.id.event_time);
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                String formattedDate = dateFormat.format(event.getTime().getTime());
                time.setText(formattedDate);

                // Set number of people attending
                TextView attendees = (TextView) view.findViewById(R.id.attendees);
                int attend = event.getAttending();
                if(attend < 1) {
                    attendees.setText("Be the first to join!");
                } else if(attend == 1) {
                    attendees.setText("One person is going");
                } else {
                    attendees.setText(String.valueOf(event.getAttending()) + " people are going");
                }

                ImageView cat = (ImageView) view.findViewById(R.id.event_cat);
                cat.setImageResource(event.getCategoryIcon());


                return view;
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events_list, container, false);

        view.findViewById(R.id.add_event_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),CreateNewEventActivity.class);
                startActivityForResult(intent, EventsActivity.CREATE_EVENT);
            }
        });

        // Set the list adapter
        mListView = (ListView) view.findViewById(android.R.id.list);
        //mListView.setAdapter(mAdapter);
        mListView.setAdapter(postsQueryAdapter);


        // Create a ListView-specific touch listener. ListViews are given special treatment because
        // by default they handle touches for their list items... i.e. they're in charge of drawing
        // the pressed state (the list selector), handling list item clicks, etc.
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        mListView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    //mAdapter.remove(position);

                                }
                                //mAdapter.notifyDataSetChanged();
                                setEmptyText();
                            }
                });

        mListView.setOnTouchListener(touchListener);

        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        mListView.setOnScrollListener(touchListener.makeScrollListener());

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // Start the View Event Activity that show the clicked event
        Intent intent = new Intent(getActivity(), ViewEventActivity.class);
        intent.putExtra(ViewEventActivity.EVENT_ID, postsQueryAdapter.getItem(position).getObjectId());
        startActivity(intent);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the event was created successfully then update the adapter
       // if (requestCode == 1) {
            //mAdapter.notifyDataSetChanged();
         //   setEmptyText();
        //}
        switch (requestCode) {
            case EventsActivity.CREATE_EVENT:
                if (resultCode == Activity.RESULT_OK) {
                    // Event was added successfully, update list
                    Log.d(TAG, "event added " + data.getStringExtra("eventId").toString());
                    postsQueryAdapter.loadObjects();
                    mListView.setAdapter(postsQueryAdapter);
                }
                return;
        }

        super.onActivityResult(requestCode, resultCode, data);
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
            /*if (mAdapter.isEmpty()) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.INVISIBLE);
            }*/
        }
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

    // Adapter for events
    /*private class EventsAdapter extends BaseAdapter {
        private List<EventModel.Event> events;

        public EventsAdapter(List<EventModel.Event> events) {
            super();
            this.events = events;
        }

        @Override
        public int getCount() {
            return events.size();
        }

        @Override
        public Object getItem(int position) {
            return events.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.events_list_item, parent, false);
            }

            /*
            // Set background image of the event item
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), events.get(position).image);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(bmp);
            bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            convertView.setBackgroundDrawable(bitmapDrawable);
            /
            // Set event name
            TextView name = (TextView) convertView.findViewById(R.id.event_name);
            name.setText(events.get(position).name);


            // Set event time
            TextView time = (TextView) convertView.findViewById(R.id.event_time);
            DateFormat dateFormat = new SimpleDateFormat("HH:mm");
            String formattedDate = dateFormat.format(events.get(position).time.getTime());
            time.setText(formattedDate);

            // Set event distance from user's current location
            // TODO add location
            TextView distance = (TextView) convertView.findViewById(R.id.event_place);
            distance.setText(events.get(position).locName + " - " + events.get(position).dist);

            // Set number of people attending
            TextView attendees = (TextView) convertView.findViewById(R.id.attendees);
            if(events.get(position).attendees == 0) {
                attendees.setText("Be the first to join!");
            } else {
                attendees.setText(String.valueOf(events.get(position).attendees) + " people are going");
            }

            ImageView cat = (ImageView) convertView.findViewById(R.id.event_cat);
            cat.setImageResource(events.get(position).getCategoryIcon());

            return convertView;
        }

        // Remove this event from the adapter
        public void remove(int position) {
            events.remove(position);
            return;
        }
    }*/



}
