package com.example.myhome.miscellaneous;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myhome.R;
import com.example.myhome.maincontroller.BottomNavBarActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    private EditText ZtH, HtT, TtF, FtT, MtT, OC, wl;
    private Button buttonSE1, buttonSW1;
    private Double ZtoH, HtoT, TtoF, FtoT, MtoT, rOC;
    private Long swl;
    private Long vwl;
    private Double vZtH, vHtT, vTtF, vFtT, vMtT, vOC;
    private MediaPlayer mp;
    private FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        database = FirebaseDatabase.getInstance().getReference("users/"+currentFirebaseUser.getUid());
        mp = MediaPlayer.create(getApplicationContext(), R.raw.buttonsound);
        ZtH = findViewById(R.id.ZtH);
        HtT = findViewById(R.id.HtT);
        TtF = findViewById(R.id.TtF);
        FtT = findViewById(R.id.FtT);
        MtT = findViewById(R.id.MtT);
        OC = findViewById(R.id.OC);
        wl = findViewById(R.id.wl);
        buttonSE1 = findViewById(R.id.buttonSE1);
        buttonSW1 = findViewById(R.id.buttonSW1);

        database.child("electricity").child("charges").addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

            if(dataSnapshot.exists()) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                ZtoH = (Double) map.get("0-100");
                HtoT = (Double) map.get("101-300");
                TtoF = (Double) map.get("301-500");
                FtoT = (Double) map.get("501-1000");
                MtoT = (Double) map.get("M1000");
                rOC = (Double) map.get("othercharges");

                ZtH.setText(ZtoH.toString());
                HtT.setText(HtoT.toString());
                TtF.setText(TtoF.toString());
                FtT.setText(FtoT.toString());
                MtT.setText(MtoT.toString());
                OC.setText(rOC.toString());

                }
            else{
                ZtH.setText(0);
                HtT.setText(0);
                TtF.setText(0);
                FtT.setText(0);
                MtT.setText(0);
                OC.setText(0);
            }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });

        database.child("water").addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        swl = (Long) map.get("distance");
                        wl.setText(swl.toString());
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        buttonSE1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mp.start();
                if (validateInput(ZtH.getText().toString(), HtT.getText().toString(), TtF.getText().toString(), FtT.getText().toString(), MtT.getText().toString(), OC.getText().toString())) {

                    vZtH = Double.parseDouble(ZtH.getText().toString());
                    vHtT = Double.parseDouble(HtT.getText().toString());
                    vTtF = Double.parseDouble(TtF.getText().toString());
                    vFtT = Double.parseDouble(FtT.getText().toString());
                    vMtT = Double.parseDouble(MtT.getText().toString());
                    vOC = Double.parseDouble(OC.getText().toString());
                }
                database.child("electricity/charges").addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            if (validateInput(ZtH.getText().toString(), HtT.getText().toString(), TtF.getText().toString(), FtT.getText().toString(), MtT.getText().toString(), OC.getText().toString())) {

                                HashMap<String,Double> mapElect=new HashMap<String,Double>();

                                mapElect.put("0-100",vZtH);
                                mapElect.put("101-300",vHtT);
                                mapElect.put("301-500",vTtF);
                                mapElect.put("501-1000",vFtT);
                                mapElect.put("M1000",vMtT);
                                mapElect.put("othercharges",vOC);

                                dataSnapshot.getRef().setValue(mapElect).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        DynamicToast.makeSuccess(SettingsActivity.this, "Updated Successfully!", 10).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        DynamicToast.makeError(SettingsActivity.this, "Error Occured!", 10).show();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("User", databaseError.getMessage());
                    }
                });

            }
        });

        buttonSW1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                if (validateInputDistance(wl.getText().toString())) {
                    vwl = Long.parseLong(wl.getText().toString());
                }
                database.child("water").addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            if (validateInputDistance(wl.getText().toString())) {
                                dataSnapshot.getRef().child("distance").setValue(vwl).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        DynamicToast.makeSuccess(SettingsActivity.this, "Updated Successfully!", 10).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                DynamicToast.makeError(SettingsActivity.this, "Error Occured!", 10).show();
                                            }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("User", databaseError.getMessage());
                    }
                });
            }
        });

    }

    public boolean validateInput(String a, String b, String c, String d, String e, String f){

        if(a.isEmpty()){
            ZtH.setError("This field can't be empty");
            return false;
        }
        if(b.isEmpty()){
            HtT.setError("This field can't be empty");
            return false;
        }
        if(c.isEmpty()){
            TtF.setError("This field can't be empty");
            return false;
        }
        if(d.isEmpty()){
            FtT.setError("This field can't be empty");
            return false;
        }
        if(e.isEmpty()){
            MtT.setError("This field can't be empty");
            return false;
        }
        if(f.isEmpty()){
            OC.setError("This field can't be empty");
            return false;
        }
        if(!a.contains(".")){
            ZtH.setError("Enter an decimal value");
            return false;
        }
        if(!b.contains(".")){
            HtT.setError("Enter an decimal value");
            return false;
        }
        if(!c.contains(".")){
            TtF.setError("Enter an decimal value");
            return false;
        }
        if(!d.contains(".")){
            FtT.setError("Enter an decimal value");
            return false;
        }
        if(!e.contains(".")){
            MtT.setError("Enter an decimal value");
            return false;
        }
        if(!f.contains(".")){
            OC.setError("Enter an decimal value");
            return false;
        }

        return true;
    }

    public boolean validateInputDistance(String a){

        if(a.isEmpty()){
            ZtH.setError("This field can't be empty");
            return false;
        }
        if(!TextUtils.isDigitsOnly(a)){
            wl.setError("Enter an numeric value");
            return false;
        }
        return true;
    }
}
