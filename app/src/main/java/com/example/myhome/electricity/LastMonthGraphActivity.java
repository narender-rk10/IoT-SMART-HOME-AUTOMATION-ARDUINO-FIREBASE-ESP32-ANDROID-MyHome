package com.example.myhome.electricity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.myhome.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.LargeValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class LastMonthGraphActivity extends AppCompatActivity {
    private BarChart chart;
    private Calendar c;
    private Integer thisMonth;
    private String inmonth;
    private String months[] = {"January" , "February" , "March" , "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_month_graph);

        chart = (BarChart) findViewById(R.id.chartLastMonth);
        c = Calendar.getInstance(TimeZone.getDefault());
        thisMonth = c.get(Calendar.MONTH) - 1;
        inmonth = months[thisMonth];
        database = FirebaseDatabase.getInstance().getReference("users/"+currentFirebaseUser.getUid());

        database.child("electricity").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    ArrayList arrayList1 = new ArrayList();
                    ArrayList arrayList2 = new ArrayList();
                    ArrayList arrayList3 = new ArrayList();
                    ArrayList dates = new ArrayList();

                    int i = 0;
                    for (DataSnapshot childDataSnapshot : dataSnapshot.child("power").child(inmonth).getChildren()) {
                        if (dataSnapshot != null) {
                            dates.add(childDataSnapshot.getKey());
                            arrayList1.add(new BarEntry(((Double) childDataSnapshot.getValue()).floatValue()/10.0f, i++));
                        } else {
                            dates.add("NA");
                            arrayList1.add(new BarEntry(0.0f, i++));
                        }
                    }

                    int j = 0;
                    for (DataSnapshot childDataSnapshot : dataSnapshot.child("current").child(inmonth).getChildren()) {
                        if (dataSnapshot != null) {
                            arrayList2.add(new BarEntry(((Double) childDataSnapshot.getValue()).floatValue(), j++));
                        } else {
                            arrayList2.add(new BarEntry(0.0f, j++));
                        }
                    }

                    int k = 0;
                    for (DataSnapshot childDataSnapshot : dataSnapshot.child("power").child(inmonth).getChildren()) {
                        if (dataSnapshot != null) {
                            arrayList3.add(new BarEntry(((Double) childDataSnapshot.getValue()).floatValue(), k++));
                        } else {
                            arrayList3.add(new BarEntry(0.0f, k++));
                        }
                    }

                    ArrayList<BarDataSet> dataSets = null;
                    BarDataSet barDataSet1 = new BarDataSet(arrayList1, "ENERGY");
                    barDataSet1.setColor(Color.rgb(117, 48, 255));

                    BarDataSet barDataSet2 = new BarDataSet(arrayList2, "CURRENT");
                    barDataSet2.setColor(Color.rgb(255, 190, 0));

                    BarDataSet barDataSet3 = new BarDataSet(arrayList3, "POWER");
                    barDataSet3.setColor(Color.rgb(255, 105, 105));

                    dataSets = new ArrayList<>();
                    dataSets.add(barDataSet1);
                    dataSets.add(barDataSet2);
                    dataSets.add(barDataSet3);

                    chart.setPinchZoom(true);
                    chart.setScaleEnabled(true);
                    chart.setDrawBarShadow(false);
                    chart.setDrawGridBackground(false);

                    XAxis xAxis = chart.getXAxis();
                    xAxis.setDrawGridLines(false);

                    chart.getAxisRight().setEnabled(false);
                    YAxis leftAxis = chart.getAxisLeft();
                    leftAxis.setValueFormatter(new LargeValueFormatter());
                    leftAxis.setDrawGridLines(true);

                    BarData data = new BarData(dates, dataSets);
                    chart.setData(data);
                    data.setValueTextColor(Color.rgb(242, 251, 255));
                    data.setValueTextSize(16);
                    chart.animateXY(2000, 2000);
                    chart.setHorizontalScrollBarEnabled(true);
                    chart.setBackgroundColor(Color.rgb(16, 14, 59));
                    chart.setVisibleXRange(12);
                    chart.getAxisLeft().setDrawGridLines(false);
                    chart.getXAxis().setDrawGridLines(false);
                    chart.getXAxis().setTextColor(Color.rgb(242, 251, 255));
                    chart.getXAxis().setTextSize(16);
                    chart.getAxisLeft().setTextColor(Color.rgb(242, 251, 255));
                    chart.getAxisRight().setTextColor(Color.rgb(242, 251, 255));
                    chart.getLegend().setTextColor(Color.rgb(242, 251, 255));
                    chart.getAxisLeft().setTextSize(16);
                    chart.getLegend().setTextSize(16);
                    chart.getAxisRight().setTextSize(16);


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        chart.getXAxis().setTypeface(getResources().getFont(R.font.mazzardhlight));
                        chart.getLegend().setTypeface(getResources().getFont(R.font.mazzardhlight));
                        chart.getAxisLeft().setTypeface(getResources().getFont(R.font.mazzardhlight));
                        chart.getAxisRight().setTypeface(getResources().getFont(R.font.mazzardhlight));

                    }

                    chart.setDrawGridBackground(false);
                    chart.invalidate();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });
    }
}
