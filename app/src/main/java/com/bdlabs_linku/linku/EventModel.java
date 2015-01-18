package com.bdlabs_linku.linku;

import android.location.Location;

import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.Vector;

public class EventModel {

    public static List<Event> EVENTS = new ArrayList<Event>();
    public static List<Integer> PICS = new ArrayList<Integer>();
    public static List<String> CATEGORIES = new ArrayList<String>();
    public static List<Integer> CATEGORIES_ICONS = new ArrayList<Integer>();
    public static List<Integer> CATEGORIES_MAP = new ArrayList<Integer>();

    static {
        PICS.add(R.drawable.bic_pattern);
        PICS.add(R.drawable.diamonds_pattern);
        PICS.add(R.drawable.geo_pattern);
        PICS.add(R.drawable.tri_pattern);
        PICS.add(R.drawable.wood_pattern);

        CATEGORIES.add("Outdoor");
        CATEGORIES.add("Food");
        CATEGORIES.add("Coffee");
        CATEGORIES.add("Shopping");
        CATEGORIES.add("Nightlife");
        CATEGORIES.add("Culture");
        CATEGORIES.add("Sports");
        CATEGORIES.add("Music");
        CATEGORIES.add("Study");
        CATEGORIES.add("Cinema");
        CATEGORIES.add("Other");

        CATEGORIES_ICONS.add(R.drawable.ic_outdoor);
        CATEGORIES_ICONS.add(R.drawable.ic_food);
        CATEGORIES_ICONS.add(R.drawable.ic_coffee);
        CATEGORIES_ICONS.add(R.drawable.ic_shopping);
        CATEGORIES_ICONS.add(R.drawable.ic_nightlife);
        CATEGORIES_ICONS.add(R.drawable.ic_culture);
        CATEGORIES_ICONS.add(R.drawable.ic_sports);
        CATEGORIES_ICONS.add(R.drawable.ic_music);
        CATEGORIES_ICONS.add(R.drawable.ic_study);
        CATEGORIES_ICONS.add(R.drawable.ic_cinema);
        CATEGORIES_ICONS.add(R.drawable.ic_other);

        CATEGORIES_MAP.add(R.drawable.ic_map_outdoor);
        CATEGORIES_MAP.add(R.drawable.ic_map_food);
        CATEGORIES_MAP.add(R.drawable.ic_map_coffee);
        CATEGORIES_MAP.add(R.drawable.ic_map_shopping);
        CATEGORIES_MAP.add(R.drawable.ic_map_nightlife);
        CATEGORIES_MAP.add(R.drawable.ic_map_culture);
        CATEGORIES_MAP.add(R.drawable.ic_map_sports);
        CATEGORIES_MAP.add(R.drawable.ic_map_music);
        CATEGORIES_MAP.add(R.drawable.ic_map_study);
        CATEGORIES_MAP.add(R.drawable.ic_map_cinema);
        CATEGORIES_MAP.add(R.drawable.ic_map_other);

        Calendar time = Calendar.getInstance();

        addEvent(new Event(1, "Coffee @Kozy", "", time, 2));
        addEvent(new Event(2, "Work in the AntiCafe", "", time, 8));
        addEvent(new Event(3, "Salsa at Bario Latino", "", time, 4));
    }

    public static void addEvent(Event e) {
        EVENTS.add(e);
    }

    public static class Event {
        public int id;
        public String name;
        public String description;
        public Calendar time;
        public Location location;
        public String locName;
        public String locAddress;
        public int attendees;
        public int image;
        public int category;
        public String dist;

        public Event(int id, String name, String des, Calendar time, int category) {
            this.id = id;
            this.name = name;
            this.description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. ";
            this.time = time;
            this.dist = "500m";
            this.locName = "Louvre";
            this.locAddress = "Louvre Museum, 75001 Paris";
            this.location = new Location("");
            this.location.setLatitude(48.860611);
            this.location.setLongitude(2.337644);
            this.attendees = 0;
            this.category = category;

            this.image = PICS.get(2);
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

        public void addAttendee() {
            this.attendees = this.attendees + 1;
        }

        public int getCategoryIcon() {
            return CATEGORIES_ICONS.get(category);
        }

        public int getCategoryMap() {
            return CATEGORIES_MAP.get(category);
        }

        public void setCategory(int category) {
            this.category = category;
        }

        @Override
        public String toString() {
            return name;
        }

    }
}
