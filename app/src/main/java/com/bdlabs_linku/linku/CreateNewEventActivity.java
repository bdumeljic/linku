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


public class CreateNewEventActivity extends ActionBarActivity implements CreateNewEventFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_event);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new CreateNewEventFragment())
                    .commit();
        }



        // Show up navigation button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
}
