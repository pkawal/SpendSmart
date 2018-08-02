package com.example.compaq.myapplication;

import com.firebase.client.Firebase;

import android.app.Application;
/**
 * Created by compaq on 3/7/2017.
 */

public class SpendSmart2  extends Application{
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);

    }

}
