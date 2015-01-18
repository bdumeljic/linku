package com.bdlabs_linku.linku;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Handler;


public class ViewEventActivity extends ActionBarActivity implements ViewEventFragment.OnFragmentInteractionListener, ObservableScrollView.Callbacks {

    public static final String TRANSITION_NAME_PHOTO = "photo";
    public static final float SESSION_BG_COLOR_SCALE_FACTOR = 0.75f;

    protected static String STATIC_MAP_API_ENDPOINT;

    ObservableScrollView mScrollView;

    private View mPhotoViewContainer;
    private ImageView mPhotoView;

    private TextView mAbstract;
    private LinearLayout mTags;
    private ViewGroup mTagsContainer;
    private TextView mRequirements;
    private View mHeaderBox;
    private View mDetailsContainer;

    private View mScrollViewChild;
    private TextView mTitle;
    private TextView mSubtitle;

    private int mPhotoHeightPixels;
    private int mHeaderHeightPixels;

    private int mEventId;

    private int mSessionColor;

    private Image mEventImage;
    private TextView mEventName;
    private TextView mEventTime;
    private TextView mEventDistance;
    private TextView mEventAttendees;
    private ImageView mMapView;

    private boolean mHasPhoto = true;
    private static final float PHOTO_ASPECT_RATIO = 1.7777777f;
    private float mMaxHeaderElevation;

    private android.os.Handler mHandler = new android.os.Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

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

        mEventId = getIntent().getIntExtra(ViewEventFragment.EVENT_POSITION, 0);
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

        mScrollViewChild = findViewById(R.id.scroll_view_child);
        mScrollViewChild.setVisibility(View.INVISIBLE);

        mDetailsContainer = findViewById(R.id.details_container);
        mHeaderBox = findViewById(R.id.header_session);
        mTitle = (TextView) findViewById(R.id.session_title);
        mSubtitle = (TextView) findViewById(R.id.session_subtitle);
        mPhotoViewContainer = findViewById(R.id.session_photo_container);
        mPhotoView = (ImageView) findViewById(R.id.session_photo);
        mMapView = (ImageView) findViewById(R.id.location_map);

        ViewCompat.setTransitionName(mPhotoView, TRANSITION_NAME_PHOTO);

        // Set event name
        mTitle = (TextView) findViewById(R.id.session_title);
        mTitle.setText(EventModel.EVENTS.get(mEventId).name);

        mHeaderBox.setBackgroundColor(mSessionColor);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(scaleColor(mSessionColor, 0.8f, false));
        }

        // Set event time
        mSubtitle = (TextView) findViewById(R.id.session_subtitle);
        DateFormat dateFormat = new SimpleDateFormat("HH:mm, d MMM yyyy");
        String formattedDate = dateFormat.format(EventModel.EVENTS.get(mEventId).time.getTime());
        mSubtitle.setText(formattedDate);

        mPhotoViewContainer.setBackgroundColor(scaleSessionColorToDefaultBG(mSessionColor));
        mHasPhoto = true;
        mPhotoView.setImageResource(R.drawable.tri_pattern);
        recomputePhotoAndScrollingMetrics();

        Log.e("AAFKLAK", "STARTING");
        onScrollChanged(0, 0); // trigger scroll handling
        mScrollViewChild.setVisibility(View.VISIBLE);

        // Set event distance from user's current location
        // TODO add location
        //mEventDistance = (TextView) view.findViewById(R.id.distance);
        //mEventDistance.setText("2 km away");

        // Set number of people attending
        //mEventAttendees = (TextView) view.findViewById(R.id.attendees);
        //mEventAttendees.setText((Integer.toString(EventModel.EVENTS.get(mEventId).attendees)) + " attendees");

        STATIC_MAP_API_ENDPOINT = "http://maps.google.com/maps/api/staticmap?center=" + Double.toString(EventModel.EVENTS.get(mEventId).getLocation().getLatitude()) + "," + Double.toString(EventModel.EVENTS.get(mEventId).getLocation().getLongitude()) + "&zoom=16&size=1100x300&scale=2&sensor=false&markers=color:blue%7Clabel:%7C" + Double.toString(EventModel.EVENTS.get(mEventId).getLocation().getLatitude()) + "," + Double.toString(EventModel.EVENTS.get(mEventId).getLocation().getLongitude()) + "";

        AsyncTask<Void, Void, Bitmap> setImageFromUrl = new AsyncTask<Void, Void, Bitmap>(){
            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bmp = null;
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet request = new HttpGet(STATIC_MAP_API_ENDPOINT);

                InputStream in = null;
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

    @Override
    public void onFragmentInteraction(int mEventId) {}



    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener
            = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Log.d("SCROLL", "scrolling!");
            recomputePhotoAndScrollingMetrics();
        }
    };


    private void recomputePhotoAndScrollingMetrics() {
        mHeaderHeightPixels = mHeaderBox.getHeight();

        mPhotoHeightPixels = 0;
        if (mHasPhoto) {
            mPhotoHeightPixels = (int) (mPhotoView.getWidth() / PHOTO_ASPECT_RATIO);
            mPhotoHeightPixels = Math.min(mPhotoHeightPixels, mScrollView.getHeight() * 2 / 3);
        }

        Log.e("SCROLL", "header height " + mHeaderHeightPixels);
        Log.e("SCROLL", "image height " + mPhotoHeightPixels);

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
}
