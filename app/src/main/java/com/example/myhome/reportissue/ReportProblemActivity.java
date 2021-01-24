package com.example.myhome.reportissue;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import com.developer.kalert.KAlertDialog;
import com.example.myhome.R;
import com.example.myhome.maincontroller.BottomNavBarActivity;
import com.example.myhome.reportissue.helpers.MailSender;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReportProblemActivity extends AppCompatActivity {
    private SmartMaterialSpinner smartMaterialSpinner;
    private List<String> issuesList;
    private EditText reportData;
    private Button reportBtn;
    private MediaPlayer mp;
    private String issue_question,emailid,nameid,phoneid;
    private FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_problem);

        reportBtn = findViewById(R.id.report_btn);
        reportData = findViewById(R.id.prob_sugg);
        smartMaterialSpinner = findViewById(R.id.spinnerQuestionsOptions);

        database = FirebaseDatabase.getInstance().getReference("users/"+currentFirebaseUser.getUid());
        mp = MediaPlayer.create(getApplicationContext(), R.raw.buttonsound);

        issuesList = new ArrayList<>();
        issuesList.add("App not Working");
        issuesList.add("IoT Device not Working");
        issuesList.add("Others");
        smartMaterialSpinner.setHint("Select One:");
        smartMaterialSpinner.setEnableFloatingLabel(false);
        smartMaterialSpinner.setItem(issuesList);

        smartMaterialSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                issue_question = issuesList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                issue_question = "Others";
            }
        });

        database.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    emailid = (String) map.get("email");
                    nameid = (String) map.get("name");
                    phoneid = (String) map.get("phone");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                if (reportData.getText().toString().trim().isEmpty()) {
                    reportData.setError("This Field can't be empty!");
                }
                else {
                    sendEmailMessage();
                }
            }
        });

    }

    private void sendEmailMessage() {

        Thread sender = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MailSender sender = new MailSender("youremailid@.com", "yourpassword");
                    sender.sendMail("MyHome Issues/Suggestions",
                            "FROM: "+emailid+ "\n NAME:"+nameid+"\n PHONE:"+phoneid+"\n ISSUE TYPE:"+issue_question + "\n CUSTOMER QUERY:[PROBLEM/SUGGESTION]" + reportData.getText().toString(),
                            "youremailid@.com",
                            "youremailid@.com");

                            ReportProblemActivity.this.runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            new KAlertDialog(ReportProblemActivity.this, KAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("ISSUE REPORTED!")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                                        @Override
                                        public void onClick(KAlertDialog sDialog) {
                                            sDialog.dismiss();
                                            startActivity(new Intent(ReportProblemActivity.this,BottomNavBarActivity.class));
                                            finish();
                                        }
                                    })
                                    .show();
                        }

                    });
                } catch (Exception e) {
                    ReportProblemActivity.this.runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            new KAlertDialog(ReportProblemActivity.this, KAlertDialog.ERROR_TYPE)
                                    .setTitleText("ERROR OCCURRED!")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                                        @Override
                                        public void onClick(KAlertDialog sDialog) {
                                            sDialog.dismiss();
                                            startActivity(new Intent(ReportProblemActivity.this,ReportProblemActivity.class));
                                            finish();
                                        }
                                    })
                                    .show();

                        }
                    });
                    Log.e("User", "Error: " + e.getMessage());
                }
            }
        });
        sender.start();
    }
}
