package com.bdlabs_linku.linku;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.Calendar;

/**
 * Controlling activity for the {@link CreateNewEventFragment} that creates a new event from user input.
 */
@Deprecated
public class EditEventActivity extends AppCompatActivity implements EditEventFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putAll(getIntent().getExtras());

            EditEventFragment fragment = EditEventFragment.newInstance();
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }

        // Show up navigation button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_back);
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
     */
    public void setDate(Calendar calendar) {
        EditEventFragment fragment = (EditEventFragment) getFragmentManager().findFragmentById(R.id.container);
        fragment.setDate(calendar);
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
