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
import com.parse.ParseQuery;


public class EditEventActivity extends ActionBarActivity implements EditEventFragment.OnFragmentInteractionListener{

    public static final String EVENT_ID = "event";
    private Event mEvent;
    private String mEventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        EditEventFragment eventFragment = new EditEventFragment();
        eventFragment.setArguments(getIntent().getExtras());
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, eventFragment)
                    .commit();
        }

        // Show up navigation button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //get event information
        mEventId = getIntent().getStringExtra(EVENT_ID);
        ParseQuery<Event> query = Event.getQuery();
        query.getInBackground(mEventId, new GetCallback<Event>() {
            public void done(Event object, ParseException e) {
                if (e == null) {
                    mEvent = object;
                    Log.e("Event transferred", "" + mEvent);

                } else {
                    // something went wrong
                    finish();
                }
            }
        });

        Log.e("Event transferred after", "" + mEvent);
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
                CreateNewEventFragment fragment = (CreateNewEventFragment) getFragmentManager().findFragmentById(R.id.container);
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
        CreateNewEventFragment fragment = (CreateNewEventFragment) getFragmentManager().findFragmentById(R.id.container);
        fragment.setDay(dayOfMonth, monthOfYear, year);
    }

    public void setTime(int hour, int minute) {
        CreateNewEventFragment fragment = (CreateNewEventFragment) getFragmentManager().findFragmentById(R.id.container);
        fragment.setTime(hour, minute);
    }

    public Event getEvent(){
        Log.e("TEST FOR EVENT", "" + mEvent);
        return mEvent;
    }
}
