package com.bdlabs_linku.linku;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

/**
 * Controlling activity for the {@link CreateNewEventFragment} that creates a new event from user input.
 */
public class EditEventActivity extends ActionBarActivity implements EditEventFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putAll(getIntent().getExtras());

            EditEventFragment fragment = new EditEventFragment();
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
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
                EditEventFragment fragment = (EditEventFragment) getFragmentManager().findFragmentById(R.id.container);
                fragment.updateEvent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFragmentInteraction() {
    }

    /**
     * Return the picked date to the fragment.
     * @param dayOfMonth
     * @param monthOfYear
     * @param year
     */
    public void setDate(int dayOfMonth, int monthOfYear, int year) {
        EditEventFragment fragment = (EditEventFragment) getFragmentManager().findFragmentById(R.id.container);
        fragment.setDate(dayOfMonth, monthOfYear, year);
    }

    /**
     * Return the picked hour to the fragment.
     * @param hour
     * @param minute
     */
    public void setTime(int hour, int minute) {
        EditEventFragment fragment = (EditEventFragment) getFragmentManager().findFragmentById(R.id.container);
        fragment.setTime(hour, minute);
    }
}
