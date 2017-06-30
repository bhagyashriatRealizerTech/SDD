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
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.realizer.salladodriver.adapter.MyOrderListAdapter;
import com.realizer.salladodriver.databasemodel.UserDietDelivery;
import com.realizer.salladodriver.utils.Constants;
import com.realizer.salladodriver.utils.Singleton;
import com.realizer.salladodriver.view.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bhagyashri on 9/27/2016.
 */
public class MyOrderActivity extends AppCompatActivity {

    ListView orderList;
    List<UserDietDelivery> userDietDeliveryList;
    FirebaseDatabase database;
    DatabaseReference userDietDelRef;
    ProgressWheel loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_order_activity);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Constants.actionBarTitle("Order List", this));

        if(Singleton.getInstance().getDatabase() == null)
            Singleton.getInstance().setDatabase(FirebaseDatabase.getInstance());

        database = Singleton.getInstance().getDatabase();
        userDietDelRef = database.getReference("UserDietDelivery");
        userDietDelRef.keepSynced(true);

        initiateView();
        getOrders();
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

    public void getOrders(){
        loading.setVisibility(View.VISIBLE);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyOrderActivity.this);
        String driverId = preferences.getString("UserID","");

        Query query = userDietDelRef.orderByChild("indexkeyDriver").equalTo(driverId+"_"+Constants.getCurrentDateTime());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userDietDeliveryList.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        UserDietDelivery userDietDelivery = snapshot.getValue(UserDietDelivery.class);
                        userDietDeliveryList.add(userDietDelivery);
                    }
                    if(userDietDeliveryList.size()>0){
                        MyOrderListAdapter myOrderListAdapter = new MyOrderListAdapter(userDietDeliveryList,MyOrderActivity.this);
                        orderList.setAdapter(myOrderListAdapter);
                    }
                    loading.setVisibility(View.GONE);
                }
                else {
                    loading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void initiateView()
    {
        orderList = (ListView) findViewById(R.id.myorderlist);
        userDietDeliveryList = new ArrayList<>();
        loading = (ProgressWheel) findViewById(R.id.loading);
    }

}
