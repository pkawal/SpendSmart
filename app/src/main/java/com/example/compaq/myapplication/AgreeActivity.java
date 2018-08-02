package com.example.compaq.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by compaq on 3/28/2017.
 */

public class AgreeActivity extends AppCompatActivity {
    private FirebaseUser user;
    private Firebase firebase;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final String newString;
        firebase=new Firebase("https://myapp-54743.firebaseio.com/");
        user=firebaseAuth.getInstance().getCurrentUser();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {
                newString= extras.getString("split");
            }
        } else {
            newString= (String) savedInstanceState.getSerializable("split");
        }
        Toast.makeText(this,newString,Toast.LENGTH_SHORT).show();
        new AlertDialog.Builder(this)
                .setTitle("Do you agree?")
                .setMessage(newString)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sendnotification(newString);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent myIntent = new Intent(AgreeActivity.this, ManualActivity.class);
                       AgreeActivity.this.startActivity(myIntent);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }
    public void sendnotification(final String message)
    {
        int i=message.indexOf("Email");
        final String finalMessage = message.substring(i+6);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,urls.send ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(AgreeActivity.this, response, Toast.LENGTH_LONG).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", "ok");
                params.put("message",message+"$"+user.getEmail());
                params.put("email", finalMessage);
                return params;
            }
        };


        MyVolley.getInstance(AgreeActivity.this).addToRequestQueue(stringRequest);

        final Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        String datefinal = df.format(c.getTime());
        i=message.indexOf("Pay")+6;
        final String pay=message.substring(i,message.indexOf("to")-1);
        ArrayList<Persons> persons=new ArrayList<>();
        final Transaction transaction=new Transaction(Integer.parseInt(pay),finalMessage,datefinal,"pay");
        firebase.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User usernew=dataSnapshot.getValue(User.class);
                HashMap<String,ArrayList<Transaction>> transactions=new HashMap<String, ArrayList<Transaction>>();
                if(usernew.getTransactions()==null) {
                    usernew.setTransactions(transactions);
                  }transactions=usernew.getTransactions();
                ArrayList<Transaction> list=new ArrayList<Transaction>();

                if(transactions.get("Split")==null)
                    transactions.put("Split", list);

                transactions.get("Split").add(transaction);
                firebase.child(user.getUid()).setValue(usernew);
                finish();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });

    }
}
