package com.example.compaq.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by compaq on 3/29/2017.
 */

public class Pay extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseUser user;
    private Firebase firebase;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebase=new Firebase("https://myapp-54743.firebaseio.com/");
        user=firebaseAuth.getInstance().getCurrentUser();


      /*  viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
*/

        recyclerView = (RecyclerView) recyclerView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        firebase.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User usernew = dataSnapshot.getValue(User.class);
                HashMap<String,ArrayList<Transaction>> transactions=new HashMap<String, ArrayList<Transaction>>();
                if(usernew.getTransactions()==null)
                    usernew.setTransactions(transactions);
                transactions=usernew.getTransactions();
                ArrayList<Transaction> list=new ArrayList<Transaction>();

                if(transactions.get("Split")==null)
                    transactions.put("Split", list);

                adapter = new RecyclerAdapter(transactions.get("Split"));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        recyclerView.setAdapter(adapter);

    }

}



