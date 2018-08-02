package com.example.compaq.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


public class Alarm extends BroadcastReceiver
{
    private FirebaseUser user;
    private Firebase firebase;
    private FirebaseAuth firebaseAuth;

    Context c;
    @Override
    public void onReceive(Context context, Intent intent)
    {
Toast.makeText(context,"Alarm",Toast.LENGTH_LONG).show();
        FirebaseApp.initializeApp(context);
        c=context;
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PREET");
        wl.acquire();
        Firebase.setAndroidContext(context);
        firebaseAuth= FirebaseAuth.getInstance();
        firebase=new Firebase("https://myapp-54743.firebaseio.com/");
        user=firebaseAuth.getInstance().getCurrentUser();

        // Put here YOUR code.
        try {
            check();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        wl.release();
    }

    com.google.firebase.database.Query applesQuery;
    void check() throws ParseException {
        final int[] count = new int[1];
        String date;
        DateFormat df=null;
        df= new SimpleDateFormat("dd MM yyyy");
        Date dateobj = new Date();
        date= df.format(dateobj);
        final Date date2=new SimpleDateFormat("dd MM yyyy").parse(date);
        firebase.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User usernew = dataSnapshot.getValue(User.class);
                HashMap<String, ArrayList<Transaction>> transactions = new HashMap<String, ArrayList<Transaction>>();
                if (usernew.getTransactions() == null)
                    usernew.setTransactions(transactions);
                transactions = usernew.getTransactions();
                ArrayList<Transaction> l = new ArrayList<Transaction>();
                ArrayList<String> list = new ArrayList<String>();
                l = transactions.get("Reminders");
                if(l!=null)
                {
                for (int i = 0; i < l.size(); i++) {
                    try {

                        Date date1 = new SimpleDateFormat("dd MM yyyy").parse(transactions.get("Reminders").get(i).date);
                        long diff = TimeUnit.DAYS.convert((date1.getTime() - date2.getTime()), TimeUnit.MILLISECONDS);
                        if (diff == 2 || diff == 1 || diff == 0) {
                            nm mNotificationManager = new nm(c);
                            Intent intent = new Intent(c, MainActivity.class);
                            mNotificationManager.showSmallNotification("Payment Due", "Pay Rs" + transactions.get("Reminders").get(i).amount + " to " + transactions.get("Reminders").get(i).place, intent);

                            count[0]++;
                        } else if(diff<0) {
                                transactions.get("Reminders").remove(i);

                            firebase.child(user.getUid()).setValue(usernew);

                    }} catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }}
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }
    public void setAlarm(Context context)
    {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),1, pi); // Millisec * Second * Minute
    }

    public void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}