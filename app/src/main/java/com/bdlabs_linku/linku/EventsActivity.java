package com.bdlabs_linku.linku;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.Toast;


public class EventsActivity extends Activity implements EventsFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new EventsFragment())
                    .commit();
        }
    }

   
    @Override
    public void onFragmentInteraction(String name) {
        //Toast.makeText(getApplicationContext(), name + ". Great choice!", Toast.LENGTH_SHORT).show();
    }
}
