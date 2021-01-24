package com.example.myhome.onboarding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.myhome.R;
import com.example.myhome.authentication.LoginActivity;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

public class OnBoardingActivity extends AppCompatActivity {

    private ViewPager pager;
    private SmartTabLayout indicator;
    private TextView skip;
    private TextView next;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }

        mp = MediaPlayer.create(OnBoardingActivity.this, R.raw.buttonsound);
        pager = (ViewPager)findViewById(R.id.pager);
        indicator = (SmartTabLayout)findViewById(R.id.indicator);
        skip = (TextView)findViewById(R.id.skip);
        next = (TextView)findViewById(R.id.next);

        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0 :
                        mp.start();
                        return new S1Fragment();
                    case 1 :
                        mp.start();
                        return new S3Fragment();
                    case 2 :
                        mp.start();
                        return new S5Fragment();
                    case 3:
                        mp.start();
                        return new S2Fragment();
                    case 4 :
                        mp.start();
                        return new S4Fragment();
                    case 5 :
                        mp.start();
                        return new S6Fragment();
                    case 6 :
                        mp.start();
                        return new S7Fragment();
                    default: return null;
                }
            }

            @Override
            public int getCount() {
                return 7;
            }
        };

        pager.setAdapter(adapter);

        indicator.setViewPager(pager);

        indicator.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if(position == 6){
                    skip.setVisibility(View.GONE);
                    next.setText("Done");
                } else {
                    skip.setVisibility(View.VISIBLE);
                    next.setText("Next");
                }
            }

        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishOnboarding();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pager.getCurrentItem() == 6){
                    finishOnboarding();
                } else {
                    pager.setCurrentItem(pager.getCurrentItem() + 1, true);
                }
            }
        });
    }

    private void finishOnboarding() {
        SharedPreferences preferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        preferences.edit().putBoolean("onboarding_complete",true).apply();

        Intent main = new Intent(OnBoardingActivity.this, LoginActivity.class);
        startActivity(main);
        finish();
    }
}