package com.example.myhome.miscellaneous;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.myhome.maincontroller.BottomNavBarActivity;
import com.example.myhome.R;
import com.example.myhome.authentication.LoginActivity;
import com.example.myhome.onboarding.OnBoardingActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;


public class SplashScreenActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = firebaseAuth.getCurrentUser();
    private FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
    private DatabaseReference database;
    private static final int LOCK_REQUEST_CODE = 221;
    private static final int SECURITY_SETTING_REQUEST_CODE = 233;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }

        if(currentFirebaseUser!=null) {
            database = FirebaseDatabase.getInstance().getReference("users/" + currentFirebaseUser.getUid());

            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    final String newToken = instanceIdResult.getToken();
                    Log.e("newToken", newToken);

                    database.child("notification").addValueEventListener(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                dataSnapshot.getRef().child("nid").setValue(newToken);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("User", databaseError.getMessage());
                        }
                    });
                    SplashScreenActivity.this.getPreferences(Context.MODE_PRIVATE).edit().putString("fb", newToken).apply();
                }
            });

            Log.d("newToken", this.getPreferences(Context.MODE_PRIVATE).getString("fb", "empty :("));
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                SharedPreferences preferences = getSharedPreferences("my_preferences", MODE_PRIVATE);

                if(!preferences.getBoolean("onboarding_complete",false)){
                    Intent i=new Intent(SplashScreenActivity.this, OnBoardingActivity.class);
                    startActivity(i);
                    finish();
                    return;
                }

                    KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
                    Intent j = keyguardManager.createConfirmDeviceCredentialIntent("title", "description");

                    if(user!=null)
                    {
                        SharedPreferences AuthDetails= getSharedPreferences("finger_authpref", 0);
                        String fingerauth = AuthDetails.getString("finger_auth", "false");

                        if(fingerauth.equals("true")){
                            authenticateApp();
                        }
                        else{
                            Intent i=new Intent(SplashScreenActivity.this, BottomNavBarActivity.class);
                            startActivity(i);
                            finish();
                        }

                    }
                    else
                    {
                        Intent i =new Intent(SplashScreenActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();

                    }

            }
        }, 5000);
    }

        private void authenticateApp() {
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Intent i = keyguardManager.createConfirmDeviceCredentialIntent(getResources().getString(R.string.unlock), getResources().getString(R.string.confirm_pattern));
                try {
                    startActivityForResult(i, LOCK_REQUEST_CODE);
                } catch (Exception e) {
                    Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                    try {
                        startActivityForResult(intent, SECURITY_SETTING_REQUEST_CODE);
                    } catch (Exception ex) {
                        DynamicToast.makeError(SplashScreenActivity.this, getResources().getString(R.string.unlock_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode) {
                case LOCK_REQUEST_CODE:
                    if (resultCode == RESULT_OK) {
                        Intent i=new Intent(SplashScreenActivity.this, BottomNavBarActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        DynamicToast.makeError(SplashScreenActivity.this, getResources().getString(R.string.unlock_failed), Toast.LENGTH_SHORT).show();
                        authenticateApp();
                    }
                    break;
                case SECURITY_SETTING_REQUEST_CODE:
                    if (isDeviceSecure()) {
                        authenticateApp();
                    } else {
                        DynamicToast.makeError(SplashScreenActivity.this, getResources().getString(R.string.security_device_cancelled), Toast.LENGTH_SHORT).show();
                        authenticateApp();
                    }

                    break;
            }
        }


        private boolean isDeviceSecure() {
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && keyguardManager.isKeyguardSecure();
        }
}