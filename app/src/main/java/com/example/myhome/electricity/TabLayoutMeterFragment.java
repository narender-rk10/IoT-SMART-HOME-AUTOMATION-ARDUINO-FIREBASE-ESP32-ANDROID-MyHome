package com.example.myhome.electricity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myhome.R;
import com.example.myhome.electricity.adapter.MyMeterAdapter;
import com.google.android.material.tabs.TabLayout;

public class TabLayoutMeterFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    FragmentActivity myContext;
    public static TabLayoutMeterFragment newInstance(String param1, String param2) {
        TabLayoutMeterFragment fragment = new TabLayoutMeterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.fragment_tab_layout_meter, container, false);

        tabLayout = myFragmentView.findViewById(R.id.tabLayoutMeter);
        viewPager = myFragmentView.findViewById(R.id.viewPagerTab);

        tabLayout.addTab(tabLayout.newTab().setText("Today Usage"));
        tabLayout.addTab(tabLayout.newTab().setText("Last Month"));
        tabLayout.addTab(tabLayout.newTab().setText("This Month"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.setSelectedTabIndicatorColor(R.color.textColor);
        tabLayout.setTabTextColors(ColorStateList.valueOf(R.color.textColor));
        tabLayout.setTabRippleColor(ColorStateList.valueOf(R.color.textColor));

        final MyMeterAdapter adapter = new MyMeterAdapter(getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return myFragmentView;
   }
}

