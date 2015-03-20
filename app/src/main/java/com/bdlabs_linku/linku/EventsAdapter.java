package com.bdlabs_linku.linku;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private EventsActivity mContext;
    private ParseQueryAdapter<Event> parseAdapter;

    private ViewGroup parseParent;

    public EventsAdapter(Context context, ViewGroup parentIn) {
        this.mContext = (EventsActivity) context;
        this.parseParent = parentIn;
        this.parseAdapter = new ParseQueryAdapter<>(context, new ParseQueryAdapter.QueryFactory<Event>() {
            @Override
            public ParseQuery<Event> create() {
                ParseQuery<Event> query = Event.getQuery();
                query.orderByAscending("time");
                query.whereGreaterThanOrEqualTo("time", new Date(System.currentTimeMillis()));
                query.setLimit(30);
                return query;
            }
        });
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.events_list_item, parent, false);
        EventViewHolder vh = new EventViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(EventViewHolder eventViewHolder, int i) {
        Event event = parseAdapter.getItem(i);

        eventViewHolder.title.setText(event.getTitle());

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String formattedDate = dateFormat.format(event.getTime().getTime());
        eventViewHolder.time.setText(formattedDate);

        // Set number of people attending
        int attend = event.getAttending();
        boolean mGoing = event.isAlreadyAttending();
        if(attend < 1) {
            eventViewHolder.attendees.setText("Be the first to join!");
        } else if(attend == 1 && mGoing) {
            eventViewHolder.attendees.setText("You are going.");
        } else if(attend == 1) {
            eventViewHolder.attendees.setText("One person is going");
        } else if (attend == 2 && mGoing) {
            eventViewHolder.attendees.setText("You and one other person are going.");
        } else if (mGoing) {
            eventViewHolder.attendees.setText("You and " + String.valueOf(event.getAttending() - 1) + " other people are going.");
        } else {
            eventViewHolder.attendees.setText(String.valueOf(event.getAttending()) + " people are going");
        }

        eventViewHolder.cat.setImageResource(event.getCategoryIcon());

        if (mContext.getLastLocation() != null) {
            ParseGeoPoint locEvent = event.getLocation();
            eventViewHolder.location.setText(parseDistance(locEvent));
        }
    }


    public Event getItem(int position) {
        return parseAdapter.getItem(position);
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    @Override
    public int getItemCount() {
        return parseAdapter.getCount();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView time;
        TextView attendees;
        ImageView cat;
        TextView location;

        public EventViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.event_name);
            time = (TextView) v.findViewById(R.id.event_time);
            attendees = (TextView) v.findViewById(R.id.attendees);
            cat = (ImageView) v.findViewById(R.id.event_cat);
            location = (TextView) v.findViewById(R.id.event_place);
        }
    }

    private String parseDistance(ParseGeoPoint locEvent) {
        Location mUserLocation = mContext.getLastLocation();
        double distance = locEvent.distanceInKilometersTo(new ParseGeoPoint(mUserLocation.getLatitude(), mUserLocation.getLongitude()));

        if (distance < 1.0) {
            distance *= 1000;
            int dist = (int) (Math.ceil(distance / 5d) * 5);
            return String.valueOf(dist) + " m";
        } else {
            BigDecimal bd = new BigDecimal(distance).setScale(1, RoundingMode.HALF_UP);
            distance = bd.doubleValue();
            return String.valueOf(distance) + " km";
        }
    }

    public ParseQueryAdapter<Event> getParseAdapter() {
        return parseAdapter;
    }
}
