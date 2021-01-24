package com.example.myhome.electricity.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.myhome.electricity.LastMonthFragment;
import com.example.myhome.electricity.ThisMonthFragment;
import com.example.myhome.electricity.TodayUsageFragment;

public class MyMeterAdapter extends FragmentPagerAdapter {

    private Context myContext;
    private int totalTabs;

    public MyMeterAdapter(FragmentManager fm, int totalTabs) {
        super(fm);
        this.totalTabs = totalTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                TodayUsageFragment todayUsageFragment = new TodayUsageFragment();
                return todayUsageFragment;
            case 1:
                LastMonthFragment lastMonthFragment = new LastMonthFragment();
                return lastMonthFragment;
            case 2:
                ThisMonthFragment thisMonthFragment = new ThisMonthFragment();
                return thisMonthFragment;
            default:
                TodayUsageFragment todayUsageFragmentdefault = new TodayUsageFragment();
                return todayUsageFragmentdefault;

        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}