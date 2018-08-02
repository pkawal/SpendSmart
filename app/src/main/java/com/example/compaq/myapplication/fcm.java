package com.example.compaq.myapplication;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by compaq on 3/25/2017.
 */

public class fcm extends FirebaseInstanceIdService {
    public static final String TOKEN_BROADCAST="myfcmtokenbroadcast";
    @Override
    public void onTokenRefresh() {
        //sql Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("my", "Refreshed token: " + refreshedToken);
        storeToken(refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        // getApplicationContext().sendBroadcast(new Intent(TOKEN_BROADCAST));
         storeToken(refreshedToken);
    }
    private void storeToken(String token)
    {
        spm.getInstance(getApplicationContext()).saveDeviceToken(token);
    }
}
