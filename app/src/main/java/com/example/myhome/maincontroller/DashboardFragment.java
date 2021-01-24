package com.example.myhome.maincontroller;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myhome.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class DashboardFragment extends Fragment {
    private SwitchCompat switch1, switch2, switch3, switch4;
    private ImageView imageView1, imageView2, imageView3, imageView4;
    private TextView temp,humi,aqi,fgs;
    private MediaPlayer mp;
    private Calendar c;
    private GifImageView gifImageView;
    private ImageView fgi;
    private Animation animation, anim;
    private Vibrator v;
    private long[] pattern;
    private FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
    private DatabaseReference database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myFragmentView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        database = FirebaseDatabase.getInstance().getReference("users/"+currentFirebaseUser.getUid());
        mp = MediaPlayer.create(getActivity(), R.raw.buttonsound);

        temp = myFragmentView.findViewById(R.id.temp);
        humi = myFragmentView.findViewById(R.id.humidity);
        aqi =  myFragmentView.findViewById(R.id.aqi);
        fgs = myFragmentView.findViewById(R.id.home_fg_status);

        switch1 = myFragmentView.findViewById(R.id.switch1);
        switch2 = myFragmentView.findViewById(R.id.switch2);
        switch3 = myFragmentView.findViewById(R.id.switch3);
        switch4 = myFragmentView.findViewById(R.id.switch4);

        imageView1 = myFragmentView.findViewById(R.id.imageView1);
        imageView2 = myFragmentView.findViewById(R.id.imageView2);
        imageView3 = myFragmentView.findViewById(R.id.imageView3);
        imageView4 = myFragmentView.findViewById(R.id.imageView4);

        fgi = myFragmentView.findViewById(R.id.fg_status);

        gifImageView = myFragmentView.findViewById(R.id.weather_animation);

        animation = new AlphaAnimation(1, 0);
        animation.setDuration(750);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);

        anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);

        v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        pattern = new long[]{0, 100, 1000};

        database.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                Long firestatus = (Long) dataSnapshot.child("fire").child("fireleak").getValue();
                Long gasstatus = (Long) dataSnapshot.child("gas").child("gasleak").getValue();

                if(dataSnapshot.exists()) {
                    if (firestatus == 0 && gasstatus == 1) {
                        fgi.setImageDrawable(getResources().getDrawable(R.drawable.danger_blinking));
                        fgs.setText("FIRE & GAS BOTH WAS DETECTED AT YOUR HOME!");
                        fgs.setTextColor(Color.RED);
                        fgi.startAnimation(animation);
                        fgs.startAnimation(anim);
                        v.vibrate(pattern, 0);
                    } else if (firestatus == 0) {
                        fgi.setImageDrawable(getResources().getDrawable(R.drawable.danger_blinking));
                        fgs.setText("FIRE WAS DETECTED AT YOUR HOME!");
                        fgs.setTextColor(Color.RED);
                        fgi.startAnimation(animation);
                        fgs.startAnimation(anim);
                        v.vibrate(pattern, 0);
                    } else if (gasstatus == 1) {
                        fgi.setImageDrawable(getResources().getDrawable(R.drawable.danger_blinking));
                        fgs.setText("GAS WAS DETECTED AT YOUR HOME!");
                        fgs.setTextColor(Color.RED);
                        fgi.startAnimation(animation);
                        fgs.startAnimation(anim);
                        v.vibrate(pattern, 0);
                    } else {
                        animation.cancel();
                        anim.cancel();
                        v.cancel();
                        fgi.setImageDrawable(getResources().getDrawable(R.drawable.safehome));
                        fgs.setText("YOUR HOME IS SAFE!");
                        fgs.setTextColor(getResources().getColor(R.color.textColor));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });

        database.child("components").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    String p1 = (String) map.get("port1");
                    String p2 = (String) map.get("port2");
                    String p3 = (String) map.get("port3");
                    String p4 = (String) map.get("port4");

                    if (p1.equals("ON")) {
                        imageView1.setImageDrawable(getResources().getDrawable(R.mipmap.light_on));
                        switch1.setChecked(true);
                    } else {
                        imageView1.setImageDrawable(getResources().getDrawable(R.mipmap.light_off));
                        switch1.setChecked(false);
                    }

                    if (p2.equals("ON")) {
                        imageView2.setImageDrawable(getResources().getDrawable(R.mipmap.light_on));
                        switch2.setChecked(true);
                    } else {
                        imageView2.setImageDrawable(getResources().getDrawable(R.mipmap.light_off));
                        switch2.setChecked(false);
                    }

                    if (p3.equals("ON")) {
                        imageView3.setImageDrawable(getResources().getDrawable(R.mipmap.light_on));
                        switch3.setChecked(true);
                    } else {
                        imageView3.setImageDrawable(getResources().getDrawable(R.mipmap.light_off));
                        switch3.setChecked(false);
                    }

                    if (p4.equals("ON")) {
                        imageView4.setImageDrawable(getResources().getDrawable(R.mipmap.light_on));
                        switch4.setChecked(true);
                    } else {
                        imageView4.setImageDrawable(getResources().getDrawable(R.mipmap.light_off));
                        switch4.setChecked(false);
                    }

                    switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (switch1.isChecked()) {
                                mp.start();
                                dataSnapshot.getRef().child("port1").setValue("ON");
                                imageView1.setImageDrawable(getResources().getDrawable(R.mipmap.light_on));
                            } else {
                                mp.start();
                                dataSnapshot.getRef().child("port1").setValue("OFF");
                                imageView1.setImageDrawable(getResources().getDrawable(R.mipmap.light_off));

                            }
                        }
                    });

                    switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (switch2.isChecked()) {
                                mp.start();
                                dataSnapshot.getRef().child("port2").setValue("ON");
                                imageView2.setImageDrawable(getResources().getDrawable(R.mipmap.light_on));
                            } else {
                                mp.start();
                                dataSnapshot.getRef().child("port2").setValue("OFF");
                                imageView2.setImageDrawable(getResources().getDrawable(R.mipmap.light_off));
                            }
                        }
                    });

                    switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if (switch3.isChecked()) {
                                mp.start();
                                dataSnapshot.getRef().child("port3").setValue("ON");
                                imageView3.setImageDrawable(getResources().getDrawable(R.mipmap.light_on));
                            } else {
                                mp.start();
                                dataSnapshot.getRef().child("port3").setValue("OFF");
                                imageView3.setImageDrawable(getResources().getDrawable(R.mipmap.light_off));
                            }
                        }
                    });

                    switch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if (switch4.isChecked()) {
                                mp.start();
                                dataSnapshot.getRef().child("port4").setValue("ON");
                                imageView4.setImageDrawable(getResources().getDrawable(R.mipmap.light_on));
                            } else {
                                mp.start();
                                dataSnapshot.getRef().child("port4").setValue("OFF");
                                imageView4.setImageDrawable(getResources().getDrawable(R.mipmap.light_off));
                            }
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });

        database.child("air").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    Long temperature = (Long) map.get("temp");
                    Long humidity = (Long) map.get("humidity");
                    Long mq135 = (Long) map.get("mq135q");

                    try {
                        c = Calendar.getInstance(TimeZone.getDefault());
                        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
                        if (timeOfDay >= 6 && timeOfDay <= 18) {
                            if (temperature < 20) {
                                GifDrawable gifFromResource;
                                gifFromResource = new GifDrawable(getResources(), R.raw.sunny_snow);
                                gifImageView.setImageDrawable(gifFromResource);
                            } else if (temperature >= 20 && humidity <= 70) {
                                GifDrawable gifFromResource;
                                gifFromResource = new GifDrawable(getResources(), R.raw.sunny_cold);
                                gifImageView.setImageDrawable(gifFromResource);
                            } else if (temperature < 50 && humidity > 70) {
                                GifDrawable gifFromResource;
                                gifFromResource = new GifDrawable(getResources(), R.raw.sunny_normal);
                                gifImageView.setImageDrawable(gifFromResource);
                            } else if (temperature < 32 && humidity > 88) {
                                GifDrawable gifFromResource;
                                gifFromResource = new GifDrawable(getResources(), R.raw.sunny_rain);
                                gifImageView.setImageDrawable(gifFromResource);
                            } else if (temperature < 32 && humidity > 100) {
                                GifDrawable gifFromResource;
                                gifFromResource = new GifDrawable(getResources(), R.raw.sunny_heavy_rain);
                                gifImageView.setImageDrawable(gifFromResource);
                            }
                        }

                        if (timeOfDay >= 18 || timeOfDay <= 6) {
                            if (temperature < 20) {
                                GifDrawable gifFromResource;
                                gifFromResource = new GifDrawable(getResources(), R.raw.night_snow);
                                gifImageView.setImageDrawable(gifFromResource);
                            } else if (temperature >= 20 && humidity <= 70) {
                                GifDrawable gifFromResource;
                                gifFromResource = new GifDrawable(getResources(), R.raw.night_cold);
                                gifImageView.setImageDrawable(gifFromResource);
                            } else if (temperature < 50 && humidity > 70) {
                                GifDrawable gifFromResource;
                                gifFromResource = new GifDrawable(getResources(), R.raw.night_normal);
                                gifImageView.setImageDrawable(gifFromResource);
                            } else if (temperature < 32 && humidity > 88) {
                                GifDrawable gifFromResource;
                                gifFromResource = new GifDrawable(getResources(), R.raw.night_rain);
                                gifImageView.setImageDrawable(gifFromResource);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    if (mq135 < 500) {
                        aqi.setText(mq135.toString() + " PPM \n FRESH AIR");
                        aqi.setTextColor(Color.GREEN);
                    } else if (mq135 > 500 && mq135 <= 1000) {
                        aqi.setText(mq135.toString() + " PPM \n POOR AIR");
                        aqi.setTextColor(Color.YELLOW);
                    } else if (mq135 > 1000) {
                        aqi.setText(mq135.toString() + " PPM \n HAZARDOUS");
                        aqi.setTextColor(Color.RED);
                    }

                    temp.setText(temperature.toString() + " °C");
                    humi.setText(humidity.toString() + " %");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });

        return myFragmentView;
    }
}
