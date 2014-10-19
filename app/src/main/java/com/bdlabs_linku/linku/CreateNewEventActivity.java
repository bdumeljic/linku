package com.bdlabs_linku.linku;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CreateNewEventActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_event);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_new_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_create_new_event, container, false);

            final Button saveButton = (Button) rootView.findViewById(R.id.btn_save_event);
            final EditText mEdit = (EditText) rootView.findViewById(R.id.event_name_input);
            final DatePicker datepicker = (DatePicker)rootView.findViewById(R.id.event_date);
            final Date eventDate = new Date(datepicker.getYear() - 1900, datepicker.getMonth(), datepicker.getDayOfMonth());



            saveButton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    EventModel.addEvent(new EventModel.Event(EventModel.EVENTS.size(), mEdit.getText().toString(), eventDate));
                    finish();
                }
            });

            return rootView;
        }
    }
}
