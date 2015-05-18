package com.bdlabs_linku.linku;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bdlabs_linku.linku.Utils.CircleTransform;
import com.bdlabs_linku.linku.Utils.ImageChooser;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Activity which displays the signup form to the user.
 */
public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";

    public static final int REQUEST_PHOTO = 0;

    Uri imageUri = null;

    // UI references.
    private ImageView mProfilePicture;
    private ProgressBar mProgressBar;
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private EditText mPasswordAgainEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        mProfilePicture = (ImageView) findViewById(R.id.profile_photo);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // Set up the signup form.
        mUsernameEditText = (EditText) findViewById(R.id.username_edit_text);
        mPasswordEditText = (EditText) findViewById(R.id.password_edit_text);
        mPasswordAgainEditText = (EditText) findViewById(R.id.password_again_edit_text);
        mPasswordAgainEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // TODO prevent signup being done twice
                if (actionId == R.id.action_signup || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    signup();
                    return true;
                }
                return false;
            }
        });

        // Set up the submit button click handler
        Button mActionButton = (Button) findViewById(R.id.action_button);
        mActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                signup();
            }
        });

        FloatingActionButton profilePicButton = (FloatingActionButton) findViewById(R.id.pick_profile_pic_btn);
        profilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });

        // Show up navigation button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Little tweak to remove shadow below actionbar
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle(R.string.sign_up);
    }

    private void signup() {
        String username = mUsernameEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();
        String passwordAgain = mPasswordAgainEditText.getText().toString().trim();

        // Validate the sign up data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
        if (username.length() == 0 || !isValidEmail(username)) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_email));
        }
        if (password.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_password));
        }
        if (!password.equals(passwordAgain)) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_mismatched_passwords));
        }
        validationErrorMessage.append(getString(R.string.error_end));

        // If there is a validation error, display the error
        if (validationError) {
            Toast.makeText(SignUpActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
            return;
        }

        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(SignUpActivity.this);
        dialog.setMessage(getString(R.string.progress_signup));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        // Set up a new Parse user
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(username);

        String[] parts = username.split("@");
        String name = parts[0];
        user.put("name", name);

        if (imageUri != null) {
            user.put("profilePicture", ImageChooser.createParseImageFileFromUri(this, imageUri));
        }

        // Call the Parse signup method
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                dialog.dismiss();
                if (e != null) {
                    // Show the error message
                    switch (e.getCode()) {
                        case ParseException.USERNAME_TAKEN:
                            Toast.makeText(SignUpActivity.this, "This username is already taken.", Toast.LENGTH_LONG).show();
                            break;
                        case ParseException.CONNECTION_FAILED:
                            Toast.makeText(SignUpActivity.this, R.string.no_internet, Toast.LENGTH_LONG).show();
                            break;
                        default:
                            Log.e(TAG, e.getCode() + " " + e.getMessage());
                    }
                } else {
                    // Start an intent for the dispatch activity
                    Intent intent = new Intent(SignUpActivity.this, DispatchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_PHOTO:
                if(resultCode == Activity.RESULT_OK){
                    mProgressBar.setVisibility(View.VISIBLE);
                    imageUri = data.getData();

                    Log.d(TAG, "path " + imageUri);

                    Glide.with(this)
                            .load(imageUri)
                            .fitCenter()
                            .transform(new CircleTransform(SignUpActivity.this))
                            .into(new GlideDrawableImageViewTarget(mProfilePicture) {
                                @Override
                                public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
                                    super.onResourceReady(drawable, anim);
                                    mProgressBar.setVisibility(View.GONE);
                                }
                            });
                } else {
                    mProfilePicture.setImageDrawable(getResources().getDrawable(R.drawable.profile_picture_placeholder));
                }
        }
    }


}
