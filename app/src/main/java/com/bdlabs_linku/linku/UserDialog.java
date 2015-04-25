package com.bdlabs_linku.linku;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bdlabs_linku.linku.Utils.CircleTransform;
import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class UserDialog extends DialogFragment {

    private ParseUser mUser;
    private Event mEvent;

    private ImageView mPhoto;
    private TextView mName;

    private TextView mEventTitle;
    private TextView mCheckdIn;

    private Activity mActivity;

    public UserDialog(EventsActivity eventsActivity) {
        mActivity = eventsActivity;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        // Get the layout inflater
        LayoutInflater inflater = mActivity.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_user, null);

        mName = (TextView) view.findViewById(R.id.name);
        mPhoto = (ImageView) view.findViewById(R.id.photo);

        mEventTitle = (TextView) view.findViewById(R.id.event);
        mCheckdIn = (TextView) view.findViewById(R.id.checkedin);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.checkin, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (mEvent != null) {
                            mEvent.checkIn();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        UserDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    public void setUser(ParseUser user) {
        this.mUser = user;

        mName.setText(user.get("name").toString());

        ParseFile photo = user.getParseFile("profilePicture");

        if (photo != null) {
            Glide.with(mActivity)
                    .load(photo.getUrl())
                    .transform(new CircleTransform(mActivity))
                    .into(mPhoto);
        }
    }

    public void setEvent(List<Event> eventList) {
        if (eventList.size() <= 0) {
            mEventTitle.setText("No current event found.");
            ((AlertDialog) this.getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        } else {
            this.mEvent = eventList.get(0);
            mEventTitle.setText(eventList.get(0).getTitle());
            isCheckedIn(eventList.get(0));
        }
        
    }

    private void isCheckedIn(Event event) {
        if (event.isCurrentUserCheckedIn()) {
            mCheckdIn.setVisibility(View.VISIBLE);
            ((AlertDialog) this.getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        }
    }
}
