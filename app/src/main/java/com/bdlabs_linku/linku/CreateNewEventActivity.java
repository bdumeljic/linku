package com.bdlabs_linku.linku;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.Calendar;

/**
 * Controlling activity for the {@link com.bdlabs_linku.linku.CreateNewEventFragment} that creates a new event from user input.
 */
@Deprecated
public class CreateNewEventActivity extends AppCompatActivity implements CreateNewEventFragment.OnFragmentInteractionListener {

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
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        final Calendar c = Calendar.getInstance();
        DialogFragment picker = DatePickerFragment.newInstance(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        picker.show(getFragmentManager(), "datePicker");
    }

    /**
     * Call the time picker widget.
     * @param v
     */
    public void timePicker(View v) {
        final Calendar c = Calendar.getInstance();
        DialogFragment picker = TimePickerFragment.newInstance(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
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
}
