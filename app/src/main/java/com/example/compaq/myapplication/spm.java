package com.example.compaq.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by compaq on 3/25/2017.
 */

public class spm {

    private static final String SHARED_PREF_NAME="fcmsharedprefdemo";
    private static final String KEY_ACCESS_TOKEN="token";
    private static Context mCtx;
    private static spm mInstance;
    private spm(Context context)
    {
        mCtx=context;
    }
    public static synchronized spm getInstance(Context context)
    {
        if(mInstance==null)
            mInstance=new spm(context);
        return mInstance;
    }
    public boolean saveDeviceToken(String token)
    {
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN,token);
        editor.apply();
        return true;
    }
    public String getToken()
    {
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ACCESS_TOKEN,null);
    }
}
