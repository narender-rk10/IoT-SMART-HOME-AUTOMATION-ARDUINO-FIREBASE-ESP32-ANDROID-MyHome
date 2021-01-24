package com.example.myhome.electricity;

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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TodayUsageFragment extends Fragment {
    private TextView tenergy,tbill,tcurrent,tpower,tvoltage,tfrequency,tpf,tdate;
    private static DecimalFormat df = new DecimalFormat("0.00");
    private Calendar c;
    private FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
    private DatabaseReference database;
    private double tempenergy = 0.0 ,tempcurrent = 0.0 ,temppower = 0.0;

    public static TodayUsageFragment newInstance(String param1, String param2) {
        TodayUsageFragment fragment = new TodayUsageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.fragment_today_usage, container, false);

        database = FirebaseDatabase.getInstance().getReference("users/"+currentFirebaseUser.getUid());
        c = Calendar.getInstance(TimeZone.getDefault());
        Integer thisMonth = c.get(Calendar.MONTH);
        String months[] = {"January" , "February" , "March" , "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        final String inmonth = months[thisMonth];
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        final String formattedDate= formatter.format(date);

        tenergy = myFragmentView.findViewById(R.id.tkwh);
        tbill = myFragmentView.findViewById(R.id.tbill);
        tcurrent = myFragmentView.findViewById(R.id.tcurr);
        tpower = myFragmentView.findViewById(R.id.tpow);
        tvoltage = myFragmentView.findViewById(R.id.tvol);
        tfrequency = myFragmentView.findViewById(R.id.tfreq);
        tpf = myFragmentView.findViewById(R.id.tpf);
        tdate= myFragmentView.findViewById(R.id.tdate);

        tdate.setText(formattedDate);

        database.child("electricity").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                try {
                    tempenergy = (Double) dataSnapshot.child("power").child(inmonth).child(formattedDate).getValue()/10.0f;
                }catch (ClassCastException classCastException) {
                    tempenergy = (Long) dataSnapshot.child("power").child(inmonth).child(formattedDate).getValue()/10.0f;
                }catch(NullPointerException nullPointerException){
                    tempenergy = 0.0;
                    }
                    Double tempbill = null;
                try {
                    tempcurrent = (Double) dataSnapshot.child("current").child(inmonth).child(formattedDate).getValue();

                }catch (ClassCastException classCastException) {
                    tempcurrent = (Long) dataSnapshot.child("current").child(inmonth).child(formattedDate).getValue();
                }catch(NullPointerException nullPointerException){
                    tempcurrent = 0.0;
                }

                try{
                    temppower = (Double) dataSnapshot.child("power").child(inmonth).child(formattedDate).getValue();
                }catch (ClassCastException classCastException)
                {
                    temppower = (Long) dataSnapshot.child("power").child(inmonth).child(formattedDate).getValue();
                }catch(NullPointerException nullPointerException){
                    temppower = 0.0;
                }
                    Long tempvoltage = (Long) dataSnapshot.child("voltage").getValue();
                    Long tempfrequency = (Long) dataSnapshot.child("frequency").getValue();
                    Double temppf = (Double) dataSnapshot.child("pf").getValue();
                    Double temp0100 = (Double) dataSnapshot.child("charges").child("0-100").getValue();
                    Double temp101300 = (Double) dataSnapshot.child("charges").child("101-300").getValue();
                    Double temp301500 = (Double) dataSnapshot.child("charges").child("301-500").getValue();
                    Double temp5011000 = (Double) dataSnapshot.child("charges").child("501-1000").getValue();
                    Double tempm1000 = (Double) dataSnapshot.child("charges").child("M1000").getValue();

                   if (tempenergy < 100) {
                            tempbill = tempenergy * temp0100;
                        } else if (tempenergy >= 100 && tempenergy < 300) {
                            tempbill = tempenergy * temp101300;
                        } else if (tempenergy >= 300 && tempenergy < 500) {
                            tempbill = tempenergy * temp301500;
                        } else if (tempenergy >= 500 && tempenergy < 1000) {
                            tempbill = tempenergy * temp5011000;
                        } else if (tempenergy >= 1000) {
                            tempbill = tempenergy * tempm1000;
                        }
                        tenergy.setText(df.format(tempenergy) + " " + "kWh");
                        tbill.setText(df.format(tempbill) + " " + "₹");
                        tcurrent.setText(df.format(tempcurrent) + " " + "A");
                        tpower.setText(df.format(temppower) + " " + "W");
                        tvoltage.setText(tempvoltage.toString() + " " + "V");
                        tfrequency.setText(tempfrequency.toString() + " " + "Hz");
                        tpf.setText(temppf.toString());

                    } else {
                        tenergy.setText("NA");
                        tbill.setText("NA");
                        tcurrent.setText("NA");
                        tpower.setText("NA");
                        tvoltage.setText("NA");
                        tfrequency.setText("NA");
                        tpf.setText("NA");

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