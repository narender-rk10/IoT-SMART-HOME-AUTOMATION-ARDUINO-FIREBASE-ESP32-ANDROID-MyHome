package com.example.myhome.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.airbnb.lottie.LottieAnimationView;
import com.example.myhome.R;
import com.example.myhome.miscellaneous.LottieDialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    private EditText name, phone, email, password;
    private Button signup, signin;
    private MediaPlayer mp;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;
    private DatabaseReference users;

    LottieAnimationView lottieAnimationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }

        mp = MediaPlayer.create(RegistrationActivity.this, R.raw.buttonsound);

        name = findViewById(R.id.full_name);
        email =  findViewById(R.id.reg_email);
        phone =  findViewById(R.id.phone);
        password = findViewById(R.id.reg_password);
        signin = findViewById(R.id.loginbtn);
        signup = findViewById(R.id.create_acc);

        firebaseAuth = FirebaseAuth.getInstance();

        password.setHint("Password");

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    password.setHint("");
                else
                    password.setHint("Password");
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mp.start();
                final String inputName = name.getText().toString().trim();
                final String inputPw = password.getText().toString().trim();
                final String inputEmail = email.getText().toString().trim();
                final String inputPhone = phone.getText().toString().trim();

                if(validateInput(inputName, inputPhone, inputEmail,inputPw))
                    registerUser(inputName, inputPhone, inputEmail,inputPw);

            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            }
        });

    }


    private void registerUser(final String inputName, final String inputPhone, final String inputEmail, final String inputPw) {

      showProgressDialog();

        firebaseAuth.createUserWithEmailAndPassword(inputEmail,inputPw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                                firebaseUser = firebaseAuth.getCurrentUser();
                                String uuid=firebaseUser.getUid();
                                sendUserData(inputName,inputEmail,inputPhone,inputPw);
                                sendVerificationEmail();
                            }else{
                                DynamicToast.makeError(RegistrationActivity.this, task.getException().getMessage(), 10).show();
                                startActivity(new Intent(RegistrationActivity.this, RegistrationActivity.class));
                                finish();
                            }}
                    });
            }

    private void sendUserData(String name, String emailid, String phone, String password){

        firebaseDatabase = FirebaseDatabase.getInstance();
        users = firebaseDatabase.getReference("users");

        Map<String, String> userData = new HashMap<String, String>();
        userData.put("name", name);
        userData.put("phone", phone);
        userData.put("password",password);
        userData.put("email",emailid);

        Map<String, String> aof = new HashMap<String, String>();
        aof.put("days", "NA");
        aof.put("ontime", "00:00");
        aof.put("offtime", "00:00");

        Map<String, Integer> airdb = new HashMap<String, Integer>();
        airdb.put("temp", 20);
        airdb.put("humidity", 20);
        airdb.put("mq135q", 1200);

        Map<String, String> componentsdb = new HashMap<String, String>();
        componentsdb.put("port1", "OFF");
        componentsdb.put("port2", "OFF");
        componentsdb.put("port3", "OFF");
        componentsdb.put("port4", "OFF");

        Map<String, Double> elecchargesdb = new HashMap<String,Double>();
        elecchargesdb.put("0-100",3.6 );
        elecchargesdb.put("101-300", 6.4);
        elecchargesdb.put("301-500", 7.6);
        elecchargesdb.put("501-1000", 9.6);
        elecchargesdb.put("M1000", 11.56);
        elecchargesdb.put("othercharges", 587.56);

        Map<String, Integer> elecsingleval = new HashMap<String,Integer>();
        elecsingleval.put("frequency", 4);
        elecsingleval.put("voltage", 9);

        users.child(firebaseUser.getUid()).setValue(userData);
        users.child(firebaseUser.getUid()).child("AutomaticOnOff").child("port1").setValue(aof);
        users.child(firebaseUser.getUid()).child("AutomaticOnOff").child("port2").setValue(aof);
        users.child(firebaseUser.getUid()).child("AutomaticOnOff").child("port3").setValue(aof);
        users.child(firebaseUser.getUid()).child("AutomaticOnOff").child("port4").setValue(aof);
        users.child(firebaseUser.getUid()).child("air").setValue(airdb);
        users.child(firebaseUser.getUid()).child("components").setValue(componentsdb);
        users.child(firebaseUser.getUid()).child("electricity").setValue(elecsingleval);
        users.child(firebaseUser.getUid()).child("electricity/pf").setValue(0.6);
        users.child(firebaseUser.getUid()).child("electricity").child("charges").setValue(elecchargesdb);
        users.child(firebaseUser.getUid()).child("plant").child("moisture").setValue(1);
        users.child(firebaseUser.getUid()).child("fire").child("fireleak").setValue(1);
        users.child(firebaseUser.getUid()).child("gas").child("gasleak").setValue(0);
        users.child(firebaseUser.getUid()).child("notification").child("nid").setValue("nid");
        users.child(firebaseUser.getUid()).child("water").child("distance").setValue(14);
        users.child(firebaseUser.getUid()).child("water").child("level").setValue(12);
    }

    private void sendVerificationEmail()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseAuth.getInstance().signOut();
                            DynamicToast.makeSuccess(RegistrationActivity.this, "Check Email For Verification.", 10).show();
                            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                            finish();
                        }
                        else
                        {
                            FirebaseAuth.getInstance().signOut();
                            DynamicToast.makeError(RegistrationActivity.this, "Unable to Send Mail", 10).show();
                            startActivity(new Intent(RegistrationActivity.this, RegistrationActivity.class));
                            finish();
                        }
                    }
                });
    }

    private boolean validateInput(String inName, String inPw, String inPhone, String inEmail){

        if(inName.isEmpty()){
            name.setError("Name is empty.");
            return false;
        }
        if(inPw.isEmpty()){
            password.setError("Password is empty.");
            return false;
        }

        if(inPhone.isEmpty()){
            password.setError("Mobile Number is empty.");
            return false;
        }

        if(inEmail.isEmpty()){
            email.setError("Email is empty.");
            return false;
        }

        return true;
    }
    private void showProgressDialog(){
        new LottieDialogFragment().show(getSupportFragmentManager(),"pd");
    }
}