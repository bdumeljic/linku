package com.bdlabs_linku.linku;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bojana on 13/10/14.
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

        public Event(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

    }
}
