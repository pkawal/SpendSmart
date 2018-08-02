package com.example.compaq.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText email;
    private EditText password;
    private Button btnRegister;
    private Button btnLogin;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null)
        {
            finish();
            startActivity(new Intent(this,MainActivity.class));
        }
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        btnRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        progressDialog = new ProgressDialog(this);
        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

    }

    private void userLogin()
    {

        final String Email=email.getText().toString().trim();
        if(spm.getInstance(LoginActivity.this).getToken()!=null) {

            final String token = spm.getInstance(this).getToken();
            Toast.makeText(getApplicationContext(),token+Email,Toast.LENGTH_LONG).show();
            StringRequest stringRequest=new StringRequest(Request.Method.POST,
                    urls.set,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {


                                Toast.makeText(getApplicationContext(),"token stored",Toast.LENGTH_LONG).show();

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





            String Password=password.getText().toString().trim();

        if(TextUtils.isEmpty(Email)||TextUtils.isEmpty(Password))
        {
            Toast.makeText(this, "Enter Complete Details", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Logging In User");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful())
                {

                    finish();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
                else
                {
                    Exception e = task.getException();

                    Toast.makeText(getApplicationContext(), "Failed to sign in "+e, Toast.LENGTH_SHORT).show();

                }

            }
        });


    }

    public void onClick(View v) {
        if (v == btnLogin) {
            userLogin();
        }
        if (v == btnRegister) {
            finish();
            startActivity(new Intent(this, RegisterActivity.class));
        }

    }
}
