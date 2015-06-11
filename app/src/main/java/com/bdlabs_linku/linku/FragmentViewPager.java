package com.bdlabs_linku.linku;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Used to hold the fragments shown in {@link com.bdlabs_linku.linku.BrowseEventsActivity}
 */
public class FragmentViewPager extends ViewPager {

    public FragmentViewPager(Context context) {
        super(context);
    }

    public FragmentViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @param containerViewId the ViewPager this adapter is being supplied to
     * @param id pass in getItemId(position) as this is whats used internally in this class
     * @return the tag used for this pages fragment
     */
    public String makeFragmentName(int containerViewId, long id) {
        return "android:switcher:" + containerViewId + ":" + id;
    }
}