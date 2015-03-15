package com.bdlabs_linku.linku;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.parse.ParseUser;

/**
 * Activity which starts an intent for either the logged in {@link com.bdlabs_linku.linku.EventsActivity} or logged out
 * {@link com.bdlabs_linku.linku.LoginActivity} activity.
 */
public class DispatchActivity extends ActionBarActivity {

  public DispatchActivity() {
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Check if there is current user info
    if (ParseUser.getCurrentUser() != null) {
      // Start an intent for the logged in activity
      startActivity(new Intent(this, EventsActivity.class));
    } else {
      // Start and intent for the logged out activity
      startActivity(new Intent(this, LoginActivity.class));
    }
  }

}
