package com.example.compaq.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class IncomingSms extends BroadcastReceiver {

    final SmsManager sms = SmsManager.getDefault();
    String type = "", money = "", date = "",name="";
    private FirebaseUser user;
    private Firebase firebase;
    private FirebaseAuth firebaseAuth;
Context c;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"incoming", Toast.LENGTH_LONG).show();

        // Retrieves a map of extended data from the intent.
       // final Bundle bundle = intent.getExtras();

c=context;

        final Bundle bundle = intent.getExtras();

/*        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            SmsMessage[] smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
           Toast t=Toast.makeText(context,smsMessages.toString(),Toast.LENGTH_LONG);
            t.show();
        }
*/

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    name=currentMessage.getOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    int duration = Toast.LENGTH_LONG;
                    message=message.toLowerCase();


                    if(message.contains("airtel"))
                        type="AIRTEL";
                    else if(message.contains("tata sky"))
                        type="TATA SKY";
                    else if(message.contains("hdfc"))
                        type="HDFC BANK";
                    Toast.makeText(c," zero senderNum: "+name+ "type:"+type+"money:"+money, Toast.LENGTH_LONG).show();


                    if(message.contains("credited")||message.contains("debited"))
                    printbill_bank(message);

                    else if((message.contains("due"))||(message.contains("till"))||(message.contains("scheduled"))) {
                        Toast.makeText(c," first senderNum: "+name+ "type:"+type+"money:"+money, Toast.LENGTH_LONG).show();
                        printbill_companies(message);
                    }

