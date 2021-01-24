package com.example.myhome.water;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myhome.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import me.itangqi.waveloadingview.WaveLoadingView;

public class WaterWaveFragment extends Fragment {
    private WaveLoadingView mWaveLoadingView;
    private FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
    private DatabaseReference database;
    private TextView water_level;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myFragmentView = inflater.inflate(R.layout.fragment_water_wave, container, false);
        mWaveLoadingView= myFragmentView.findViewById(R.id.waveLoadingView);
        water_level= myFragmentView.findViewById(R.id.water_length);
        database = FirebaseDatabase.getInstance().getReference("users/"+currentFirebaseUser.getUid());
        mWaveLoadingView.setAnimDuration(3000);
        mWaveLoadingView.setShapeType(WaveLoadingView.ShapeType.RECTANGLE);
        mWaveLoadingView.setBorderWidth(10);
        mWaveLoadingView.setAmplitudeRatio(27);
        mWaveLoadingView.setWaveBgColor(Color.WHITE);
        mWaveLoadingView.setBorderColor(Color.BLACK);

        database.child("water").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    Long level = (Long) map.get("level");
                    Long distance = (Long) map.get("distance");

                    int a = level.intValue();
                    int b = distance.intValue();
                    double c = ((double) a / b) * 100;

                    if(c>0) {
                        //   mWaveLoadingView.setCenterTitle(level+" "+distance+" "+level/distance+" "+c+" "+divideld1.toString()+" %");
                        mWaveLoadingView.setCenterTitle(String.format("%.2f", c) + " % Full");
                        mWaveLoadingView.setProgressValue((int) c);
                        water_level.setText("Measurement: " + String.format("%.2f", c) + " % Full");

                    }
                    else{
                        mWaveLoadingView.setCenterTitle(String.format("%.2f", 0.1) + " % Full");
                        mWaveLoadingView.setProgressValue((int) 0.1);
                        water_level.setText("Measurement: " + String.format("%.2f", 0.1) + " % Full");

                    }
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
