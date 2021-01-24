package com.example.myhome.plant;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.progresviews.ProgressWheel;
import com.example.myhome.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class PlantFragment extends Fragment {

    private TextView plants;
    private ProgressWheel progressWheel;
    private FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
    private DatabaseReference database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.fragment_plant, container, false);

        database = FirebaseDatabase.getInstance().getReference("users/"+currentFirebaseUser.getUid());
        progressWheel = myFragmentView.findViewById(R.id.plant_moisture);
        plants = myFragmentView.findViewById(R.id.plant_status);

        database.child("plant").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    Long plantmo = (Long) map.get("moisture");
                    Double plantmod = plantmo.doubleValue();
                    int soilWet = 500;
                    int soilDry = 750;

                    Double moisture_percentage =  (plantmod / 1023.00) * 100;
                    Double moisture_percentage360 =  (plantmod / 1023.00) * 360;

                    progressWheel.setStepCountText(String.format("%.2f",moisture_percentage)+" %");
                    progressWheel.setPercentage(moisture_percentage360.intValue());

                    if (plantmod < soilWet) {
                        plants.setText("Status:\n Soil is too wet");
                    } else if (plantmod >= soilWet && plantmod < soilDry) {
                        plants.setText("Status: \n Soil moisture is perfect");
                    } else {
                        plants.setText("Status:\n Soil is too dry - time to water!");
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

