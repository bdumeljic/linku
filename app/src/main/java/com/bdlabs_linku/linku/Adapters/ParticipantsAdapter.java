package com.bdlabs_linku.linku.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bdlabs_linku.linku.R;
import com.bdlabs_linku.linku.Utils.CircleTransform;
import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseUser;

import java.util.List;

public class ParticipantsAdapter extends ArrayAdapter {
    private List mParticipants;
    private Context mContext;
    private ParseUser mCreator;

    public ParticipantsAdapter(Context context, List participants, ParseUser creator) {
        super(context, R.layout.participant_item, participants);
        this.mContext = context;
        this.mParticipants = participants;
        this.mCreator = creator;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.participant_item, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.participant_name);
        ImageView specialIcon = (ImageView) convertView.findViewById(R.id.extra_icon);
        ImageView profilePic = (ImageView) convertView.findViewById(R.id.participant_pic);

        ParseUser participant = (ParseUser) mParticipants.get(position);
        name.setText(participant.get("name").toString());
        ParseFile photo = participant.getParseFile("profilePicture");

        if (photo != null) {
            Glide.with(mContext)
                    .load(photo.getUrl())
                    .transform(new CircleTransform(mContext))
                    .into(profilePic);
        }

        if (participant.equals(mCreator)) {
            specialIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_organizer));
            specialIcon.setVisibility(View.VISIBLE);
        } else if (participant.equals(ParseUser.getCurrentUser())) {
            specialIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_going_list));
            specialIcon.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}