if(type=="" || date==""|| money==""|| name=="")
return;

                    Toast toast = Toast.makeText(context,"senderNum: "+name+ "type:"+type+"money:"+money, duration);
                    toast.show();
                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);

        }
    }

    String extract(int x,String msg)
    {

        int flag=0;
        String bal="";
        while(!Character.isDigit(msg.charAt(x)))
        {
            x++;
        }
        while(true)
        {
            if(Character.isDigit(msg.charAt(x))||msg.charAt(x)=='.') {
               if(msg.charAt(x)=='.'&& flag==1)
                   break;
               if(msg.charAt(x)=='.')
                   flag=1;
                bal=bal+msg.charAt(x);
                if(!Character.isDigit(msg.charAt(x+1))&&msg.charAt(x+1)!='.')
                {
                    break;
                }
            }
            x++;

        }
        return bal;
    }
    void printbill_bank( String msg)
    {

        msg=msg.toLowerCase();
        final String a=msg;
        String bal="";
        String[] words = msg.split("\\s+");
        int ind = 0;
        for (int i = 0; i < words.length; i++)
        {

            words[i] = words[i].replaceAll("[^\\w]", "");

            if((words[i].equalsIgnoreCase("balance"))||(words[i].equalsIgnoreCase("bal")))
            {
                ind=msg.indexOf(words[i]);
                bal=extract(ind,msg);
            }



            if(words[i].equalsIgnoreCase("debited")||words[i].equalsIgnoreCase("credited"))
            {
                if((words[i].equalsIgnoreCase("debited"))||(words[i].equalsIgnoreCase("credited")))
                {
                    ind=msg.indexOf(words[i]);
                    type=words[i];
                    money=extract(ind,msg);
                }


            }
        }




        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        date = df.format(c.getTime());
        if(type=="" || date==""|| money==""|| name=="")
            return;
        final  Transaction transaction=new Transaction((int)(Double.parseDouble(money)),name+" "+type,date);
        firebase=new Firebase("https://myapp-54743.firebaseio.com/");
        user=firebaseAuth.getInstance().getCurrentUser();
        firebase.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User usernew=dataSnapshot.getValue(User.class);

                HashMap<String,ArrayList<Transaction>> transactions=new HashMap<String, ArrayList<Transaction>>();
                if(usernew.getTransactions()==null)
                    usernew.setTransactions(transactions);
                transactions=usernew.getTransactions();
                ArrayList<Transaction> list=new ArrayList<Transaction>();
                if(transactions.get("Utilities")==null)
                    transactions.put("Utilities", list);
                transactions.get("Utilities").add(transaction);
                firebase.child(user.getUid()).setValue(usernew);
                if(a.contains("credited"))
                {

                    usernew.spent+=Double.parseDouble(money);
                    firebase.child(user.getUid()).setValue(usernew);
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



    }
    void printbill_companies(String msg)

    {


        int ind = 0;
        Double min=0.0;

        msg=msg.toLowerCase();
        if((msg.contains("due"))||(msg.contains("till"))||(msg.contains("scheduled")))
        {
            ind=msg.indexOf("due")+msg.indexOf("till")+msg.indexOf("scheduled")+2;
            date(ind,msg);
            type="";
            ind=0;

            for (int index = msg.indexOf("rs");
                 index >= 0;
                 index = msg.indexOf("rs", index + 1)) {
                money = extract(index, msg);
                if (ind == 0) {
                    min = Double.parseDouble(money);
                }
                if (min > Double.parseDouble(money)) {
                    min = Double.parseDouble(money);
                }

                ind = 1;
            }
        }
    money=min+"";
if(type=="")
{
    type=name;
}
        if(msg.contains("airtel"))
            type="AIRTEL";
        else if(msg.contains("tata sky"))
            type="TATA SKY";
        else if(msg.contains("hdfc"))
            type="HDFC BANK";

        date=dateformat(date);
        Toast.makeText(c,"second senderNum: "+name+ "type:"+type+"money:"+money, Toast.LENGTH_LONG).show();

        if(type=="" || date==""|| money==""|| name=="")
            return;

        final  Transaction transaction=new Transaction((int)(Double.parseDouble(money)),name+" "+type,date);
        firebase=new Firebase("https://myapp-54743.firebaseio.com/");
        user=firebaseAuth.getInstance().getCurrentUser();
        firebase.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User usernew=dataSnapshot.getValue(User.class);

                HashMap<String,ArrayList<Transaction>> transactions=new HashMap<String, ArrayList<Transaction>>();
                if(usernew.getTransactions()==null)
                    usernew.setTransactions(transactions);
                transactions=usernew.getTransactions();
                ArrayList<Transaction> list=new ArrayList<Transaction>();
                if(transactions.get("Reminders")==null)
                    transactions.put("Reminders", list);
                transactions.get("Reminders").add(transaction);
                firebase.child(user.getUid()).setValue(usernew);


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }
    void date(int x,String msg)
    {
        DateFormat df=null;
        if(msg.indexOf("today", x)>-1)
        {
            df= new SimpleDateFormat("dd-MM-yy");
            Date dateobj = new Date();
            date= df.format(dateobj);
        }
        else if(msg.indexOf("tomorrow", x)>-1)
        {
            df= new SimpleDateFormat("dd-MM-yy HH:mm:ss");
            Date dateobj = Calendar.getInstance().getTime();
            Calendar c = Calendar.getInstance();
            String dt = df.format(dateobj);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
            c = Calendar.getInstance();
            try {
                c.setTime(sdf.parse(dt));
            } catch (ParseException ex) {
                //Logger.getLogger(Pr.class.getName()).log(Level.SEVERE, null, ex);
            }
            c.add(Calendar.DATE, 1);  // number of days to add
            date = sdf.format(c.getTime());

        }

        int p=0;
        while(!Character.isDigit(msg.charAt(x)))
            x++;
        while(x<=msg.length()&&msg.charAt(x)!=' '&&msg.charAt(x)!='.'&&msg.charAt(x)!='\n')
        {
            if(msg.charAt(x)=='/'||msg.charAt(x)=='-')
                p++;
            date=date+msg.charAt(x);

            x++;
        }

      if(p!=2)
      {
          date="";
          date(x,msg);

      }

    }
    String dateformat(String date)
    {
        String day,month;
        date=date.toLowerCase();
        if(date.contains("jan"))
            month="01";
        else if(date.contains("feb"))
            month="02";
        else if(date.contains("mar"))
            month="03";
        else if(date.contains("apr"))
            month="04";
        else if(date.contains("may"))
            month="05";
        else if(date.contains("jun"))
            month="06";
        else if(date.contains("jul"))
            month="07";
        else if(date.contains("aug"))
            month="08";
        else if(date.contains("sep"))
            month="09";
        else if(date.contains("oct"))
            month="10";
        else if(date.contains("nov"))
            month="11";
        else if(date.contains("dec"))
            month="12";
        else
        {
            int i=date.indexOf('/')+date.indexOf("-")+date.indexOf(",")+2;
            month=""+date.charAt(i+1)+date.charAt(i+2);
        }
        date=""+date.charAt(0)+date.charAt(1)+" "+month+" "+"2017";
        return date;
    }
}