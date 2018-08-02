package com.example.compaq.myapplication;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText name;
    private EditText email;
    private EditText password;
    private Button btnRegister;
    private Button btnLogin;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    String Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FirebaseApp.initializeApp(this);
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        if(firebaseAuth.getCurrentUser()!=null)
        {
            finish();
            startActivity(new Intent(this,MainActivity.class));
        }
        name=(EditText)findViewById(R.id.name);
        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        btnRegister=(Button)findViewById(R.id.btnRegister);
        btnLogin=(Button)findViewById(R.id.btnLinkToLoginScreen);
        progressDialog=new ProgressDialog(this);
        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);


    }

    private void registerUser()
    {
        final  String Name=name.getText().toString().trim();
        Email=email.getText().toString().trim();
        String Password=password.getText().toString().trim();
        final HashMap<String,ArrayList<Transaction>> transactions=new HashMap<>();
        final ArrayList<Reminders> reminders=new ArrayList<>();

        if(TextUtils.isEmpty(Name)||TextUtils.isEmpty(Email)||TextUtils.isEmpty(Password))
        {
            Toast.makeText(this,"Enter Complete Details", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Registering User");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(Email,Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful())
                {
                    User user =new User(Name,Email,transactions,0,10000);
                    Toast.makeText(RegisterActivity.this,"User Successfully Registered", Toast.LENGTH_LONG).show();
                    databaseReference.child(task.getResult().getUser().getUid()).setValue(user);
                    sendtokentoserver();
                    finish();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
                else
                {
                   Exception e = task.getException();
                    Toast.makeText(RegisterActivity.this," Problems in Registering"+e, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void sendtokentoserver(){

        if(TextUtils.isEmpty(Email))
        {
            Toast.makeText(this,"enter your email",Toast.LENGTH_SHORT).show();
        }else{
                 if(spm.getInstance(RegisterActivity.this).getToken()!=null)
            {

                final String token = spm.getInstance(this).getToken();
                Toast.makeText(getApplicationContext(),token+Email,Toast.LENGTH_LONG).show();
                StringRequest stringRequest=new StringRequest(Request.Method.POST,
                        urls.register,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject obj=new JSONObject(response);
                                    Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params=new HashMap<>();
                        params.put("email",Email);
                        params.put("token",token);
                        return params;
                    }
                };
                RequestQueue requestQueue= Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);
            }
            else
            {
                Toast.makeText(this,"Token not generated",Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onClick(View v) {
        if(v==btnRegister)
        {
            registerUser();
        }
        if(v==btnLogin)
        {
            //Go To Login Activity
            finish();
           startActivity(new Intent(this,LoginActivity.class));
        }
    }
}
