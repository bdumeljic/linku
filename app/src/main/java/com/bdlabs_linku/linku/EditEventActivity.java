package com.bdlabs_linku.linku;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class EditEventActivity extends ActionBarActivity implements EditEventFragment.OnFragmentInteractionListener{

    public static final String EVENT_ID = "event";
    public Event mEvent;
    private Event mEventObject;
    private String mEventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new EditEventFragment())
                    .commit();
        }
        // Show up navigation button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //get event information

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_new_event, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_confirm:
                EditEventFragment fragment = (EditEventFragment) getFragmentManager().findFragmentById(R.id.container);
                fragment.saveEvent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    public void dayPicker(View v) {
        DialogFragment picker = new DatePickerFragment();
        picker.show(getFragmentManager(), "datePicker");
    }

    public void timePicker(View v) {
        DialogFragment picker = new TimePickerFragment();
        picker.show(getFragmentManager(), "timePicker");
    }

    public void setDate(int dayOfMonth, int monthOfYear, int year) {
        EditEventFragment fragment = (EditEventFragment) getFragmentManager().findFragmentById(R.id.container);
        fragment.setDay(dayOfMonth, monthOfYear, year);
    }

    public void setTime(int hour, int minute) {
        EditEventFragment fragment = (EditEventFragment) getFragmentManager().findFragmentById(R.id.container);
        fragment.setTime(hour, minute);
    }

    public String getEventTitle(){
        return getIntent().getStringExtra("EventTitle");
    }
    public String getEventDescription(){
        return getIntent().getStringExtra("EventDescription");
    }
    public String getEventTime(){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String formattedDate = dateFormat.format(getIntent().getLongExtra("EventTime", -1));
        return formattedDate;
    }
    public String getEventDay(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(getIntent().getLongExtra("EventDay", -1));
    }
    public String getEventLocation(){
        return getIntent().getStringExtra("EventLocation");
    }
    public String getEventId(){
        return getIntent().getStringExtra(EVENT_ID);
    }
    public int getEventCategory(){
        return getIntent().getIntExtra("EventCategory", 0);
    }
}
