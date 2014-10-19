package com.bdlabs_linku.linku;

import android.location.Location;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Bojana on 13/10/14.
 */
public class EventModel {

    public static List<Event> EVENTS = new ArrayList<Event>();

    static {
        addEvent(new Event(1, "Coffee @Kozy"));
        addEvent(new Event(2, "Work in the AntiCafe"));
    }

    private static void addEvent(Event e) {
        EVENTS.add(e);
    }

    public static class Event {
        public int id;
        public String name;

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

        public Calendar time;
        public Location location;
        public int attendees;

        public Event(int id, String name) {
            this.id = id;
            this.name = name;
            this.time = Calendar.getInstance();
            this.location = new Location("");
            this.location.setLatitude(48.860611);
            this.location.setLongitude(2.337644);
            this.attendees = 0;
        }

        @Override
        public String toString() {
            return name;
        }

    }
}
