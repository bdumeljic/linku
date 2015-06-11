package com.bdlabs_linku.linku;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bdlabs_linku.linku.Utils.PrefUtils;
import com.parse.ParseUser;

/**
 * Activity which starts an intent for either the logged in {@link com.bdlabs_linku.linku.BrowseEventsActivity} or logged out
 * {@link com.bdlabs_linku.linku.LoginActivity} activity.
 */
public class DispatchActivity extends AppCompatActivity {

  public DispatchActivity() {
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Check if there is current user info
    if (ParseUser.getCurrentUser() != null && PrefUtils.isTosAccepted(this)) {
      // Start an intent for the logged in activity but tos not yet accepted
      startActivity(new Intent(this, BrowseEventsActivity.class));
    } else if (ParseUser.getCurrentUser() != null) {
      startActivity(new Intent(this, WelcomeActivity.class));
    } else {
      // Start and intent for the logged out activity
      startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
  }

}
