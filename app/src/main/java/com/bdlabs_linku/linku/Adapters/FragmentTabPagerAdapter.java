package com.bdlabs_linku.linku.Adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.bdlabs_linku.linku.EventsFragment;
import com.bdlabs_linku.linku.FragmentViewPager;
import com.bdlabs_linku.linku.MapEventsFragment;
import com.bdlabs_linku.linku.R;

import java.util.Locale;

/**
 * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class FragmentTabPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    FragmentManager mFM;
    Context mContext;
    FragmentViewPager mViewPager;

    public FragmentTabPagerAdapter(Context context, FragmentManager fm, FragmentViewPager viewPager) {
        super(fm);

        this.mContext = context;
        this.mFM = fm;
        this.mViewPager = viewPager;

    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        switch(position) {
            case 0:
                return EventsFragment.newInstance();
            case 1:
                return MapEventsFragment.newInstance(position, "");
        }
        return null;
    }

    @Override
    public int getCount() {
        // Show total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return mContext.getString(R.string.title_feed).toUpperCase(l);
            case 1:
                return mContext.getString(R.string.title_map).toUpperCase(l);
        }
        return null;
    }

    /**
     * @return may return null if the fragment has not been instantiated yet for that position - this depends on if the fragment has been viewed
     * yet OR is a sibling covered by {@link android.support.v4.view.ViewPager#setOffscreenPageLimit(int)}. Can use this to call methods on
     * the current positions fragment.
     */
    public @Nullable
    Fragment getFragmentForPosition(int position) {
        String tag = mViewPager.makeFragmentName(mViewPager.getId(), getItemId(position));
        return mFM.findFragmentByTag(tag);
    }
}