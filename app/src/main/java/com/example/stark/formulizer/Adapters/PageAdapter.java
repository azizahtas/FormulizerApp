package com.example.stark.formulizer.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stark on 02-03-2017.
 */

public class PageAdapter extends FragmentPagerAdapter{
    private List<Fragment> mFragmentList = new ArrayList<>();
    private ArrayList<String> mFragmentTitleList = new ArrayList<>();

    public ArrayList<String> getmFragmentTitleList() {
        return mFragmentTitleList;
    }

    public void setmFragmentTitleList(ArrayList<String> mFragmentTitleList) {
        this.mFragmentTitleList = mFragmentTitleList;
    }

    public PageAdapter(FragmentManager manager) {
        super(manager);
        manager.enableDebugLogging(true);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    public List<Fragment> exportList(){
        return mFragmentList;
    }
    public void importList(List<Fragment> savedPages) {
        mFragmentList = savedPages;
    }
}
