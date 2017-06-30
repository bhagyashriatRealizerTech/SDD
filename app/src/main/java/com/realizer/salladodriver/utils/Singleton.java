package com.realizer.salladodriver.utils;

import android.content.Intent;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Win on 16-06-2017.
 */

public class Singleton {
    public static Singleton instance = null;
    public static boolean isChange = false;
    public static FirebaseDatabase database = null;
    public static Intent autoserviceIntent = null;

    private Singleton(){

    }

    public static Singleton getInstance(){

        if(instance == null){
            instance = new Singleton();
        }

        return instance;
    }

    public static boolean isChange() {
        return isChange;
    }

    public static void setIsChange(boolean isChange) {
        Singleton.isChange = isChange;
    }


    public static FirebaseDatabase getDatabase() {
        return database;
    }

    public static void setDatabase(FirebaseDatabase database) {
        boolean flag = false;
        if(Singleton.database == null) {
            flag = true;
            Singleton.database = database;
        }
        if(flag)
        Singleton.database.setPersistenceEnabled(true);
    }

    public static Intent getAutoserviceIntent() {
        return autoserviceIntent;
    }

    public static void setAutoserviceIntent(Intent autoserviceIntent) {
        Singleton.autoserviceIntent = autoserviceIntent;
    }
}
