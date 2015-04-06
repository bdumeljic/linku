package com.bdlabs_linku.linku;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bdlabs_linku.linku.Utils.ImageChooser;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateNewEventFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateNewEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class CreateNewEventFragment extends Fragment {
    private static final String TAG = "CreateNewEventFragment";
    private OnFragmentInteractionListener mListener ;

    public static final String EVENT_ID = "EventID";

    public static final int REQUEST_PHOTO = 0;
    public static final int REQUEST_PLACE_PICKER = 1;

    private ScrollView mContainer;

    // Input values from view
    private EditText mEditTitle;
    private EditText mEditDescription;
    private Button mEditLocation;
    private Button mEditDay;
    private Button mEditTime;
    private Date mEventDate;
    private Spinner mCategorySpinner;
    private ImageView mCategoryIcon;

    private int day = -1;
    private int month = -1;
    private int year = -1;
    private int hour = -1;
    private int minute = -1;

    String picturePath = null;

    private View mPhotoViewContainer;
    private ImageView mPhotoView;
    private ProgressBar mProgressBar;

    private int mPhotoHeightPixels = 0;
    private static final float PHOTO_ASPECT_RATIO = 1.7777777f;

    private FloatingActionButton mPickImageButton;

    private CreateNewEventActivity mActivity;

    /** Place **/
    private ParseGeoPoint mGeoPoint;
    private String mPlaceName;
    private String mPlaceAddress;
    private String mPlaceId;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment CreateNewEventFragment.
     */
    public static CreateNewEventFragment newInstance() {
        return new CreateNewEventFragment();
    }
    public CreateNewEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_create_new_event, container, false);

        mGeoPoint = new ParseGeoPoint();

        mContainer = (ScrollView) view.findViewById(R.id.scroll_view);

        // Connect to the view
        mEditTitle = (EditText) view.findViewById(R.id.event_title_input);
        mEditDescription = (EditText) view.findViewById(R.id.event_description_input);
        mEditDay = (Button) view.findViewById(R.id.event_day_input);
        mEditTime = (Button) view.findViewById(R.id.event_time_input);

        mEditLocation = (Button) view.findViewById(R.id.event_location_input);
        mEditLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPickPlace();
            }
        });

        mCategorySpinner = (Spinner) view.findViewById(R.id.category);
        ArrayAdapter<CharSequence> categoryAdapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_item, (List) Event.CATEGORIES);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(categoryAdapter);
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCategoryIcon.setImageResource(Event.CATEGORIES_ICONS.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mCategoryIcon = (ImageView) view.findViewById(R.id.cat_icon);

        mPhotoViewContainer = view.findViewById(R.id.event_photo_container);
        mPhotoView = (ImageView) view.findViewById(R.id.event_photo);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        mPickImageButton = (FloatingActionButton) view.findViewById(R.id.pick_image_btn);
        mPickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mActivity = (CreateNewEventActivity) activity;

        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_PHOTO:
                if(resultCode == Activity.RESULT_OK){
                    Uri selectedImage = data.getData();

                    picturePath = ImageChooser.getPath(mActivity, selectedImage);

                    Log.d(TAG, "path " + picturePath);

                    CenterCrop mCenterCrop = new CenterCrop(Glide.get(mActivity).getBitmapPool());

                    Glide.with(this)
                            .load(picturePath)
                            .transform(mCenterCrop)
                            .into(new GlideDrawableImageViewTarget(mPhotoView) {
                                @Override
                                public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
                                    super.onResourceReady(drawable, anim);
                                    mProgressBar.setVisibility(View.GONE);
                                }
                            });
                }
                break;
            case REQUEST_PLACE_PICKER:
                if (resultCode == Activity.RESULT_OK) {
                    // The user has selected a place. Extract the name and address.
                    final Place place = PlacePicker.getPlace(data, mActivity);
                    setLocation(place);
                }
                break;
        }
    }

    /**
     * Save the event to the backend, after checking that all field have been filled.
     */
    public void saveEvent() {
        if(validateEvent()) {
            String title = mEditTitle.getText().toString().trim();

            // Set up a progress dialog
            final ProgressDialog dialog = new ProgressDialog(mActivity);
            dialog.setMessage(getString(R.string.progress_create_event));
            dialog.show();

            // Create an event
            final Event event = new Event();
            event.setCreator(ParseUser.getCurrentUser());
            event.setTitle(title);
            event.setDescription(mEditDescription.getText().toString().trim());
            event.setTime(mEventDate);
            event.setAttending(0);
            event.setCategory(mCategorySpinner.getSelectedItemPosition());

            if (picturePath != null) {
                if (!picturePath.equals("")) {
                    event.setPhoto(picturePath);
                    event.setHasUploadedPhoto(true);
                } else {
                    event.setHasUploadedPhoto(false);
                }
            }

            event.setLocation(mPlaceId, mGeoPoint, mPlaceName, mPlaceAddress);

            // Save the event
            event.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    dialog.dismiss();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(EVENT_ID, event.getObjectId());
                    mActivity.setResult(Activity.RESULT_OK, resultIntent);
                    mActivity.finish();
                }
            });
        }
    }

    /**
     * Check if all the event details have been filled in correctly.
     * @return True if everything is correct. False if there is data missing.
     */
    public boolean validateEvent() {
        if(mEditTitle.getText().toString().matches("")) {
            Toast.makeText(mActivity, "Event does nt have a title", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (day == -1 || hour == -1) {
            Toast.makeText(mActivity, "Date or time is not set", Toast.LENGTH_SHORT).show();
            return false;
        }
        // add description to model
        else if (mEditDescription.getText().toString().matches("")) {
            Toast.makeText(mActivity, "There is no event description", Toast.LENGTH_SHORT).show();
            return false;
        }
        // add place to model
        else if (mGeoPoint.getLatitude() == 0 || mGeoPoint.getLongitude() == 0) {
            Toast.makeText(mActivity, "No location picked for the event", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            mEventDate = new Date(year, month, day, hour, minute);
            return true;
        }
    }

    /**
     * Set the date picked that the user picked from the date widget.
     * @param dayOfMonth
     * @param monthOfYear
     * @param yearPicked
     */
    public void setDate(int dayOfMonth, int monthOfYear, int yearPicked) {
        day = dayOfMonth;
        month = monthOfYear;
        year = yearPicked;
        mEditDay.setText(day + " / " + month + " / " + year);
        mEditDay.setTextColor(getResources().getColor(R.color.body_dark));
    }

    /**
     * Set the time the user picked from the event from the time widget.
     * @param hourPicked
     * @param minutePicked
     */
    public void setTime(int hourPicked, int minutePicked) {
        hour = hourPicked;
        minute = minutePicked;

        Date time = new Date();
        time.setHours(hour);
        time.setMinutes(minute);
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String formattedTime = timeFormat.format(time.getTime());

        mEditTime.setText(formattedTime);
        mEditTime.setTextColor(getResources().getColor(R.color.body_dark));
    }

    public void recomputePhotoMetrics() {
        mPhotoHeightPixels = (int) (mPhotoView.getWidth() / PHOTO_ASPECT_RATIO);
        Log.d(TAG, "width: " + mPhotoView.getWidth() +" height: " + mPhotoHeightPixels + " container height " + mContainer.getHeight());
        mPhotoHeightPixels = Math.min(mPhotoHeightPixels, mContainer.getHeight() * 2 / 3);
        Log.d(TAG, "height: " + mPhotoHeightPixels);

        if (mPhotoView.getHeight() == mPhotoHeightPixels) {
            ViewGroup.LayoutParams lp;
            lp = mPhotoViewContainer.getLayoutParams();
            lp.height = mPhotoHeightPixels;
            mPhotoViewContainer.setLayoutParams(lp);
        }
    }

    private void pickImage() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_PHOTO);
    }

    public void startPickPlace() {
        // Construct an intent for the place picker
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(mActivity);
            startActivityForResult(intent, REQUEST_PLACE_PICKER);

        } catch (GooglePlayServicesRepairableException e) {
            // ...
        } catch (GooglePlayServicesNotAvailableException e) {
            // ...
        }
    }

    public void setLocation(Place place) {
        mPlaceName = place.getName().toString();
        mPlaceAddress = place.getAddress().toString();
        mPlaceId = place.getId();

        mGeoPoint.setLatitude(place.getLatLng().latitude);
        mGeoPoint.setLongitude(place.getLatLng().longitude);

        mEditLocation.setText(mPlaceName);
        mEditLocation.setTextColor(getResources().getColor(R.color.body_dark));
        mEditLocation.setTextAppearance(mActivity, android.R.style.TextAppearance_Material_Body1);

        Log.d("CreateLocationEvent", "name: " + mPlaceName + " address: " + mPlaceAddress + " loc: " + mGeoPoint.toString() + " id " + mPlaceId);
    }
}

