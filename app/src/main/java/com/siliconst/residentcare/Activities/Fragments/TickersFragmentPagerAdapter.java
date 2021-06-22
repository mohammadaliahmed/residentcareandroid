package com.siliconst.residentcare.Activities.Fragments;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class TickersFragmentPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;
    List<String> statusList;

    public TickersFragmentPagerAdapter(Context context, FragmentManager fm, List<String> statusList) {
        super(fm);
        mContext = context;
        this.statusList = statusList;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        return new StaffDataFragment(statusList.get(position));
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return statusList.size();
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return statusList.get(position);
    }
}
