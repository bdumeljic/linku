package com.bdlabs_linku.linku;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class AboutActivity extends ActionBarActivity {

    private static final String VERSION_UNAVAILABLE = "N/A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Show up navigation button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Little tweak to remove shadow below actionbar
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle(R.string.title_activity_about);

        PackageManager pm = getPackageManager();
        String packageName = getPackageName();
        String versionName;
        try {
            PackageInfo info = pm.getPackageInfo(packageName, 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = VERSION_UNAVAILABLE;
        }

        TextView version = (TextView) findViewById(R.id.about_version);
        version.append(" " + versionName);

        TextView web = (TextView) findViewById(R.id.about_website);
        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.getlinku.com"));
                startActivity(browserIntent);
            }
        });

        TextView tos = (TextView) findViewById(R.id.about_tos);
        tos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.getlinku.com/terms-of-use.html"));
                startActivity(browserIntent);
            }
        });

        TextView privacy = (TextView) findViewById(R.id.about_pp);
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.getlinku.com/privacy.html"));
                startActivity(browserIntent);
            }
        });

        TextView safety = (TextView) findViewById(R.id.about_safety);
        safety.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.getlinku.com/safety.html"));
                startActivity(browserIntent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
