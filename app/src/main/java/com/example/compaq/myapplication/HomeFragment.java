package com.example.compaq.myapplication;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

import com.example.compaq.myapplication.R;


public class HomeFragment extends Fragment implements View.OnClickListener {
    private TextView budget,spent;
    private ImageView img1,img2,img3,img4;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private Firebase firebase;



    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this.getActivity());
        firebase=new Firebase("https://myapp-54743.firebaseio.com/");
        user=firebaseAuth.getInstance().getCurrentUser();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        img1=(ImageView)rootView.findViewById(R.id.firstimage);
        img2=(ImageView)rootView.findViewById(R.id.secondimage);
        img3=(ImageView)rootView.findViewById(R.id.thirdimage);
        img4=(ImageView)rootView.findViewById(R.id.fourthimage);

        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);
        img4.setOnClickListener(this);
        budget=(TextView)rootView.findViewById(R.id.budgetText);
        spent=(TextView)rootView.findViewById(R.id.spentText);
        firebase.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String,String> map=dataSnapshot.getValue(Map.class);
                spent.setText("Spent:" + String.valueOf(map.get("spent")));
                budget.setText("Budget:"+String.valueOf(map.get("budget")));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        // budget.setText(user.budget);



        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        if(v==img2){
            startActivity(new Intent(getActivity(),ManualActivity.class));
        }
        if(v==img3){
            startActivity(new Intent(getActivity(),TransactionActivity.class));
        }
        if(v==img1)
        {
            startActivity(new Intent(getActivity(), OCRActivity.class));

        }
        if(v==img4)
        {
            startActivity(new Intent(getActivity(), SplitActivity.class));
        }
    }
}