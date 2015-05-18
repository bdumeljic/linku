package com.bdlabs_linku.linku;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bdlabs_linku.linku.Utils.PrefUtils;


public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getSupportActionBar().hide();
        TextView legalText = (TextView) findViewById(R.id.legal);
        legalText.setText(Html.fromHtml(getResources().getString(R.string.welcome_legal)));
        legalText.setMovementMethod(LinkMovementMethod.getInstance());

        Button declineButton = (Button) findViewById(R.id.button_decline);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button acceptButton = (Button) findViewById(R.id.button_accept);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefUtils.markTosAccepted(WelcomeActivity.this);
                Intent intent = new Intent(WelcomeActivity.this, DispatchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}
