package com.bdlabs_linku.linku;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

/**
 * Activity which displays a login screen to the user or the option to go to the signup form on {@link com.bdlabs_linku.linku.SignUpActivity}.
 */
public class LoginActivity extends ActionBarActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        usernameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.edittext_action_login ||
                        actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    login();
                    return true;
                }
                return false;
            }
        });

        // Set up the login submit button click handler
        Button actionButton = (Button) findViewById(R.id.login_button);
        actionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                login();
            }
        });

        // Sign up button click handler
        Button signupButton = (Button) findViewById(R.id.signup_button);
        signupButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Starts an intent for the sign up activity
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        Button resetPasswordButton = (Button) findViewById(R.id.reset_password_button);
        resetPasswordButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this, R.style.AppTheme_Dialog);

                View promptView = getLayoutInflater().inflate(R.layout.dialog_email_prompt, null);
                alertDialogBuilder.setView(promptView);

                final EditText input = (EditText) promptView.findViewById(R.id.email);
                if (isValidEmail(usernameEditText.getText())) {
                    input.setText(usernameEditText.getText());
                }
                // setup a dialog window
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton(R.string.action_reset, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String email = input.getText().toString().trim();

                                if (isValidEmail(email)) {
                                    usernameEditText.setText(email);

                                    ParseUser.requestPasswordResetInBackground(email,
                                            new RequestPasswordResetCallback() {
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        Toast.makeText(LoginActivity.this, "You've got mail", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        // Something went wrong. Look at the ParseException to see what's up.
                                                        Log.e("LOGIN", e.toString() + " " + e.getCode());
                                                        if (e.getCode() == 205) {
                                                            Toast.makeText(LoginActivity.this, "No account with this email", Toast.LENGTH_LONG).show();
                                                        } else if (e.getCode() == 100) {
                                                            Toast.makeText(LoginActivity.this, "No internet connection", Toast.LENGTH_LONG).show();
                                                        } else {
                                                            Toast.makeText(LoginActivity.this, "Something went wrong. Try again later", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(LoginActivity.this, "Not a valid email address", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create an alert dialog
                AlertDialog promptEmailDialog = alertDialogBuilder.create();

                promptEmailDialog.show();

            }
        });
    }

    /**
     * Try to log the user in. Go to back to {@link com.bdlabs_linku.linku.DispatchActivity} on success or show error message on failure.
     */
    private void login() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate the log in data
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
        validationErrorMessage.append(getString(R.string.error_end));

        // If there is a validation error, display the error
        if (validationError) {
            Toast.makeText(LoginActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage(getString(R.string.progress_login));
        dialog.show();
        // Call the Parse login method
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                dialog.dismiss();
                if (e != null) {
                    Log.d("PARSE", " " + e.getCode() + " " + e.getMessage());
                    switch (e.getCode()) {
                        case ParseException.OBJECT_NOT_FOUND:
                            Toast.makeText(LoginActivity.this, "Email and/or password incorrect.", Toast.LENGTH_LONG).show();
                            break;
                        case ParseException.CONNECTION_FAILED:
                            Toast.makeText(LoginActivity.this, R.string.no_internet, Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Start an intent for the dispatch activity
                    Intent intent = new Intent(LoginActivity.this, DispatchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
