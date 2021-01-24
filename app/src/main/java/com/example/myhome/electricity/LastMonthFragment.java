package com.example.myhome.electricity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;


public class LastMonthFragment extends Fragment {
    private Button lastMonthStats;
    private TextView tenergy,tbill,tcurrent,tpower,tlmonth;
    private static DecimalFormat df = new DecimalFormat("0.00");
    private Calendar c;
    private MediaPlayer mp ;
    private FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
    private DatabaseReference database;

    public static LastMonthFragment newInstance(String param1, String param2) {
        LastMonthFragment fragment = new LastMonthFragment();
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

        View myFragmentView = inflater.inflate(R.layout.fragment_last_month, container, false);
        database = FirebaseDatabase.getInstance().getReference("users/"+currentFirebaseUser.getUid());
        c = Calendar.getInstance(TimeZone.getDefault());
        Integer thisMonth = c.get(Calendar.MONTH)-1;
        String months[] = {"January" , "February" , "March" , "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        final String inmonth = months[thisMonth];

        mp = MediaPlayer.create(getActivity(), R.raw.buttonsound);

        tenergy = myFragmentView.findViewById(R.id.tlkwh);
        tbill = myFragmentView.findViewById(R.id.tlbill);
        tcurrent = myFragmentView.findViewById(R.id.tlcurr);
        tpower = myFragmentView.findViewById(R.id.tlpow);
        tlmonth = myFragmentView.findViewById(R.id.tlmonth);

        tlmonth.setText(inmonth);

        database.child("electricity").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    ArrayList<Double> arrayList1 = new ArrayList();
                    ArrayList<Double> arrayList2 = new ArrayList();
                    ArrayList<Double> arrayList3 = new ArrayList();

                    if(dataSnapshot.exists()) {
                        for (DataSnapshot childDataSnapshot : dataSnapshot.child("power").child(inmonth).getChildren()) {
                            if (childDataSnapshot != null) {
                                try {
                                    arrayList1.add((double) childDataSnapshot.getValue()/10.0f);
                                }
                                catch (ClassCastException classCastException)
                                {
                                    arrayList1.add(Double.valueOf(((Long) childDataSnapshot.getValue()).floatValue()/10.0f));

                                }
                            } else {
                                arrayList1.add(0.0);
                            }
                        }

                        for (DataSnapshot childDataSnapshot : dataSnapshot.child("power").child(inmonth).getChildren()) {
                            if (childDataSnapshot != null)
                            {
                                try {
                                    arrayList2.add((double) childDataSnapshot.getValue());
                                }
                                catch (ClassCastException classCastException)
                                {
                                    arrayList2.add(Double.valueOf(((Long) childDataSnapshot.getValue()).floatValue()));
                                }
                            } else {
                                arrayList2.add(0.0);
                            }
                        }

                        for (DataSnapshot childDataSnapshot : dataSnapshot.child("current").child(inmonth).getChildren()) {
                            if (childDataSnapshot != null) {
                                try {
                                    arrayList3.add((double) childDataSnapshot.getValue());
                                }
                                catch (ClassCastException classCastException)
                                {
                                    arrayList3.add(Double.valueOf(((Long) childDataSnapshot.getValue()).floatValue()));
                                }
                            } else {
                                arrayList3.add(0.0);
                            }
                        }

                    Double temp0100 = (Double) dataSnapshot.child("charges").child("0-100").getValue();
                    Double temp101300 = (Double) dataSnapshot.child("charges").child("101-300").getValue();
                    Double temp301500 = (Double) dataSnapshot.child("charges").child("301-500").getValue();
                    Double temp5011000 = (Double) dataSnapshot.child("charges").child("501-1000").getValue();
                    Double tempm1000 = (Double) dataSnapshot.child("charges").child("M1000").getValue();
                    Double tempoc = (Double) dataSnapshot.child("charges").child("othercharges").getValue();

                    double sumEnergy = 0.0;
                    double sumBill = 0.0;
                    double sumCurrent = 0.0;
                    double sumPower = 0.0;

                      for (Double g : arrayList1) {
                            sumEnergy += g.floatValue();
                        }

                        for (Double k : arrayList2) {
                            sumCurrent += k.floatValue();
                        }

                        for (Double f : arrayList3) {
                            sumPower += f.floatValue();
                        }

                        if (sumEnergy < 100) {
                            sumBill = tempoc + (sumEnergy * temp0100);
                        } else if (sumEnergy >= 100 && sumEnergy < 300) {
                            sumBill = tempoc + (sumEnergy * temp101300);
                        } else if (sumEnergy >= 300 && sumEnergy < 500) {
                            sumBill = tempoc + (sumEnergy * temp301500);
                        } else if (sumEnergy >= 500 && sumEnergy < 1000) {
                            sumBill = tempoc + (sumEnergy * temp5011000);
                        } else if (sumEnergy >= 1000) {
                            sumBill = tempoc + (sumEnergy * tempm1000);
                        }
                        tenergy.setText(df.format(sumEnergy) + " " + "kWh");
                        tbill.setText(df.format(sumBill) + " " + "₹");
                        tcurrent.setText(df.format(sumCurrent) + " " + "A");
                        tpower.setText(df.format(sumPower) + " " + "W");
                    } else {
                        tenergy.setText("NA");
                        tbill.setText("NA");
                        tcurrent.setText("NA");
                        tpower.setText("NA");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });

        lastMonthStats = myFragmentView.findViewById(R.id.blast_button);
        lastMonthStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                Intent i = new Intent(getActivity(), LastMonthGraphActivity.class);
                startActivity(i);
            }
        });

        return myFragmentView;
    }
}