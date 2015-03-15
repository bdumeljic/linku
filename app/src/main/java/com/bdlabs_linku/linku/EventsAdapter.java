package com.bdlabs_linku.linku;

import android.content.Context;
import android.location.Location;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventsAdapter extends ParseQueryAdapter<Event> {

    private EventsActivity mContext;

    public EventsAdapter(Context context) {
        super(context, new QueryFactory<Event>() {
            @Override
            public ParseQuery<Event> create() {
                ParseQuery<Event> query = Event.getQuery();
                query.orderByAscending("time");
                query.whereGreaterThanOrEqualTo("time", new Date(System.currentTimeMillis()));
                query.setLimit(30);
                return query;
            }
        });

        this.mContext = (EventsActivity) context;
    }

    @Override
    public Event getItem(int index) {
        return super.getItem(index);
    }

    @Override
    public View getItemView(Event event, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.events_list_item, null);
        }

        TextView title = (TextView) v.findViewById(R.id.event_name);
        title.setText(event.getTitle());

        TextView time = (TextView) v.findViewById(R.id.event_time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String formattedDate = dateFormat.format(event.getTime().getTime());
        time.setText(formattedDate);

        // Set number of people attending
        TextView attendees = (TextView) v.findViewById(R.id.attendees);
        int attend = event.getAttending();
        boolean mGoing = event.isAlreadyAttending();
        if(attend < 1) {
            attendees.setText("Be the first to join!");
        } else if(attend == 1 && mGoing) {
            attendees.setText("You are going.");
        } else if(attend == 1) {
            attendees.setText("One person is going");
        } else if (attend == 2 && mGoing) {
            attendees.setText("You and one other person are going.");
        } else if (mGoing) {
            attendees.setText("You and " + String.valueOf(event.getAttending() - 1) + " other people are going.");
        } else {
            attendees.setText(String.valueOf(event.getAttending()) + " people are going");
        }

        ImageView cat = (ImageView) v.findViewById(R.id.event_cat);
        cat.setImageResource(event.getCategoryIcon());

        if (mContext.getLastLocation() != null) {
            TextView location = (TextView) v.findViewById(R.id.event_place);

            ParseGeoPoint locEvent = event.getLocation();
            location.setText(parseDistance(locEvent));
        }
        return v;
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
}
