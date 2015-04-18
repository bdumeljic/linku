package com.bdlabs_linku.linku;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bdlabs_linku.linku.Adapters.ParticipantsAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
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
import java.util.Date;
import java.util.List;

/**
 * Activity that displays all the events details.
 */
public class ViewEventActivity extends ActionBarActivity implements ObservableScrollView.Callbacks {

    public static final String TAG = "ViewEventActivity";

    private static final int EDIT_EVENT = 2;
    private static final String EVENT_POS = "event_pos";
    private static final String EDITED = "event_edited";

    public static final String EVENT_ID = "event_id";
    public static final String EVENT_IMAGE = "event_image";
    public static final String EVENT_TITLE = "event_title";
    public static final String EVENT_DESCRIPTION = "event_description";
    public static final String EVENT_TIME = "event_time";
    public static final String EVENT_LOCATION_GEO_LAT = "event_location_geo_lat";
    public static final String EVENT_LOCATION_GEO_LONG = "event_location_geo_long";
    public static final String EVENT_LOCATION_NAME = "event_location_name";
    public static final String EVENT_LOCATION_ADDRESS = "event_location_address";
    public static final String EVENT_LOCATION_ID = "event_location_id";

    public static final String EVENT_CATEGORY = "event_category";

    public static final String TRANSITION_NAME_PHOTO = "photo";
    public static final float SESSION_BG_COLOR_SCALE_FACTOR = 0.75f;

    protected static String STATIC_MAP_API_ENDPOINT;

    private int mEventColor;

    private ObservableScrollView mScrollView;

    private View mPhotoViewContainer;
    private ImageView mPhotoView;
    private ProgressBar mProgressBar;

    private int mPhotoHeightPixels;
    private int mHeaderHeightPixels;
    private int mActiobarHeightPixels;
    private View mHeaderBox;
    private View mDetailsContainer;

    private View mScrollViewChild;
    private TextView mTitle;
    private TextView mSecondaryTitle;
    private ImageView mCategory;
    private TextView mEmptyParticipants;
    private ListView mParticipantsList;
    private LinearLayout mAttendees;
    private TextView mDescription;

    private ImageView mMapView;
    private TextView mLocName;
    private TextView mLocAddress;

    private boolean mHasPhoto = false;
    private static final float PHOTO_ASPECT_RATIO = 1.7777777f;
    private float mMaxHeaderElevation;
    private float mFABElevation;

    private FloatingActionButton mJoinButton;

    private boolean mGoing = false;

    private Event mEvent;
    private String mEventId;
    private Location mUserLoc;

    private int mEventPos;

    private Menu mMenu;

    private int mJoinButtonHeightPixels;

