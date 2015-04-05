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
        eventViewHolder.secondary.setText(formattedDate);

        eventViewHolder.cat.setImageResource(event.getCategoryIcon());

        if (mContext.getLastLocation() != null) {
            ParseGeoPoint locEvent = event.getLocation();
            eventViewHolder.secondary.append(" - " + parseDistance(locEvent));
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
        TextView secondary;
        ImageView cat;

        public EventViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.event_name);
            secondary = (TextView) v.findViewById(R.id.event_secondary);
            cat = (ImageView) v.findViewById(R.id.event_cat);
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
