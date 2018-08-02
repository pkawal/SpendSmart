package com.example.compaq.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.util.List;
import java.util.Map;

import com.example.compaq.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.compaq.myapplication.R.color.white;
import static java.lang.Thread.sleep;

public class ManualActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener {
    // private Spinner spinner;
        private EditText amount,place,number;
        private Button submit,split;
        private String category;
        private FirebaseUser user;
        private Firebase firebase;
        private FirebaseAuth firebaseAuth;
        List<Spinner> allEds = new ArrayList<Spinner>();

    private List<String> devices = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allEds.clear();
        email="";
        setContentView(R.layout.activity_manual);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        firebase=new Firebase("https://myapp-54743.firebaseio.com/");
        user=firebaseAuth.getInstance().getCurrentUser();
        amount=(EditText)findViewById(R.id.amount);
        place=(EditText)findViewById(R.id.place);
        number=(EditText)findViewById(R.id.number);
        submit=(Button)findViewById(R.id.btnSave);
        split=(Button)findViewById(R.id.split);
        submit.setOnClickListener(this);
        split.setOnClickListener(this);
        String[] SPINNERLIST = {"Food", "Clothes", "Groceries", "Utilities"};
        Spinner spinner = (Spinner) findViewById(R.id.category);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        loadRegisteredDevices();
        Toast.makeText(ManualActivity.this,devices.toString()+"kok",Toast.LENGTH_LONG).show();
       /* ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, SPINNERLIST);
        MaterialBetterSpinner materialDesignSpinner = (MaterialBetterSpinner)
                findViewById(R.id.android_material_design_spinner);
        materialDesignSpinner.setAdapter(arrayAdapter);
        materialDesignSpinner.setOnItemSelectedListener(this);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manual, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        category = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
int i;
    String email="",message;
    private ProgressDialog progressDialog;
    @Override
    public void onClick(View v) {
        if(v==submit)
        {
            progressDialog = new ProgressDialog(this);
         //   email=new String[allEds.size()];
            final int each=Integer.parseInt(amount.getText().toString())/(allEds.size()+1);
            final String title="Split Bill";

            firebase.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User usernew = dataSnapshot.getValue(User.class);
                    message ="Pay Rs"+each+" to "+usernew.getName()+"\nEmail "+usernew.getEmail();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
                    for(i=0;i<allEds.size();i++) {
                        Toast.makeText(ManualActivity.this,email.toString(),Toast.LENGTH_LONG).show();
                        email=email+allEds.get(i).getSelectedItem().toString()+" ";
                    }
            if(allEds.size()==0)
            {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
                String datefinal = df.format(c.getTime());

                final Transaction transaction=new Transaction(Integer.parseInt(amount.getText().toString()),place.getText().toString(),datefinal);
                firebase.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User usernew=dataSnapshot.getValue(User.class);
                        HashMap<String,ArrayList<Transaction>> transactions=new HashMap<String, ArrayList<Transaction>>();
                        if(usernew.getTransactions()==null)
                            usernew.setTransactions(transactions);
                        transactions=usernew.getTransactions();
                        ArrayList<Transaction> list=new ArrayList<Transaction>();

                        if(transactions.get(category)==null)
                            transactions.put(category, list);

                        transactions.get(category).add(transaction);

                        usernew.spent+=Integer.parseInt(amount.getText().toString());
                        firebase.child(user.getUid()).setValue(usernew);
                        Toast.makeText(ManualActivity.this,"Transaction Added",Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

            }
           else{

                            progressDialog.setMessage("Sending Notifications");
                            progressDialog.show();


                            StringRequest stringRequest = new StringRequest(Request.Method.POST,urls.send ,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                           progressDialog.dismiss();
                                            Toast.makeText(ManualActivity.this, response, Toast.LENGTH_LONG).show();

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
                                    params.put("title", title);
                                    params.put("message", message);
                                    params.put("email",email);
                                    return params;
                                }
                            };

                            MyVolley.getInstance(ManualActivity.this).addToRequestQueue(stringRequest);

                        }




        /*            Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
                    String datefinal = df.format(c.getTime());
                    ArrayList<Persons> persons=new ArrayList<>();
                    int each=Integer.parseInt(amount.getText().toString())/(allEds.size()+1);
                    for(int i=0; i < allEds.size(); i++)
                    {
                         //persons.add(new Persons(allEds.get(i).getText().toString(),each));
                    }
          final Transaction transaction=new Transaction(Integer.parseInt(amount.getText().toString()),place.getText().toString(),datefinal,persons);
            firebase.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                   /* GenericTypeIndicator<ArrayList<Transaction>> t = new GenericTypeIndicator<ArrayList<Transaction>>() {};
                    ArrayList<Transaction> yourStringArray = dataSnapshot.getValue(t);*/
      //              User usernew=dataSnapshot.getValue(User.class);
                    //User user=dataSnapshot.getValue(User.class);
                    /*ArrayList<Transaction> arrayList=usernew.getTransactions();
                    arrayList.add(transaction);*/

          /*          HashMap<String,ArrayList<Transaction>> transactions=new HashMap<String, ArrayList<Transaction>>();
                    if(usernew.getTransactions()==null)
                        usernew.setTransactions(transactions);
                    transactions=usernew.getTransactions();
                    ArrayList<Transaction> list=new ArrayList<Transaction>();

                    if(transactions.get(category)==null)
                        transactions.put(category, list);

                    transactions.get(category).add(transaction);

                    usernew.spent+=Integer.parseInt(amount.getText().toString());
                    firebase.child(user.getUid()).setValue(usernew);
                    Toast.makeText(ManualActivity.this,"Transaction Added",Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });


*/

        }
        if(v==split)
        {
            LinearLayout linear = (LinearLayout) findViewById(R.id.editTextGroupLayout);
            linear.setOrientation(LinearLayout.VERTICAL);
            Spinner ed;
            //allEds = new ArrayList<EditText>();

            int count =Integer.parseInt(number.getText().toString());
            for (int i = 0; i < count; i++) {

                ed = new Spinner(ManualActivity.this);

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        ManualActivity.this,
                        R.layout.spinner_item,
                        devices);

                ed.setAdapter(arrayAdapter);
                allEds.add(ed);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,20,0,20);
                linear.addView(ed,params);
            }

        }
    }

List<String> de=new ArrayList<>();
    void loadRegisteredDevices()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Devices...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urls.getdevices,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);

                            if (!obj.getBoolean("error")) {
                                JSONArray jsonDevices = obj.getJSONArray("devices");

                                for (int i = 0; i < jsonDevices.length(); i++) {

                                    JSONObject d = jsonDevices.getJSONObject(i);
                                    devices.add(d.getString("email"));

                                }


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

        };
        Toast.makeText(ManualActivity.this,de.toString()+"pp",Toast.LENGTH_LONG).show();

        progressDialog.dismiss();

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);

    }


}

