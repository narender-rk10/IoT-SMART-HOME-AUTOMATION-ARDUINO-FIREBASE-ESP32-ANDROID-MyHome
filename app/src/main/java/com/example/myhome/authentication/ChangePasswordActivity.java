package com.example.myhome.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myhome.R;
import com.example.myhome.maincontroller.BottomNavBarActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

public class ChangePasswordActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private EditText oldpass,newpass;
    private Button cp;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mp = MediaPlayer.create(getApplicationContext(), R.raw.buttonsound);
        firebaseAuth = FirebaseAuth.getInstance();
        oldpass = findViewById(R.id.old_pass);
        newpass = findViewById(R.id.new_pass);
        cp = findViewById(R.id.cpbtn);

        oldpass.setHint("Current Password");
        newpass.setHint("New Password");

        oldpass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    oldpass.setHint("");
                else
                    oldpass.setHint("Current Password");
            }
        });


        newpass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    newpass.setHint("");
                else
                    newpass.setHint("New Password");
            }
        });

        cp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mp.start();

                if(TextUtils.isEmpty(oldpass.getText().toString())){

                    oldpass.setError("This Field Can't be Empty");
                }

                if(TextUtils.isEmpty(newpass.getText().toString())){

                    newpass.setError("This Field Can't be Empty");
                }

                if(!TextUtils.isEmpty(oldpass.getText().toString().trim()) && !TextUtils.isEmpty(newpass.getText().toString().trim())) {
                    firebaseAuth.getCurrentUser().updatePassword(newpass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                DynamicToast.makeSuccess(ChangePasswordActivity.this, "Password Updated Successfully", 10).show();
                                firebaseAuth.signOut();
                                startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                            } else {
                                DynamicToast.makeError(ChangePasswordActivity.this, task.getException().getMessage(), 10).show();
                            }
                        }
                    });
                }
            }
        });
    }
}