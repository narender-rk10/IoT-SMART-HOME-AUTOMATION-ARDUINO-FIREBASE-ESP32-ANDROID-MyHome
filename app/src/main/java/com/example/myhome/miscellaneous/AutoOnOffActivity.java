package com.example.myhome.miscellaneous;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.dpro.widgets.OnWeekdaysChangeListener;
import com.dpro.widgets.WeekdaysPicker;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AutoOnOffActivity extends AppCompatActivity {
    private WeekdaysPicker pw1, pw2, pw3, pw4;
    private TextView startTimeP1, startTimeP2, startTimeP3, startTimeP4;
    private TextView stopTimeP1, stopTimeP2, stopTimeP3, stopTimeP4;
    private Button finalButton1, finalButton2, finalButton3, finalButton4;
    private ArrayList<Integer> pa1, pa2, pa3, pa4;
    private String selectedDays1, selectedDays2, selectedDays3, selectedDays4;
    private TimePickerDialog startPicker1, startPicker2, startPicker3, startPicker4,
            stopPicker1, stopPicker2, stopPicker3, stopPicker4;
    private String sd1, sontime1, sofftime1, sd2, sontime2, sofftime2,
            sd3, sontime3, sofftime3, sd4, sontime4, sofftime4;
    private MediaPlayer mp;
    private FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_on_off);

        database = FirebaseDatabase.getInstance().getReference("users/"+currentFirebaseUser.getUid());
        mp = MediaPlayer.create(getApplicationContext(), R.raw.buttonsound);

        pw1 = findViewById(R.id.weekdaysp1);
        pw2 = findViewById(R.id.weekdaysp2);
        pw3 = findViewById(R.id.weekdaysp3);
        pw4 = findViewById(R.id.weekdaysp4);

        startTimeP1 = findViewById(R.id.startTime1);
        startTimeP2 = findViewById(R.id.startTime2);
        startTimeP3 = findViewById(R.id.startTime3);
        startTimeP4 = findViewById(R.id.startTime4);

        stopTimeP1 = findViewById(R.id.stopTime1);
        stopTimeP2 = findViewById(R.id.stopTime2);
        stopTimeP3 = findViewById(R.id.stopTime3);
        stopTimeP4 = findViewById(R.id.stopTime4);

        finalButton1 = findViewById(R.id.buttonUp1);
        finalButton2 = findViewById(R.id.buttonUp2);
        finalButton3 = findViewById(R.id.buttonUp3);
        finalButton4 = findViewById(R.id.buttonUp4);

        final Calendar cldr = Calendar.getInstance();
        final int hour = cldr.get(Calendar.HOUR_OF_DAY);
        final int minutes = cldr.get(Calendar.MINUTE);

        startTimeP1.setText(utilTime(hour) + ":" + utilTime(minutes));
        startTimeP2.setText(utilTime(hour) + ":" + utilTime(minutes));
        startTimeP3.setText(utilTime(hour) + ":" + utilTime(minutes));
        startTimeP4.setText(utilTime(hour) + ":" + utilTime(minutes));

        stopTimeP1.setText(utilTime(hour) + ":" + utilTime(minutes));
        stopTimeP2.setText(utilTime(hour) + ":" + utilTime(minutes));
        stopTimeP3.setText(utilTime(hour) + ":" + utilTime(minutes));
        stopTimeP4.setText(utilTime(hour) + ":" + utilTime(minutes));

        startTimeP1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startPicker1 = new TimePickerDialog(AutoOnOffActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                startTimeP1.setText(utilTime(sHour) + ":" + utilTime(sMinute));
                            }
                        }, hour, minutes, true);
                startPicker1.show();
            }
        });

        startTimeP2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startPicker2 = new TimePickerDialog(AutoOnOffActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                startTimeP2.setText(utilTime(sHour) + ":" + utilTime(sMinute));
                            }
                        }, hour, minutes, true);
                startPicker2.show();
            }
        });

        startTimeP3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startPicker3 = new TimePickerDialog(AutoOnOffActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                startTimeP3.setText(utilTime(sHour) + ":" + utilTime(sMinute));
                            }
                        }, hour, minutes, true);
                startPicker3.show();
            }
        });

        startTimeP4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startPicker4 = new TimePickerDialog(AutoOnOffActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                startTimeP4.setText(utilTime(sHour) + ":" + utilTime(sMinute));
                            }
                        }, hour, minutes, true);
                startPicker4.show();
            }
        });

        stopTimeP1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopPicker1 = new TimePickerDialog(AutoOnOffActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                stopTimeP1.setText(utilTime(sHour) + ":" + utilTime(sMinute));
                            }
                        }, hour, minutes, true);
                stopPicker1.show();
            }
        });

        stopTimeP2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopPicker2 = new TimePickerDialog(AutoOnOffActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                stopTimeP2.setText(utilTime(sHour) + ":" + utilTime(sMinute));
                            }
                        }, hour, minutes, true);
                stopPicker2.show();
            }
        });

        stopTimeP3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopPicker3 = new TimePickerDialog(AutoOnOffActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                stopTimeP3.setText(utilTime(sHour) + ":" + utilTime(sMinute));
                            }
                        }, hour, minutes, true);
                stopPicker3.show();
            }
        });

        stopTimeP4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopPicker4 = new TimePickerDialog(AutoOnOffActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                stopTimeP4.setText(utilTime(sHour) + ":" + utilTime(sMinute));
                            }
                        }, hour, minutes, true);
                stopPicker4.show();
            }
        });

        selectedDays1 = pw1.getSelectedDaysText().toString();
        selectedDays2 = pw2.getSelectedDaysText().toString();
        selectedDays3 = pw3.getSelectedDaysText().toString();
        selectedDays4 = pw4.getSelectedDaysText().toString();

        pw1.setOnWeekdaysChangeListener(new OnWeekdaysChangeListener() {
            @Override
            public void onChange(View view, int clickedDayOfWeek, List<Integer> selectedDays) {
                selectedDays1 = pw1.getSelectedDaysText().toString();
            }
        });

        pw2.setOnWeekdaysChangeListener(new OnWeekdaysChangeListener() {
            @Override
            public void onChange(View view, int clickedDayOfWeek, List<Integer> selectedDays) {
                selectedDays2 = pw2.getSelectedDaysText().toString();
            }
        });

        pw3.setOnWeekdaysChangeListener(new OnWeekdaysChangeListener() {
            @Override
            public void onChange(View view, int clickedDayOfWeek, List<Integer> selectedDays) {
                selectedDays3 = pw3.getSelectedDaysText().toString();
            }
        });

        pw4.setOnWeekdaysChangeListener(new OnWeekdaysChangeListener() {
            @Override
            public void onChange(View view, int clickedDayOfWeek, List<Integer> selectedDays) {
                selectedDays4 = pw4.getSelectedDaysText().toString();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        database.child("AutomaticOnOff").addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    sd1 = (String) dataSnapshot.child("port1").child("days").getValue();
                    sontime1 = (String) dataSnapshot.child("port1").child("ontime").getValue();
                    sofftime1 = (String) dataSnapshot.child("port1").child("offtime").getValue();

                    sd2 = (String) dataSnapshot.child("port2").child("days").getValue();
                    sontime2 = (String) dataSnapshot.child("port2").child("ontime").getValue();
                    sofftime2 = (String) dataSnapshot.child("port2").child("offtime").getValue();

                    sd3 = (String) dataSnapshot.child("port3").child("days").getValue();
                    sontime3 = (String) dataSnapshot.child("port3").child("ontime").getValue();
                    sofftime3 = (String) dataSnapshot.child("port3").child("offtime").getValue();

                    sd4 = (String) dataSnapshot.child("port4").child("days").getValue();
                    sontime4 = (String) dataSnapshot.child("port4").child("ontime").getValue();
                    sofftime4 = (String) dataSnapshot.child("port4").child("offtime").getValue();


                    sd1.replace("[", "");
                    sd1.replace("]", "");

                    sd2.replace("[", "");
                    sd2.replace("]", "");

                    sd3.replace("[", "");
                    sd3.replace("]", "");

                    sd4.replace("[", "");
                    sd4.replace("]", "");

                    pa1 = new ArrayList<Integer>();

                    if (sd1.contains("Sunday")) {
                        pa1.add(1);
                    }
                    if (sd1.contains("Monday")) {
                        pa1.add(2);
                    }
                    if (sd1.contains("Tuesday")) {
                        pa1.add(3);
                    }
                    if (sd1.contains("Wednesday")) {
                        pa1.add(4);
                    }
                    if (sd1.contains("Thursday")) {
                        pa1.add(5);
                    }
                    if (sd1.contains("Friday")) {
                        pa1.add(6);
                    }
                    if (sd1.contains("Saturday")) {
                        pa1.add(7);
                    }

                    pw1.setSelectedDays(pa1);
                    startTimeP1.setText(sontime1);
                    stopTimeP1.setText(sofftime1);

                    pa2 = new ArrayList<Integer>();
                    if (sd2.contains("Sunday")) {
                        pa2.add(1);
                    }
                    if (sd2.contains("Monday")) {
                        pa2.add(2);
                    }
                    if (sd2.contains("Tuesday")) {
                        pa2.add(3);
                    }
                    if (sd2.contains("Wednesday")) {
                        pa2.add(4);
                    }
                    if (sd2.contains("Thursday")) {
                        pa2.add(5);
                    }
                    if (sd2.contains("Friday")) {
                        pa2.add(6);
                    }
                    if (sd2.contains("Saturday")) {
                        pa2.add(7);
                    }

                    pw2.setSelectedDays(pa2);
                    startTimeP2.setText(sontime2);
                    stopTimeP2.setText(sofftime2);

                    pa3 = new ArrayList<Integer>();
                    if (sd3.contains("Sunday")) {
                        pa3.add(1);
                    }
                    if (sd3.contains("Monday")) {
                        pa3.add(2);
                    }
                    if (sd3.contains("Tuesday")) {
                        pa3.add(3);
                    }
                    if (sd3.contains("Wednesday")) {
                        pa3.add(4);
                    }
                    if (sd3.contains("Thursday")) {
                        pa3.add(5);
                    }
                    if (sd3.contains("Friday")) {
                        pa3.add(6);
                    }
                    if (sd3.contains("Saturday")) {
                        pa3.add(7);
                    }

                    pw3.setSelectedDays(pa3);
                    startTimeP3.setText(sontime3);
                    stopTimeP3.setText(sofftime3);

                    pa4 = new ArrayList<Integer>();
                    if (sd4.contains("Sunday")) {
                        pa4.add(1);
                    }
                    if (sd4.contains("Monday")) {
                        pa4.add(2);
                    }
                    if (sd4.contains("Tuesday")) {
                        pa4.add(3);
                    }
                    if (sd4.contains("Wednesday")) {
                        pa4.add(4);
                    }
                    if (sd4.contains("Thursday")) {
                        pa4.add(5);
                    }
                    if (sd4.contains("Friday")) {
                        pa4.add(6);
                    }
                    if (sd4.contains("Saturday")) {
                        pa4.add(7);
                    }

                    pw4.setSelectedDays(pa4);
                    startTimeP4.setText(sontime4);
                    stopTimeP4.setText(sofftime4);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });


        finalButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();

                database.child("AutomaticOnOff/port1").addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            HashMap<String,String> mapBt1=new HashMap<String,String>();

                            if (selectedDays1 != null) {
                                mapBt1.put("days",selectedDays1);
                            } else {
                                mapBt1.put("days","NA");
                            }
                            mapBt1.put("ontime",startTimeP1.getText().toString());
                            mapBt1.put("offtime",stopTimeP1.getText().toString());

                            dataSnapshot.getRef().setValue(mapBt1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    DynamicToast.makeSuccess(AutoOnOffActivity.this, "Updated Successfully!", 10).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    DynamicToast.makeError(AutoOnOffActivity.this, "Error Occured!", 10).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("User", databaseError.getMessage());
                    }
                });
            }
        });


        finalButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();

                database.child("AutomaticOnOff/port2").addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()) {

                            HashMap<String,String> mapBt2=new HashMap<String,String>();

                            if (selectedDays2 != null) {
                                mapBt2.put("days",selectedDays2);
                            } else {
                                mapBt2.put("days","NA");
                            }
                            mapBt2.put("ontime",startTimeP2.getText().toString());
                            mapBt2.put("offtime",stopTimeP2.getText().toString());

                            dataSnapshot.getRef().setValue(mapBt2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    DynamicToast.makeSuccess(AutoOnOffActivity.this, "Updated Successfully!", 10).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    DynamicToast.makeError(AutoOnOffActivity.this, "Error Occured!", 10).show();
                                }
                            });
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("User", databaseError.getMessage());
                    }
                });
            }
        });


        finalButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();

                database.child("AutomaticOnOff/port3").addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override

                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            HashMap<String,String> mapBt3=new HashMap<String,String>();

                            if (selectedDays3 != null) {
                                mapBt3.put("days",selectedDays3);
                            } else {
                                mapBt3.put("days","NA");
                            }
                            mapBt3.put("ontime",startTimeP3.getText().toString());
                            mapBt3.put("offtime",stopTimeP3.getText().toString());

                            dataSnapshot.getRef().setValue(mapBt3).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    DynamicToast.makeSuccess(AutoOnOffActivity.this, "Updated Successfully!", 10).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    DynamicToast.makeError(AutoOnOffActivity.this, "Error Occured!", 10).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("User", databaseError.getMessage());
                    }
                });
            }
        });


        finalButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();

                database.child("AutomaticOnOff/port4").addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()) {
                            HashMap<String,String> mapBt4=new HashMap<String,String>();

                            if (selectedDays4 != null) {
                                mapBt4.put("days",selectedDays4);
                            } else {
                                mapBt4.put("days","NA");
                            }
                            mapBt4.put("ontime",startTimeP4.getText().toString());
                            mapBt4.put("offtime",stopTimeP4.getText().toString());

                            dataSnapshot.getRef().setValue(mapBt4).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    DynamicToast.makeSuccess(AutoOnOffActivity.this, "Updated Successfully!", 10).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    DynamicToast.makeError(AutoOnOffActivity.this, "Error Occured!", 10).show();
                                }
                            });                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("User", databaseError.getMessage());
                    }
                });
            }
        });

    }

    private static String utilTime(int value) {

        if (value < 10)
            return "0" + String.valueOf(value);
        else
            return String.valueOf(value);
    }
}