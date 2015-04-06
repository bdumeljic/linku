package com.bdlabs_linku.linku;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ParseClassName("Event")
public class Event extends ParseObject {

    public static List<Integer> PICS = new ArrayList<>();
    public static List<String> CATEGORIES = new ArrayList<>();
    public static List<Integer> CATEGORIES_ICONS = new ArrayList<>();
    public static List<Integer> CATEGORIES_MAP = new ArrayList<>();
    public static List<Integer> CATEGORIES_CIRCLE = new ArrayList<>();

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

        CATEGORIES_CIRCLE.add(R.drawable.ic_circle_outdoor);
        CATEGORIES_CIRCLE.add(R.drawable.ic_circle_food);
        CATEGORIES_CIRCLE.add(R.drawable.ic_circle_coffee);
        CATEGORIES_CIRCLE.add(R.drawable.ic_circle_shopping);
        CATEGORIES_CIRCLE.add(R.drawable.ic_circle_nightlife);
        CATEGORIES_CIRCLE.add(R.drawable.ic_circle_culture);
        CATEGORIES_CIRCLE.add(R.drawable.ic_circle_sports);
        CATEGORIES_CIRCLE.add(R.drawable.ic_circle_music);
        CATEGORIES_CIRCLE.add(R.drawable.ic_circle_study);
        CATEGORIES_CIRCLE.add(R.drawable.ic_circle_cinema);
        CATEGORIES_CIRCLE.add(R.drawable.ic_circle_other);
    }

    public static ParseQuery<Event> getQuery() {
        return ParseQuery.getQuery(Event.class);
    }

    public void setCreator(ParseUser user) {
        put("creator", user);
    }

    public ParseUser getCreator() {
        return getParseUser("creator");
    }

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String value) {
        put("title", value);
    }

    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String value) {
        put("description", value);
    }

    public Date getTime() {
        return getDate("time");
    }

    public void setTime(Date time) {
        put("time", time);
    }

    public ParseGeoPoint getLocationGeo() {
        return getParseGeoPoint("location");
    }

    public String getLocationPlaceId() {
        return getString("locationPlaceId");
    }

    public String getLocationName() {
        return getString("locationName");
    }

    public String getLocationAddress() {
        return getString("locationAddress");
    }


    public void setLocation(String id, ParseGeoPoint location, String name, String address) {
        put("locationPlaceId", id);
        put("locationName", name);
        put("locationAddress", address);
        put("location", location);
    }
    public int getAttending() {
        return getInt("attending");
    }

    public void setAttending(int attendees) {
        put("attending", attendees);
    }

    public boolean isAlreadyAttending() {
        List<ParseUser> mAttending = null;
        try {
            mAttending = getAttendingList().getQuery().find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        boolean attending = false;

        if (mAttending != null) {
            for (ParseUser user : mAttending) {
                if (user.equals(ParseUser.getCurrentUser())) {
                    attending = true;
                }
            }
        }

        return attending;
    }

    public ParseRelation<ParseUser> getAttendingList() {
        return getRelation("attendingList");
    }

    public void addAttendee() {
        increment("attending");
        getAttendingList().add(ParseUser.getCurrentUser());
        try {
            save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("EVENT", "attending: " + getAttendingList().toString());
    }

    public void removeAttendee() {
        put("attending", getAttending() - 1);
        getAttendingList().remove(ParseUser.getCurrentUser());
        try {
            save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public int getCategory() {
        return getInt("category");
    }

    public void setCategory(int category) {
        put("category", category);
    }

    public int getCategoryIcon() {
        return CATEGORIES_ICONS.get(getCategory());
    }

    public int getCategoryMap() {
        return CATEGORIES_MAP.get(getCategory());
    }

    public int getCategoryCircle() {
        return CATEGORIES_CIRCLE.get(getCategory());
    }

    @Override
    public String toString() {
        return getTitle() + " " + String.valueOf(getAttending());
    }

    public boolean hasUploadedPhoto() {
        return getBoolean("uploadedPhoto");
    }

    public void setHasUploadedPhoto(boolean hasUploadedPhoto) {
        put("uploadedPhoto", hasUploadedPhoto);
    }

    public String getUploadedPhotoUrl() {
        return getParseFile("photo").getUrl();
    }

    public void setPhoto(String photo) {
        Bitmap bitmap = BitmapFactory.decodeFile(photo);

        // Convert it to byte
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Compress image to lower quality scale 1 - 100
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] image = stream.toByteArray();

        // Create the ParseFile
        ParseFile file = new ParseFile(image);
        put("photo", file);
    }
}
