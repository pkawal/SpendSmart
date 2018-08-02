package com.example.compaq.myapplication;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class   MainActivity extends AppCompatActivity implements View.OnClickListener,FragmentDrawer.FragmentDrawerListener,ProfileFragment.OnFragmentInteractionListener
{

    private FirebaseAuth firebaseAuth;
    private FragmentDrawer drawerFragment;
    private FirebaseUser user;
    private Firebase firebase;
    int food=0,clothes=0,grocery=0,utilities=0,spent=0;
    private Alarm a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(this);

        Firebase.setAndroidContext(this);
        firebaseAuth=FirebaseAuth.getInstance();

        calculatetotal();

        displayView(0);


      a=new Alarm();
        Context c=this.getApplicationContext();
        if(a!=null)
        {
            a.setAlarm(c);

        }else{

            Toast.makeText(c, "Alarm is null", Toast.LENGTH_SHORT).show();

        }

}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.reports) {
            Intent intent = new Intent(this,ReportActivity.class);

            Toast.makeText(MainActivity.this,"Readyagain "+food,Toast.LENGTH_LONG).show();
            intent.putExtra("food",food);
            intent.putExtra("grocery",grocery);
            intent.putExtra("utility",utilities);
            intent.putExtra("clothes",clothes);
            intent.putExtra("total",spent);
            this.startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void onClick(View v) {

    }
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);

    }
    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                title = getString(R.string.title_home);
                break;
            /*case 1:
                fragment = new FriendsFragment();
                title = getString(R.string.title_friends);
                break;*/
            case 2:
                fragment = new ProfileFragment();
                title = "Profile";
                break;
            case 3:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this,LoginActivity.class));
                break;




            /*default:



                break;*/
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
void calculatetotal(){

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
            if(transactions.get("Food")==null)
                transactions.put("Food", list);
            list=transactions.get("Food");
            if(list!=null) {
                for (int i = 0; i < list.size(); i++) {
                    food = food + transactions.get("Food").get(i).amount;
                    spent = spent + transactions.get("Food").get(i).amount;
                }
                list.clear();
            }
               list=transactions.get("Clothes");
               if(list!=null){
                for(int i=0;i<list.size();i++)
                {
                    clothes=clothes+transactions.get("Clothes").get(i).amount;
                    spent=spent+transactions.get("Clothes").get(i).amount;
                }
                list.clear();}
                list=transactions.get("Groceries");
               if(list!=null) {
                   for (int i = 0; i < list.size(); i++) {
                       grocery = grocery + transactions.get("Groceries").get(i).amount;
                       spent = spent + transactions.get("Groceries").get(i).amount;
                   }
                   list.clear();
               }
                list=transactions.get("Utilities");
                if(list!=null){for(int i=0;i<list.size();i++)
                {
                    if(transactions.get("Utilities").get(i).type=="credited")
                    { utilities=utilities+transactions.get("Utilities").get(i).amount;
                    spent=spent+transactions.get("Utilities").get(i).amount;}
                }
                list.clear();}

            }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    });
}
}
