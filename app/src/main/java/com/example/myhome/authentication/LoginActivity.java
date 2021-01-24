package com.example.myhome.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myhome.maincontroller.BottomNavBarActivity;
import com.example.myhome.R;
import com.example.myhome.miscellaneous.LottieDialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;


public class LoginActivity extends AppCompatActivity  {

    private EditText email, password;
    private TextView forgotPass;
    private Button btnLogin;//,btnSignup;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }

        mp = MediaPlayer.create(LoginActivity.this, R.raw.buttonsound);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        btnLogin=findViewById(R.id.loginbtn);
        forgotPass=findViewById(R.id.fp);
//        btnSignup=findViewById(R.id.create_acc);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        if(user != null) {
            finish();
            startActivity(new Intent(LoginActivity.this, BottomNavBarActivity.class));
        }

        password.setHint("Password");

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    password.setHint("");
                else
                    password.setHint("Password");
            }
        });

       btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mp.start();
                String inEmail = email.getText().toString().trim();
                String inPassword = password.getText().toString().trim();

                if(validateInput(inEmail, inPassword)){
                    signUser(inEmail, inPassword);
                }

            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                startActivity(new Intent(LoginActivity.this, PasswordResetActivity.class));
            }
        });

//        btnSignup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mp.start();
//                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
//            }
//        });

    }

    public void signUser(final String email, final String password){

        showProgressDialog();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user.isEmailVerified()) {
                        DynamicToast.makeSuccess(LoginActivity.this, "Redirecting to OTP!", 10).show();
                        FirebaseAuth.getInstance().signOut();
                        Intent sendToOtp =  new Intent(LoginActivity.this, OtpVerificationActivity.class);
                        sendToOtp.putExtra("emailfromlogin",email);
                        sendToOtp.putExtra("pwdfromlogin",password);
                        startActivity(sendToOtp);
                        finish();
                    }
                    else {
                        FirebaseAuth.getInstance().signOut();
                        DynamicToast.makeWarning(LoginActivity.this, "Please Verify Your Email", 10).show();
                        startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                        finish();
                    }
                }
                else{
                    DynamicToast.makeError(LoginActivity.this, "Invalid Email or Password", 10).show();
                    startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });

    }


    public boolean validateInput(String inemail, String inpassword){

        if(inemail.isEmpty()){
            email.setError("Email field is empty.");
            return false;
        }
        if(inpassword.isEmpty()){
            password.setError("Password is empty.");
            return false;
        }
        return true;
    }
    private void showProgressDialog(){
        new LottieDialogFragment().show(getSupportFragmentManager(),"pd");
    }
    }