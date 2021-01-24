package com.example.myhome.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.myhome.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

public class PasswordResetActivity extends AppCompatActivity {

    private EditText resetEmail;
    private Button forgo, logit; //create
    private FirebaseAuth firebaseAuth;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }

        mp = MediaPlayer.create(PasswordResetActivity.this, R.raw.buttonsound);

        firebaseAuth = FirebaseAuth.getInstance();
        resetEmail = findViewById(R.id.reset_pwd);
        forgo = findViewById(R.id.reset_acc);
//        create = findViewById(R.id.register_back);
        logit = findViewById(R.id.login_back);

        forgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mp.start();
                String email = resetEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    resetEmail.setError("Please, fill the email field.",null);
                }
                else {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                DynamicToast.makeSuccess(PasswordResetActivity.this, "Email has been sent successfully.", 10).show();
                                finish();
                                startActivity(new Intent(PasswordResetActivity.this, LoginActivity.class));
                            } else {
                                DynamicToast.makeError(PasswordResetActivity.this, "Invalid Email Address.", 10).show();
                            }
                        }
                    });

                }

            }
        });

        logit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                startActivity(new Intent(PasswordResetActivity.this,LoginActivity.class));
            }
        });

//        create.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mp.start();
//                startActivity(new Intent(PasswordResetActivity.this, RegistrationActivity.class));
//            }
//        });

    }

}