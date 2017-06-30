package com.realizer.salladodriver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.realizer.salladodriver.utils.Constants;
import com.realizer.salladodriver.utils.Singleton;

/**
 * Created by Bhagyashri on 9/27/2016.
 */
public class MainActivity extends AppCompatActivity {

    TextView profile,map,list;
    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    ImageButton start_stop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Constants.actionBarTitle("Order Track", this));
        initiateView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void initiateView()
    {
        profile = (TextView) findViewById(R.id.txtProfile);
        map = (TextView) findViewById(R.id.txtMap);
        list = (TextView)findViewById(R.id.txtList);
        start_stop = (ImageButton) findViewById(R.id.iv_button_start_stop);
        Typeface face= Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/font.ttf");
        profile.setTypeface(face);
        map.setTypeface(face);
        list.setTypeface(face);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        getSupportActionBar().setTitle(Constants.actionBarTitle("Track Order", MainActivity.this));

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyAccountActivity.class);
                startActivity(intent);
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyCurrentLocationActivity.class);
                startActivity(intent);
            }
        });


        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyOrderActivity.class);
                startActivity(intent);
            }
        });

        start_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor edit = sharedpreferences.edit();
                if(!sharedpreferences.getBoolean("IsStart",false))
                {
                    start_stop.setImageResource(R.drawable.stop_button);
                    edit.putBoolean("IsStart", true);
                    edit.commit();
                    Intent intent = new Intent(MainActivity.this,ServiceLocationChange.class);
                    Singleton.getInstance().setAutoserviceIntent(intent);
                    startService(intent);
                }
                else
                {
                    start_stop.setImageResource(R.drawable.start_button);
                    edit.putBoolean("IsStart",false);
                    edit.commit();
                    if(Singleton.getInstance().getAutoserviceIntent() != null)
                        stopService(Singleton.getInstance().getAutoserviceIntent());
                }
            }
        });
    }
}
