package com.bdlabs_linku.linku;

import android.location.Location;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

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

        Calendar time = Calendar.getInstance();

        addEvent(new Event(1, "Coffee @Kozy", time, 4));
        addEvent(new Event(2, "Work in the AntiCafe", time, 2));
        addEvent(new Event(3, "Salsa at Bario Latino", time, 20));
    }

    public static void addEvent(Event e) {
        EVENTS.add(e);
    }

    public static class Event {
        public int id;
        public String name;
        public Calendar time;
        public Location location;
        public int attendees;
        public int image;

        public Event(int id, String name, Calendar time, int attendees) {
            this.id = id;
            this.name = name;
            this.time = time;
            this.location = new Location("");
            this.location.setLatitude(48.860611);
            this.location.setLongitude(2.337644);
            this.attendees = attendees;

            this.image = PICS.get(new Random().nextInt(5));
        }

        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public Calendar getTime() {
            return time;
        }
        
        public void setTime(Calendar time) {
            this.time = time;
        }
        
        public Location getLocation() {
            return location;
        }
        
        public void setLocation(Location location) {
            this.location = location;
        }
        
        public int getAttendees() {
            return attendees;
        }
        
        public void setAttendees(int attendees) {
            this.attendees = attendees;
        }
        
        @Override
        public String toString() {
            return name;
        }

    }
}
