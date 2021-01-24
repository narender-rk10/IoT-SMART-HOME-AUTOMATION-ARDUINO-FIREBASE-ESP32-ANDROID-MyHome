package com.example.myhome.authentication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myhome.R;
import com.example.myhome.maincontroller.BottomNavBarActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.shreyaspatil.MaterialDialog.MaterialDialog;

import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    private EditText ename,eemail;
    private Button btnEdit;
    private String n,e;
    private MediaPlayer mp;
    private FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ename=findViewById(R.id.ed_name);
        eemail=findViewById(R.id.ed_email);
        btnEdit=findViewById(R.id.BEDemail);

        database = FirebaseDatabase.getInstance().getReference("users/"+currentFirebaseUser.getUid());
        mp = MediaPlayer.create(getApplicationContext(), R.raw.buttonsound);

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();

                    n =  map.get("name");
                    e =  map.get("email");
                    ename.setText(n);
                    eemail.setText(e);

                }
                else{
                    ename.setText("NA");
                    eemail.setText("NA");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();


            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();

                if (!ename.getText().toString().equals(n)) {
                    database.child("name").addValueEventListener(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {
                                if (validateInputName(ename.getText().toString().trim())) {
                                    dataSnapshot.getRef().setValue(ename.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            DynamicToast.makeSuccess(EditProfileActivity.this, "Name Updated Successfully!", 10).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            DynamicToast.makeError(EditProfileActivity.this, "Error Occured!", 10).show();
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

                if (validateInputEmail(eemail.getText().toString().trim())) {
                    if (!eemail.getText().toString().equals(e)) {
                        MaterialDialog mDialog = new MaterialDialog.Builder(EditProfileActivity.this)
                                .setTitle("Update Email")
                                .setMessage("Do you really want to edit this email, after updating email you will be logout and verification mail will be send to you?")
                                .setCancelable(false)
                                .setPositiveButton("Update Email", R.mipmap.logout, new MaterialDialog.OnClickListener() {
                                    @Override
                                    public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                        mp.start();
                                        currentFirebaseUser.updateEmail(eemail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    currentFirebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                database.addValueEventListener(new ValueEventListener() {
                                                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                                                    @Override
                                                                    public void onDataChange(final DataSnapshot dataSnapshot) {
                                                                        if (dataSnapshot.exists()) {
                                                                            dataSnapshot.getRef().child("email").setValue(eemail.getText().toString().trim());
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {
                                                                        Log.d("User", databaseError.getMessage());
                                                                    }
                                                                });
                                                                FirebaseAuth.getInstance().signOut();
                                                                DynamicToast.makeSuccess(EditProfileActivity.this, "Check Email For Verification.", 10).show();
                                                                startActivity(new Intent(EditProfileActivity.this, LoginActivity.class));
                                                                finish();
                                                            } else {
                                                                DynamicToast.makeError(EditProfileActivity.this, "Unable to Send Mail", 10).show();
                                                                startActivity(new Intent(EditProfileActivity.this, EditProfileActivity.class));
                                                                finish();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    DynamicToast.makeError(EditProfileActivity.this, "Error Occured!", 10).show();
                                                }
                                            }
                                        });

                                        dialogInterface.dismiss();
                                    }

                                })
                                .setNegativeButton("Cancel", R.mipmap.close, new MaterialDialog.OnClickListener() {
                                    @Override
                                    public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                        DynamicToast.makeError(getApplicationContext(), "CANCELLED", 10).show();
                                        mp.start();
                                        dialogInterface.dismiss();
                                    }
                                })
                                .build();

                        mDialog.show();
                    }
                }
            }
        });
    }

    public boolean validateInputName(String a){

        if(a.isEmpty()){
            ename.setError("This field can't be empty");
            return false;
        }
        return true;
    }

    public boolean validateInputEmail(String a){

        if(a.isEmpty()){
            eemail.setError("This field can't be empty");
            return false;
        }
        return true;
    }

}