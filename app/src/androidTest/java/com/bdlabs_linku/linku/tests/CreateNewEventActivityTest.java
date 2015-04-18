package com.bdlabs_linku.linku.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import com.bdlabs_linku.linku.CreateNewEventActivity;
import com.bdlabs_linku.linku.R;


public class CreateNewEventActivityTest extends ActivityInstrumentationTestCase2<CreateNewEventActivity>{

    private CreateNewEventActivity mCreateEventActivity;
    private EditText mEventTitle;

    public void testPreconditions() {
        assertNotNull("mCreateEventActivity is null", mCreateEventActivity);
        assertNotNull("mEventTitle is null", mEventTitle);
    }

    public CreateNewEventActivityTest() {
        super(CreateNewEventActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mCreateEventActivity = getActivity();
        mEventTitle =
                (EditText) mCreateEventActivity
                        .findViewById(R.id.event_title_input);
    }

    public void testEventTitlePlaceholder() {
        final String expected =
                mCreateEventActivity.getString(R.string.placeholder_events_item_title);
        final String actual = mEventTitle.getHint().toString();
        assertEquals(expected, actual);
    }
}
