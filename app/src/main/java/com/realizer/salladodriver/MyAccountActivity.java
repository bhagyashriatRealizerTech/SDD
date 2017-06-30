package com.realizer.salladodriver;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.realizer.salladodriver.databasemodel.Driver;
import com.realizer.salladodriver.utils.Constants;
import com.realizer.salladodriver.utils.Singleton;
import com.realizer.salladodriver.view.ProgressWheel;

import static com.realizer.salladodriver.R.styleable.ProgressWheel;

public class MyAccountActivity extends AppCompatActivity {

    EditText userName,userLicNo,userMobile,userAddress,userBday;

    ImageView userImage;
    SharedPreferences preferences;
    com.realizer.salladodriver.view.ProgressWheel loading;
    FirebaseDatabase database;
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_account_activity);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Constants.actionBarTitle("My Account", this));

        preferences  = PreferenceManager.getDefaultSharedPreferences(MyAccountActivity.this);

        if(Singleton.getInstance().getDatabase() == null)
            Singleton.getInstance().setDatabase(FirebaseDatabase.getInstance());
        database = Singleton.getDatabase();
        myRef = database.getReference("Driver");
        myRef.keepSynced(true);

        initiateView();
        setValue();

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setValue(){
        Query query = myRef.child(preferences.getString("UserID",""));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                        // do something with the individual "issues"
                        SharedPreferences.Editor edit = preferences.edit();
                        Driver user  = dataSnapshot.getValue(Driver.class);
                        edit.putString("UserName", user.getDriverName());
                        edit.putString("MobNo", user.getDriverMobileNo());
                        edit.putString("UserID",dataSnapshot.getKey());
                        edit.putString("IsLogin","true");
                        edit.apply();

                        loading.setVisibility(View.GONE);

                        userName.setText("Name        : "+user.getDriverName());
                        userLicNo.setText("Licence No : "+user.getLicenceNo());
                        userBday.setText("Birthdate   : "+user.getBirthDate());
                        userMobile.setText("Mobile No : "+user.getDriverMobileNo());
                        userAddress.setText("Address  : "+user.getAddress());
                    if(userAddress.getText().toString().length()<=0)
                        userAddress.setText("Address Not Available");

                    enableDisable(false);

                }
                else {
                    loading.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                loading.setVisibility(View.GONE);
            }
        });

    }

    public void initiateView(){

        userName = (EditText) findViewById(R.id.txt_username);
        userBday = (EditText) findViewById(R.id.txt_birthday);
        userMobile = (EditText) findViewById(R.id.txt_usermobile);
        userAddress = (EditText) findViewById(R.id.txtAddress);
        userLicNo = (EditText) findViewById(R.id.txt_licno);
        userImage = (ImageView) findViewById(R.id.img_user);
        loading = (ProgressWheel) findViewById(R.id.loading);

    }

    public void enableDisable(boolean value){
        userName.setEnabled(value);
        userBday.setEnabled(value);
        userMobile.setEnabled(value);
        userAddress.setEnabled(value);


        if(value){
            userName.setBackgroundResource(R.drawable.dashboard_icon_background);
            userBday.setBackgroundResource(R.drawable.dashboard_icon_background);
            userMobile.setBackgroundResource(R.drawable.dashboard_icon_background);
            userAddress.setBackgroundResource(R.drawable.dashboard_icon_background);
        }
        else {
            userName.setTextColor(Color.BLACK);
            userBday.setTextColor(Color.BLACK);
            userMobile.setTextColor(Color.BLACK);
            userAddress.setTextColor(Color.BLACK);

            userName.setBackgroundColor(Color.TRANSPARENT);
            userBday.setBackgroundColor(Color.TRANSPARENT);
            userMobile.setBackgroundColor(Color.TRANSPARENT);
            userAddress.setBackgroundColor(Color.TRANSPARENT);
        }
    }
}
