package com.bdlabs_linku.linku;

import android.location.Location;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Bojana on 13/10/14.
 */
public class EventModel {

    public static List<Event> EVENTS = new ArrayList<Event>();

    static {
        addEvent(new Event(1, "Coffee @Kozy", new Date()));
        addEvent(new Event(2, "Work in the AntiCafe", new Date()));
    }

    public static void addEvent(Event e) {
        EVENTS.add(e);
    }

    public static class Event {
        public int id;
        public String name;
        public Date time;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getTime() {
            return time;
        }

        public void setTime(Date time) {
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

        public Location location;
        public int attendees;

        public Event(int id, String name, Date time) {
            this.id = id;
            this.name = name;
            this.time = time;
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
