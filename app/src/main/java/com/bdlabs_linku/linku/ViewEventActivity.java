package com.bdlabs_linku.linku;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Activity that displays all the events details.
 */
public class ViewEventActivity extends ActionBarActivity implements ObservableScrollView.Callbacks {

    public static final String TAG = "ViewEventActivity";

    public static final String EVENT_ID = "event";

    public static final String TRANSITION_NAME_PHOTO = "photo";
    public static final float SESSION_BG_COLOR_SCALE_FACTOR = 0.75f;

    protected static String STATIC_MAP_API_ENDPOINT;

    private int mSessionColor;

    private ObservableScrollView mScrollView;

    private View mPhotoViewContainer;
    private ImageView mPhotoView;
    private ProgressBar mProgressBar;

    private int mPhotoHeightPixels;
    private int mHeaderHeightPixels;
    private View mHeaderBox;
    private View mDetailsContainer;

    private View mScrollViewChild;
    private TextView mTitle;
    private TextView mTime;
    private TextView mPlace;
    private ImageView mCategory;
    private LinearLayout mAttendees;
    private TextView mDescription;

    private ImageView mMapView;
    private TextView mLocName;
    private TextView mLocAddress;

    private boolean mHasPhoto = false;
    private static final float PHOTO_ASPECT_RATIO = 1.7777777f;
    private float mMaxHeaderElevation;

    private FloatingActionButton mJoinButton;

    private boolean mGoing = false;

    private Event mEvent;
    private String mEventId;
    private Location mUserLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        // Get the user's location
        mUserLoc = getIntent().getParcelableExtra(EventsActivity.USER_LOC);

        // Setup the transparent ActionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.view_event_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_up);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setTitle("");

        // Setup parallax scrolling effect
        mMaxHeaderElevation = getResources().getDimensionPixelSize(
                R.dimen.headerbar_elevation);

