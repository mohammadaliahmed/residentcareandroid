package com.siliconst.residentcare.Activities.Fragments;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.siliconst.residentcare.Models.Department;

import java.util.List;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;
    List<Department> departmentList;

    public SimpleFragmentPagerAdapter(Context context, FragmentManager fm, List<Department> departmentList) {
        super(fm);
        mContext = context;
        this.departmentList = departmentList;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        return new FaqDataFragment(departmentList.get(position).getId());
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return departmentList.size();
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return departmentList.get(position).getName();
    }
}
