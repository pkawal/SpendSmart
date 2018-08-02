package com.example.compaq.myapplication;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by compaq on 3/25/2017.
 */

public class ms extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    private FirebaseUser user;
    private Firebase firebase;
    private FirebaseAuth firebaseAuth;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        if (remoteMessage.getData().size() > 0) {

            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }

    }

    //this method will display the notification
    //We are passing the JSONObject that is received from
    //firebase cloud messaging
    private int sendPushNotification(JSONObject json) {
        //optionally we can display the json into log
        Log.e(TAG, "Notification JSON " + json.toString());

        try {
            //getting the json data
            JSONObject data = json.getJSONObject("data");

            //parsing json data
            final String title = data.getString("title");
            final String message = data.getString("message");
            String imageUrl = data.getString("image");
            //creating MyNotificationManager object

            //if(title=="ok")
            //{
                firebase=new Firebase("https://myapp-54743.firebaseio.com/");
                user=firebaseAuth.getInstance().getCurrentUser();


if(title.equalsIgnoreCase("ok")){
    Handler handler = new Handler(Looper.getMainLooper());

    handler.post(new Runnable() {

        @Override
        public void run() {
               }
    });

                entry(title,message);
               return 0;
        }
else {

                nm mNotificationManager = new nm(getApplicationContext());

                //creating an intent for the notification
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                //if there is no image
                if (imageUrl.equals("null")) {
                    //displaying small notification
                    mNotificationManager.showSmallNotification(title, message, intent);
                } else {
                    //if there is an image
                    //displaying a big notification
                    mNotificationManager.showBigNotification(title, message, imageUrl, intent);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    return 0;
    }
    private void entry(String title,String message)
    {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(ms.this.getApplicationContext(),"entry mil gayi",Toast.LENGTH_SHORT).show();
            }
        });

        int i=message.indexOf("$");
        final Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        String datefinal = df.format(c.getTime());
        final String pay=message.substring(message.indexOf("Pay")+6,message.indexOf("to")-1);
        message=message.substring(i+1);
        ArrayList<Persons> persons=new ArrayList<>();
        final Transaction transaction=new Transaction(Integer.parseInt(pay),message,datefinal,"recieve");
        firebase.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User usernew=dataSnapshot.getValue(User.class);
                HashMap<String,ArrayList<Transaction>> transactions=new HashMap<String, ArrayList<Transaction>>();
                if(usernew.getTransactions()==null)
                    usernew.setTransactions(transactions);
                transactions=usernew.getTransactions();
                ArrayList<Transaction> list=new ArrayList<Transaction>();

                if(transactions.get("Split")==null)
                    transactions.put("Split", list);

                transactions.get("Split").add(transaction);
                firebase.child(user.getUid()).setValue(usernew);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });

    }
}
