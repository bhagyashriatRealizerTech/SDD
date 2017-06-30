package com.realizer.salladodriver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.PropertyName;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.realizer.salladodriver.databasemodel.Driver;
import com.realizer.salladodriver.databasemodel.UserDietDelivery;
import com.realizer.salladodriver.utils.Constants;
import com.realizer.salladodriver.utils.Singleton;
import com.realizer.salladodriver.view.ProgressWheel;

import java.util.Date;


public class LoginActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference driverRef;
    EditText username,password;
    Button login;
    ProgressWheel loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        getSupportActionBar().hide();
        initiateView();

        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission())
            {
                requestForSpecificPermission();
            }
        }

        if(Singleton.getInstance().getDatabase() == null)
            Singleton.getInstance().setDatabase(FirebaseDatabase.getInstance());

        database = Singleton.getInstance().getDatabase();
        driverRef = database.getReference("Driver");
        driverRef.keepSynced(true);


    }

    public void initiateView(){

        username = (EditText) findViewById(R.id.edt_username);
        password = (EditText) findViewById(R.id.edt_password);
        login = (Button) findViewById(R.id.btn_Submit);
        loading = (ProgressWheel) findViewById(R.id.loading);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.getText().toString().trim().length()<=0){
                    Constants.alertDialog(LoginActivity.this,"Login","User Id can not be Empty.\nPlease Enter User Id.");
                }
                else if(password.getText().toString().trim().length()<=0){
                    Constants.alertDialog(LoginActivity.this,"Login","Password can not be Empty.\nPlease Enter Password.");
                }
                else {
                    loading.setVisibility(View.VISIBLE);

                    Query query = driverRef.orderByChild("driverId").equalTo(username.getText().toString().trim());
                    //Query query = driverRef.child("-Kncv_d2I9wiMUMb8MZh");
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                for (DataSnapshot childData : dataSnapshot.getChildren()) {
                                    Driver driver = childData.getValue(Driver.class);
                                    if (driver.getPassword().equals(password.getText().toString().trim())) {
                                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                        SharedPreferences.Editor edit = preferences.edit();
                                        edit.putString("UserName", driver.getDriverName());
                                        edit.putString("MobNo", driver.getDriverMobileNo());
                                        edit.putString("UserID", childData.getKey());
                                        edit.putString("IsLogin", "true");
                                        edit.apply();
                                        loading.setVisibility(View.GONE);
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        loading.setVisibility(View.GONE);
                                        Constants.alertDialog(LoginActivity.this, "Login", "Invalid Credentials.\nPlease Enter Valid Credentials.");
                                    }
                                }
                            }
                            else {
                                loading.setVisibility(View.GONE);
                                Constants.alertDialog(LoginActivity.this,"Login","Invalid Credentials.\nPlease Enter Valid Credentials.");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        });
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]
                {
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.READ_PHONE_STATE,
                        android.Manifest.permission.WAKE_LOCK,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.ACCESS_NETWORK_STATE,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION

                }, 101);
    }

}