    public ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.progress_get_event));
        dialog.show();

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
        mFABElevation = getResources().getDimensionPixelSize(R.dimen.fab_elevation);

        // Get views by ids
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll_view);
        mScrollView.addCallbacks(this);
        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.addOnGlobalLayoutListener(mGlobalLayoutListener);
        }

        mEventColor = getResources().getColor(R.color.primary);

        // Connect to view
        mScrollViewChild = findViewById(R.id.scroll_view_child);
        mScrollViewChild.setVisibility(View.INVISIBLE);

        mDetailsContainer = findViewById(R.id.details_container);
        mHeaderBox = findViewById(R.id.header_event);
        mTitle = (TextView) findViewById(R.id.event_title);
        mSecondaryTitle = (TextView) findViewById(R.id.event_secondary);

        mPhotoViewContainer = findViewById(R.id.event_photo_container);
        mPhotoView = (ImageView) findViewById(R.id.event_photo);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mCategory = (ImageView) findViewById(R.id.cat);
        mDescription = (TextView) findViewById(R.id.description);
        mParticipantsList = (ListView) findViewById(R.id.list_participants);
        mEmptyParticipants = (TextView) findViewById(R.id.emptyViewParticipants);
        mAttendees = (LinearLayout) findViewById(R.id.attendees);

        mMapView = (ImageView) findViewById(R.id.location_map);
        mLocName = (TextView) findViewById(R.id.location_name);
        mLocAddress = (TextView) findViewById(R.id.location_address);

        mJoinButton = (FloatingActionButton) findViewById(R.id.join_event_btn);

        ViewCompat.setTransitionName(mPhotoView, TRANSITION_NAME_PHOTO);

        mHeaderBox.setBackgroundColor(mEventColor);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(scaleColor(mEventColor, 0.8f, false));
        }

        mPhotoViewContainer.setBackgroundColor(scaleSessionColorToDefaultBG(mEventColor));

        // Setup join FAB action
        mJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGoing) {
                    mGoing = false;
                    mEvent.removeAttendee();
                    setParticipants();
                    ((FloatingActionButton) v).setIcon(R.drawable.ic_plus_dark);
                    ((FloatingActionButton) v).setColorNormal(getResources().getColor(android.R.color.white));
                    ((FloatingActionButton) v).setColorPressed(getResources().getColor(R.color.white_darker));
                } else {
                    mGoing = true;
                    mEvent.addAttendee();
                    setParticipants();
                    ((FloatingActionButton) v).setIcon(R.drawable.ic_confirm);
                    ((FloatingActionButton) v).setColorNormal(getResources().getColor(R.color.accent_2));
                    ((FloatingActionButton) v).setColorPressed(getResources().getColor(R.color.accent_2_darker));
                }

                Intent intent = new Intent();
                intent.putExtra(EVENT_POS, mEventPos);
                intent.putExtra(EDITED, true);
                setResult(Activity.RESULT_OK, intent);
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
                        mJoinButton.setIcon(R.drawable.ic_confirm);
                        mJoinButton.setColorNormal(getResources().getColor(R.color.accent_2));
                        mJoinButton.setColorPressed(getResources().getColor(R.color.accent_2_darker));
                    }

                    setInfo();
                } else {
                    // something went wrong
                    finish();
                }
            }
        });

        mEventPos = getIntent().getIntExtra(EVENT_POS, -1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_event, menu);

        mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Toast.makeText(ViewEventActivity.this, "editing", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ViewEventActivity.this, EditEventActivity.class);
                intent.putExtra(EVENT_ID, mEventId);
                if (mEvent.hasUploadedPhoto()) {
                    intent.putExtra(EVENT_IMAGE, mEvent.getUploadedPhotoUrl());
                } else {
                    intent.putExtra(EVENT_IMAGE, "");
                }
                intent.putExtra(EVENT_TITLE, mEvent.getTitle());
                intent.putExtra(EVENT_DESCRIPTION, mEvent.getDescription());
                intent.putExtra(EVENT_TIME, mEvent.getTime());
                intent.putExtra(EVENT_LOCATION_GEO_LAT, mEvent.getLocationGeo().getLatitude());
                intent.putExtra(EVENT_LOCATION_GEO_LONG, mEvent.getLocationGeo().getLongitude());
                intent.putExtra(EVENT_LOCATION_NAME, mEvent.getLocationName());
                intent.putExtra(EVENT_LOCATION_ADDRESS, mEvent.getLocationAddress());
                intent.putExtra(EVENT_LOCATION_ID, mEvent.getLocationPlaceId());
                intent.putExtra(EVENT_CATEGORY, mEvent.getCategory());
                startActivityForResult(intent, EDIT_EVENT);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * After Parse has returned the details of the event, set the details of the view.
     */
    public void setInfo() {
        if (ParseUser.getCurrentUser() == mEvent.getCreator()) {
            getMenuInflater().inflate(R.menu.view_event_creator, mMenu);
        }

        mTitle.setText(mEvent.getTitle());

        mHasPhoto = mEvent.hasUploadedPhoto();
        recomputePhotoAndScrollingMetrics();
        onScrollChanged(0, 0); // trigger scroll handling

        mScrollViewChild.setVisibility(View.VISIBLE);
        dialog.dismiss();

        if (mHasPhoto) {
            String mPhoto = mEvent.getUploadedPhotoUrl();

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

        mTitle.setText(mEvent.getTitle());
        mCategory.setImageResource(mEvent.getCategoryCircle());

        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String formattedDate = dateFormat.format(mEvent.getTime().getTime());
        mSecondaryTitle.setText("Starting at " + formattedDate);

        if (mUserLoc != null) {
            mSecondaryTitle.append(" - " + parseDistance(mEvent.getLocationGeo()));
        }

        setParticipants();

        mDescription.setText(mEvent.getDescription());

        if (mEvent.getLocationName() == null && mEvent.getLocationAddress() == null) {
            mLocName.setText("(" + mEvent.getLocationGeo().getLatitude() + ", " + mEvent.getLocationGeo().getLongitude() + ")");
            mLocAddress.setVisibility(View.GONE);
        } else if (mEvent.getLocationAddress().equals("")) {
            mLocName.setText(mEvent.getLocationName());
            mLocAddress.setVisibility(View.GONE);
        } else if (!mEvent.getLocationName().equals("") && !mEvent.getLocationAddress().equals("")) {
            mLocName.setText(mEvent.getLocationName());
            mLocAddress.setText(mEvent.getLocationAddress());
        }

        // Get the google maps screenshot of the map near the event
        STATIC_MAP_API_ENDPOINT = "http://maps.google.com/maps/api/staticmap?center=" + Double.toString(mEvent.getLocationGeo().getLatitude()) + "," + Double.toString(mEvent.getLocationGeo().getLongitude()) + "&zoom=16&size=1100x300&scale=2&sensor=false&markers=color:blue%7Clabel:%7C" + Double.toString(mEvent.getLocationGeo().getLatitude()) + "," + Double.toString(mEvent.getLocationGeo().getLongitude()) + "";

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
        // Query Parse for all the attending relation
        ParseRelation<ParseUser> mRelation = mEvent.getAttendingList();
        ParseQuery<ParseUser> query = mRelation.getQuery();
        query.orderByAscending("name");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (e == null) {
                    // Set the empty text if there are no participants.
                    if (parseUsers.isEmpty()) {
                        mParticipantsList.setVisibility(View.GONE);
                        mEmptyParticipants.setVisibility(View.VISIBLE);
                    } else {
                        mParticipantsList.setAdapter(new ParticipantsAdapter(ViewEventActivity.this, parseUsers));
                        setListViewHeightBasedOnChildren(mParticipantsList);
                        mEmptyParticipants.setVisibility(View.GONE);
                        mParticipantsList.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) {
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    /**
     * Listen for scrolling. Call the recompute functions when there are changes.
     */
    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener
            = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            mJoinButtonHeightPixels = mJoinButton.getHeight();
            recomputePhotoAndScrollingMetrics();
        }
    };

    /**
     * Recompute the sizes of view elements for the parallax scrolling effect.
     */
    private void recomputePhotoAndScrollingMetrics() {
        mHeaderHeightPixels = mHeaderBox.getHeight();
        mActiobarHeightPixels = getSupportActionBar().getHeight();

        mPhotoHeightPixels = 0;
        if (mHasPhoto) {
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(android.R.drawable.list_selector_background));
            mPhotoHeightPixels = (int) (mPhotoView.getWidth() / PHOTO_ASPECT_RATIO);
            mPhotoHeightPixels = Math.min(mPhotoHeightPixels, mScrollView.getHeight() * 2 / 3);
        } else {
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.selectable_item_background_primary));
        }

        ViewGroup.LayoutParams lp;
        lp = mPhotoViewContainer.getLayoutParams();
        if (lp.height != mPhotoHeightPixels) {
            lp.height = mPhotoHeightPixels;
            mPhotoViewContainer.setLayoutParams(lp);
        }

        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) mDetailsContainer.getLayoutParams();
        if (!mHasPhoto && mlp.topMargin != mHeaderHeightPixels + mPhotoHeightPixels + mActiobarHeightPixels) {
            mlp.topMargin = mHeaderHeightPixels + mPhotoHeightPixels + mActiobarHeightPixels;
            mDetailsContainer.setLayoutParams(mlp);
        } else if (mHasPhoto && mlp.topMargin != mHeaderHeightPixels + mPhotoHeightPixels) {
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

        float dif = Math.abs(newTop - Math.min(mPhotoHeightPixels, scrollY));

        if (mPhotoHeightPixels == 0) {
            mHeaderBox.setTranslationY(mPhotoHeightPixels + mActiobarHeightPixels + dif);
            mJoinButton.setTranslationY(mPhotoHeightPixels + mActiobarHeightPixels + dif + mHeaderHeightPixels - mJoinButtonHeightPixels / 2);
        } else if (scrollY > mPhotoHeightPixels) {
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.selectable_item_background_primary));
            mHeaderBox.setTranslationY(mPhotoHeightPixels + mActiobarHeightPixels + dif);
            mJoinButton.setTranslationY(mPhotoHeightPixels + mActiobarHeightPixels + dif + mHeaderHeightPixels - mJoinButtonHeightPixels / 2);
        } else if (dif <= mActiobarHeightPixels) {
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.selectable_item_background_primary));
            mHeaderBox.setTranslationY(mPhotoHeightPixels + (mActiobarHeightPixels - dif));
            mJoinButton.setTranslationY(mPhotoHeightPixels + (mActiobarHeightPixels - dif) + mHeaderHeightPixels - mJoinButtonHeightPixels / 2);
        } else {
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(android.R.drawable.list_selector_background));
            mHeaderBox.setTranslationY(newTop);
            mJoinButton.setTranslationY(newTop + mHeaderHeightPixels - mJoinButtonHeightPixels / 2);
        }

        float gapFillProgress = 1;
        if (mPhotoHeightPixels != 0) {
            gapFillProgress = Math.min(Math.max(getProgress(scrollY, 0, mPhotoHeightPixels), 0), 1);
        }

        ViewCompat.setElevation(mHeaderBox, gapFillProgress * mMaxHeaderElevation);
        ViewCompat.setElevation(mJoinButton, gapFillProgress * mMaxHeaderElevation
                + mFABElevation);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_EVENT && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            mTitle.setText(bundle.getString(EVENT_TITLE));
            mDescription.setText(bundle.getString(EVENT_DESCRIPTION));
            mCategory.setImageResource(Event.CATEGORIES_CIRCLE.get(bundle.getInt(EVENT_CATEGORY)));

            DateFormat dateFormat = new SimpleDateFormat("HH:mm");
            String formattedDate = dateFormat.format(((Date) bundle.get(EVENT_TIME)).getTime());
            mSecondaryTitle.setText("Starting at " + formattedDate);

            if (mUserLoc != null) {
                Log.i(TAG, mUserLoc.toString());
                mSecondaryTitle.append(" - " + parseDistance(new ParseGeoPoint(bundle.getDouble(EVENT_LOCATION_GEO_LAT), bundle.getDouble(EVENT_LOCATION_GEO_LONG))));
            }

            String mPhoto = bundle.getString(EVENT_IMAGE);
            if (!mPhoto.equals("")) {
                mProgressBar.setVisibility(View.VISIBLE);
                mHasPhoto = true;
                    Glide.with(this)
                            .load(mPhoto)
                            .centerCrop()
                            .into(new GlideDrawableImageViewTarget(mPhotoView) {
                                @Override
                                public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
                                    super.onResourceReady(drawable, anim);
                                    mProgressBar.setVisibility(View.GONE);
                                }
                            });
                }

            if (bundle.getString(EVENT_LOCATION_NAME) == null && bundle.getString(EVENT_LOCATION_ADDRESS) == null) {
                mLocName.setText("(" + bundle.getDouble(EVENT_LOCATION_GEO_LAT) + ", " + bundle.getDouble(EVENT_LOCATION_GEO_LONG) + ")");
                mLocAddress.setVisibility(View.GONE);
            } else if (bundle.getString(EVENT_LOCATION_ADDRESS).equals("")) {
                mLocName.setText(bundle.getString(EVENT_LOCATION_NAME));
                mLocAddress.setVisibility(View.GONE);
            } else if (!bundle.getString(EVENT_LOCATION_NAME).equals("") && !bundle.getString(EVENT_LOCATION_ADDRESS).equals("")) {
                mLocName.setText(bundle.getString(EVENT_LOCATION_NAME));
                mLocAddress.setText(bundle.getString(EVENT_LOCATION_ADDRESS));
            }

            Intent intent = new Intent();
            intent.putExtra(EVENT_POS, mEventPos);
            intent.putExtra(EDITED, true);
            setResult(Activity.RESULT_OK, intent);
        }
    }
}
