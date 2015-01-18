package com.bdlabs_linku.linku;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Toast;


public class ViewEventActivity extends ActionBarActivity implements ViewEventFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
        Bundle bundle = new Bundle();
        bundle.putInt(ViewEventFragment.EVENT_POSITION, getIntent().getIntExtra(ViewEventFragment.EVENT_POSITION, 0));
        // to pass arguments to the fragment a new bundle with the arguments has to be created and set as arguments
        ViewEventFragment viewEvent = new ViewEventFragment();
        viewEvent.setArguments(bundle);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, viewEvent)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        // if (id == R.id.action_settings) {
        //   return true;
        //}
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(int mEventId) {

    }
}
