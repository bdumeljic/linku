package com.bdlabs_linku.linku;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;


/**
 * Controlling activity for the {@link com.bdlabs_linku.linku.CreateNewEventFragment} that creates a new event from user input.
 */
public class CreateNewEventActivity extends ActionBarActivity implements CreateNewEventFragment.OnFragmentInteractionListener {

    public static final int REQUEST_PLACE_PICKER = 1;

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
    public void onFragmentInteraction() {
    }

    /**
     * Call the date picker widget.
     * @param v
     */
    public void datePicker(View v) {
        DialogFragment picker = new DatePickerFragment();
        picker.show(getFragmentManager(), "datePicker");
    }

    /**
     * Call the time picker widget.
     * @param v
     */
    public void timePicker(View v) {
        DialogFragment picker = new TimePickerFragment();
        picker.show(getFragmentManager(), "timePicker");
    }

    /**
     * Return the picked date to the fragment.
     * @param dayOfMonth
     * @param monthOfYear
     * @param year
     */
    public void setDate(int dayOfMonth, int monthOfYear, int year) {
        CreateNewEventFragment fragment = (CreateNewEventFragment) getFragmentManager().findFragmentById(R.id.container);
        fragment.setDate(dayOfMonth, monthOfYear, year);
    }

    /**
     * Return the picked hour to the fragment.
     * @param hour
     * @param minute
     */
    public void setTime(int hour, int minute) {
        CreateNewEventFragment fragment = (CreateNewEventFragment) getFragmentManager().findFragmentById(R.id.container);
        fragment.setTime(hour, minute);
    }

    public void onPickButtonClick(View v) {
        // Construct an intent for the place picker
       // try {
            //PlacePicker.IntentBuilder intentBuilder =
              //      new PlacePicker.IntentBuilder();
            //Intent intent = intentBuilder.build(this);
            Intent intent = new Intent(this, MainActivity.class);
            // Start the intent by requesting a result,
            // identified by a request code.
            startActivityForResult(intent, REQUEST_PLACE_PICKER);

        /*} catch (GooglePlayServicesRepairableException e) {
            // ...
        } catch (GooglePlayServicesNotAvailableException e) {
            // ...
        }*/
    }
        @Override
        public void onActivityResult(int requestCode,  int resultCode, Intent data){

            if (requestCode == REQUEST_PLACE_PICKER
                    && resultCode == Activity.RESULT_OK) {

                // The user has selected a place. Extract the name and address.
                final Place place = PlacePicker.getPlace(data, this);
                CreateNewEventFragment fragment = (CreateNewEventFragment) getFragmentManager().findFragmentById(R.id.container);
                fragment.setLocation(place);

            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }




}
