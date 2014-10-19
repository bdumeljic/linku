package com.bdlabs_linku.linku;

import android.location.Location;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by Bojana on 13/10/14.
 */
public class EventModel {

    public static List<Event> EVENTS = new ArrayList<Event>();
    public static List<Integer> PICS = new ArrayList<Integer>();

    static {
        PICS.add(R.drawable.bic_pattern);
        PICS.add(R.drawable.diamonds_pattern);
        PICS.add(R.drawable.geo_pattern);
        PICS.add(R.drawable.tri_pattern);
        PICS.add(R.drawable.wood_pattern);


        addEvent(new Event(1, "Coffee @Kozy"));
        addEvent(new Event(2, "Work in the AntiCafe"));
    }

    private static void addEvent(Event e) {
        EVENTS.add(e);
    }

    public static class Event {
        public int id;
        public String name;
        public Calendar time;
        public Location location;
        public int attendees;
        public int image;

        public Event(int id, String name) {
            this.id = id;
            this.name = name;
            this.time = Calendar.getInstance();
            this.location = new Location("");
            this.location.setLatitude(48.860611);
            this.location.setLongitude(2.337644);
            this.attendees = 0;

            this.image = PICS.get(new Random().nextInt(5));
        }

        @Override
        public String toString() {
            return name;
        }

    }
}
