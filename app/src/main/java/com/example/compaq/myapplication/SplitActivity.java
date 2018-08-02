package com.example.compaq.myapplication;

import android.app.TabActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.compaq.myapplication.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplitActivity extends TabActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FirebaseUser user;
    private Firebase firebase;
    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private int[] tabIcons = {
            R.drawable.pay,
            R.drawable.recieve,
    };
int i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pay);
        TabHost tabHost = getTabHost();
        TabSpec inboxSpec = tabHost.newTabSpec("PAY");
        // Tab Icon
        inboxSpec.setIndicator("PAY", getResources().getDrawable(R.drawable.pay));
        Intent inboxIntent = new Intent(this, PayFragment.class);
        // Tab Content
        inboxSpec.setContent(inboxIntent);


        // Outbox Tab
       TabSpec outboxSpec = tabHost.newTabSpec("Recieve");
        outboxSpec.setIndicator("RECIEVE", getResources().getDrawable(R.drawable.recieve));
        Intent outboxIntent = new Intent(this, RecieveFragmant.class);
        outboxSpec.setContent(outboxIntent);

        tabHost.addTab(inboxSpec);
        tabHost.addTab(outboxSpec);
        /*toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        new GetDataFromFirebase().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        firebase=new Firebase("https://myapp-54743.firebaseio.com/");
        user=firebaseAuth.getInstance().getCurrentUser();

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
                recyclerView.setAdapter(new RecyclerAdapter(transactions.get("Split")));

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
*/
    }
    private class GetDataFromFirebase extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }
}