        // Get views by ids
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll_view);
        mScrollView.addCallbacks(this);
        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.addOnGlobalLayoutListener(mGlobalLayoutListener);
        }

        mSessionColor = getResources().getColor(R.color.primary);

        // Connect to view
        mScrollViewChild = findViewById(R.id.scroll_view_child);
        mScrollViewChild.setVisibility(View.INVISIBLE);

        mDetailsContainer = findViewById(R.id.details_container);
        mHeaderBox = findViewById(R.id.header_session);
        mTitle = (TextView) findViewById(R.id.session_title);
        mTime = (TextView) findViewById(R.id.event_time);
        mPlace = (TextView) findViewById(R.id.event_place);

        mCategory = (ImageView) findViewById(R.id.cat);

        mPhotoViewContainer = findViewById(R.id.session_photo_container);
        mPhotoView = (ImageView) findViewById(R.id.session_photo);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mDescription = (TextView) findViewById(R.id.description);

        mMapView = (ImageView) findViewById(R.id.location_map);
        mLocName = (TextView) findViewById(R.id.location_name);
        mLocAddress = (TextView) findViewById(R.id.location_address);

        ViewCompat.setTransitionName(mPhotoView, TRANSITION_NAME_PHOTO);

        mTitle = (TextView) findViewById(R.id.session_title);

        mHeaderBox.setBackgroundColor(mSessionColor);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(scaleColor(mSessionColor, 0.8f, false));
        }

        mPhotoViewContainer.setBackgroundColor(scaleSessionColorToDefaultBG(mSessionColor));
        recomputePhotoAndScrollingMetrics();
        onScrollChanged(0, 0); // trigger scroll handling
        mScrollViewChild.setVisibility(View.VISIBLE);

        mAttendees = (LinearLayout) findViewById(R.id.attendees);

        // Setup FAB
        mJoinButton = (FloatingActionButton) findViewById(R.id.join_event_btn);
        mJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGoing) {
                    mGoing = false;
                    mEvent.removeAttendee();
                    setParticipants();
                    ((FloatingActionButton) v).setColorPressed(getResources().getColor(R.color.primary));
                    ((FloatingActionButton) v).setColorNormal(getResources().getColor(R.color.primary_dark));
                } else {
                    mGoing = true;
                    mEvent.addAttendee();
                    setParticipants();
                    ((FloatingActionButton) v).setColorNormal(getResources().getColor(R.color.accent));
                    ((FloatingActionButton) v).setColorPressed(getResources().getColor(R.color.accent_darker));
                }
            }
        });

        // Query Parse for event details
        mEventId = getIntent().getStringExtra(EVENT_ID);
        ParseQuery<Event> query = Event.getQuery();
        query.getInBackground(mEventId, new GetCallback<Event>() {
            public void done(Event object, ParseException e) {
                if (e == null) {
                    mEvent = object;

                    // Set visible markers if user is going to this event
                    mGoing = mEvent.isAlreadyAttending();

                    if (mGoing) {
                        mJoinButton.setColorNormal(getResources().getColor(R.color.accent));
                        mJoinButton.setColorPressed(getResources().getColor(R.color.accent_darker));
                    }

                    setInfo();
                } else {
                    // something went wrong
                    finish();
                }
            }
        });
    }

    /**
     * After Parse has returned the details of the event, set the details of the view.
     */
    public void setInfo() {
        mHasPhoto = mEvent.hasUploadedPhoto();
        recomputePhotoAndScrollingMetrics();
        onScrollChanged(0, 0); // trigger scroll handling
        if (mHasPhoto) {
            String mPhoto = mEvent.getUploadedPhotoUrl();
            Log.d(TAG, mPhoto);

            CenterCrop mCenterCrop = new CenterCrop(Glide.get(this).getBitmapPool());

            Glide.with(this)
                    .load(mPhoto)
                    .transform(mCenterCrop)
                    .into(new GlideDrawableImageViewTarget(mPhotoView) {
                        @Override
                        public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
                            super.onResourceReady(drawable, anim);
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
        }

        Log.d(TAG, "no photo uploaded for this event");

        mTitle.setText(mEvent.getTitle());
        mCategory.setImageResource(mEvent.getCategoryIcon());

        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String formattedDate = dateFormat.format(mEvent.getTime().getTime());
        mTime.setText("Starting at " + formattedDate);

        if (mUserLoc != null) {
            mPlace.setText(parseDistance(mEvent.getLocation()));
        }

        setParticipants();

        mDescription.setText(mEvent.getDescription());
        //mLocName.setText(EventModel.EVENTS.get(mEventId).locName);
        //mLocAddress.setText(EventModel.EVENTS.get(mEventId).locAddress);

        // Get the google maps screenshot of the map near the event
        STATIC_MAP_API_ENDPOINT = "http://maps.google.com/maps/api/staticmap?center=" + Double.toString(mEvent.getLocation().getLatitude()) + "," + Double.toString(mEvent.getLocation().getLongitude()) + "&zoom=16&size=1100x300&scale=2&sensor=false&markers=color:blue%7Clabel:%7C" + Double.toString(mEvent.getLocation().getLatitude()) + "," + Double.toString(mEvent.getLocation().getLongitude()) + "";

        AsyncTask<Void, Void, Bitmap> setImageFromUrl = new AsyncTask<Void, Void, Bitmap>(){
            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bmp = null;
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet request = new HttpGet(STATIC_MAP_API_ENDPOINT);

                InputStream in;
                try {
                    in = httpclient.execute(request).getEntity().getContent();
                    bmp = BitmapFactory.decodeStream(in);
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bmp;
            }
            protected void onPostExecute(Bitmap bmp) {
                if (bmp!=null) {
                    final ImageView iv = (ImageView) findViewById(R.id.location_map);
                    iv.setImageBitmap(bmp);
                }
            }
        };
        setImageFromUrl.execute();
    }

    /**
     * Add the participants to the view.
     */
    public void setParticipants() {
        mAttendees.removeAllViewsInLayout();

        // Query Parse for all the attending relation
        ParseRelation<ParseUser> mRelation = mEvent.getAttendingList();
        ParseQuery<ParseUser> query = mRelation.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (e == null) {
                    // Set the empty text if there are no participants.
                    if (parseUsers.isEmpty()) {
                        TextView text = new TextView(getApplicationContext());
                        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        text.setTextColor(getResources().getColor(R.color.body_dark));
                        text.setPadding(0,16,0,16);
                        text.setText("No participants yet. Be the first to join!");
                        mAttendees.addView(text);
                    }

                    // If there are participants, add them to the list.
                    for (ParseUser user : parseUsers) {
                        TextView text = new TextView(getApplicationContext());
                        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        text.setTextColor(getResources().getColor(R.color.body_dark));
                        text.setPadding(0,16,0,16);

                        if (user.equals(ParseUser.getCurrentUser()) && mGoing) {
                            text.setTextColor(getResources().getColor(R.color.accent));
                            text.setText(user.getUsername() + " (You!)");
                        } else {
                            text.setText(user.getUsername());
                        }

                        mAttendees.addView(text);
                    }
                }
            }
        });
    }

    /**
     * Listen for scrolling. Call the recompute functions when there are changes.
     */
    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener
            = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            recomputePhotoAndScrollingMetrics();
        }
    };

    /**
     * Recompute the sizes of view elements for the parallax scrolling effect.
     */
    private void recomputePhotoAndScrollingMetrics() {
        mHeaderHeightPixels = mHeaderBox.getHeight();

        mPhotoHeightPixels = 0;
        if (mHasPhoto) {
            mPhotoHeightPixels = (int) (mPhotoView.getWidth() / PHOTO_ASPECT_RATIO);
            mPhotoHeightPixels = Math.min(mPhotoHeightPixels, mScrollView.getHeight() * 2 / 3);
        }

        ViewGroup.LayoutParams lp;
        lp = mPhotoViewContainer.getLayoutParams();
        if (lp.height != mPhotoHeightPixels) {
            lp.height = mPhotoHeightPixels;
            mPhotoViewContainer.setLayoutParams(lp);
        }

        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)
                mDetailsContainer.getLayoutParams();
        if (mlp.topMargin != mHeaderHeightPixels + mPhotoHeightPixels) {
            mlp.topMargin = mHeaderHeightPixels + mPhotoHeightPixels;
            mDetailsContainer.setLayoutParams(mlp);
        }

        onScrollChanged(0, 0); // trigger scroll handling
    }

    @Override
    public void onScrollChanged(int deltaX, int deltaY) {
        // Reposition the header bar -- it's normally anchored to the top of the content,
        // but locks to the top of the screen on scroll
        int scrollY = mScrollView.getScrollY();

        float newTop = Math.max(mPhotoHeightPixels, scrollY);
        mHeaderBox.setTranslationY(newTop);


        float gapFillProgress = 1;
        if (mPhotoHeightPixels != 0) {
            gapFillProgress = Math.min(Math.max(getProgress(scrollY, 0,
                    mPhotoHeightPixels), 0), 1);
        }

        ViewCompat.setElevation(mHeaderBox, gapFillProgress * mMaxHeaderElevation);

        // Move background photo (parallax effect)
        mPhotoViewContainer.setTranslationY(scrollY * 0.5f);
    }

    public static float getProgress(int value, int min, int max) {
        if (min == max) {
            throw new IllegalArgumentException("Max (" + max + ") cannot equal min (" + min + ")");
        }

        return (value - min) / (float) (max - min);
    }

    public static int scaleColor(int color, float factor, boolean scaleAlpha) {
        return Color.argb(scaleAlpha ? (Math.round(Color.alpha(color) * factor)) : Color.alpha(color),
                Math.round(Color.red(color) * factor), Math.round(Color.green(color) * factor),
                Math.round(Color.blue(color) * factor));
    }

    public static int scaleSessionColorToDefaultBG(int color) {
        return scaleColor(color, SESSION_BG_COLOR_SCALE_FACTOR, false);
    }

    private String parseDistance(ParseGeoPoint locEvent) {
        double distance = locEvent.distanceInKilometersTo(new ParseGeoPoint(mUserLoc.getLatitude(), mUserLoc.getLongitude()));

        if (distance < 1.0) {
            distance *= 1000;
            int dist = (int) (Math.ceil(distance / 5d) * 5);
            return String.valueOf(dist) + " m away";
        } else {
            BigDecimal bd = new BigDecimal(distance).setScale(1, RoundingMode.HALF_UP);
            distance = bd.doubleValue();
            return String.valueOf(distance) + " km away";
        }
    }
}
