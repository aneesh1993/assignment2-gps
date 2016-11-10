package com.mobileappclass.assignment3;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;

import android.net.ConnectivityManager;

import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ///////////////////////////// Final Variables //////////////////////////////////////////////////
    public static final String broadcastString = "com.example.aneesh.gpsdataapp.broadcast";
    public static final String networkType_wifi = "WiFi";
    public static final String networkType_roaming = "Roaming";
    public static final String netID = "ana85";

    ///////////////////////////// Private Variables ////////////////////////////////////////////////
    private IntentFilter intentFilter;
    private String networkDetails;

    ///////////////////////////// Public Variables /////////////////////////////////////////////////
    Toolbar toolbar;
    MyDBHandler dbHandler;
    public boolean isWifi, isMobile, isServerFrag;
    public boolean isServiceStarted = false;
    public String networkInfo, serverStauts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar)findViewById(R.id.customToolbar);
        setSupportActionBar(toolbar);
        dbHandler = new MyDBHandler(this, null, null, 1);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, 1);


        /////////////////////////////// Start the Service //////////////////////////////////////////

        Intent intent = new Intent(this, GPSDataService.class);
        startService(intent);
        isServiceStarted = true;

        intentFilter = new IntentFilter();
        intentFilter.addAction(broadcastString);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(networkType_wifi);
        intentFilter.addAction(networkType_roaming);


        /////////////////////////////// default Server Fragment ////////////////////////////////////

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            isServerFrag = true;
            Fragment newServerFragment = new ServerFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newServerFragment);
            transaction.addToBackStack(null);

            transaction.commit();

        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("serviceFlag", isServiceStarted);
    }


    ///////////////////////////// Register Intent Filter ///////////////////////////////////////////
    @Override
    public void onResume()
    {
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);
    }

    //////////////////////////////// Toolbar View //////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        Toast.makeText(MainActivity.this, "Select a database - Local or Server from the toolbar",
                Toast.LENGTH_SHORT).show();

        return super.onCreateOptionsMenu(menu);
    }

    //////////////////////////// Toolbar Option Listener ///////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //////// if online selected //////////////
        if(item.getItemId() == R.id.action_online){

            isServerFrag = true;
            Bundle args = new Bundle();
            Log.i("networkInfo", networkInfo);
            args.putString("networkInfo", networkInfo);
            args.putString("serverStatus", serverStauts);

            Fragment newServerFragment = new ServerFragment();
            newServerFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newServerFragment);
            transaction.addToBackStack(null);

            transaction.commit();

        }

        //////// if offline selected //////////////
        if(item.getItemId() == R.id.action_offline){

            isServerFrag = false;

            Fragment newLocalFragment = new LocalFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newLocalFragment);
            transaction.addToBackStack(null);

            transaction.commit();
        }

        //////// if query selected //////////////
        if(item.getItemId() == R.id.action_query){
            isServerFrag = false;

            Fragment newQueryFragment = new QueryFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newQueryFragment);
            transaction.addToBackStack(null);

            transaction.commit();

        }

        return super.onOptionsItemSelected(item);
    }

    //////////////////////////////// Broadcast Receiver ////////////////////////////////////////////
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            ///////////////////// GPS Data update //////////////////////////////
            ///////// do 2 things -------->
            //////// 1. populate lists (local and server)
            //////// 2. check if wifi and upload data to server

            if(intent.getAction().equals(broadcastString)){

                ////////////////////// If portrait ///////////////////////////////////////////
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){


                    /////////////////////// Local Fragment populate list /////////////////////////////
                    if(getSupportFragmentManager().findFragmentById(R.id.fragment_container) != null &&
                            getSupportFragmentManager().findFragmentById(R.id.fragment_container).
                                    toString().startsWith("LocalFragment")){

                        LocalFragment lf = (LocalFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                        lf.populateList(dbHandler.readDB());
                    }

                }
                else{ //// (means it is landscape) //////
                    LocalFragment lf = (LocalFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_local);
                    lf.populateList(dbHandler.readDB());

                }

                ////////////////////// check if wifi and upload data to server /////////////////////
                if(isWifi){
                    new FirebaseUpload().execute(dbHandler.readDB());
                }else{
                    System.out.println("No Wifi");
                    Toast.makeText(MainActivity.this, "Cannot upload data... No Wifi!\n" +
                            "Press Sync to upload using Mobile data", Toast.LENGTH_SHORT).show();
                    setServerStatus("Not Connected");
                    serverStauts = "Not Connected";
                }
            }
            Intent stopIntent = new Intent(MainActivity.this, GPSDataService.class);
            stopService(stopIntent);


            ////////////////// Network Type Update ///////////////////////////////////
            ///////// do 2 things -------->
            //////// 1. set variables in Main Activity
            //////// 2. set TextViews in Server Fragment
            if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){

                Bundle extras = intent.getExtras();
                if (extras != null) {

                    if(extras.get("networkType").equals(ConnectivityManager.TYPE_MOBILE)){
                        networkInfo = "Mobile Network";
                        isMobile = true;
                        isWifi = false;
                    }
                    else if(extras.get("networkType").equals(ConnectivityManager.TYPE_WIFI) &&
                            !extras.get("extraInfo").equals("<unknown ssid>")){

                        networkInfo = "WiFi: " + extras.get("extraInfo");
                        isMobile = false;
                        isWifi = true;
                    }
                    else{
                        networkInfo = "No Network";
                        isMobile = isWifi = false;
                    }

                    if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){

                        ServerFragment serverFragment = (ServerFragment)getSupportFragmentManager().
                                findFragmentById(R.id.fragment_server);

                        serverFragment.setNetworkStatus(networkInfo);
                    }
                    else if(getSupportFragmentManager().findFragmentById(R.id.fragment_container) != null &&
                            getSupportFragmentManager().findFragmentById(R.id.fragment_container).
                                    toString().startsWith("ServerFragment")){

                        networkDetails = networkInfo;

                        if(getSupportFragmentManager().findFragmentById(R.id.fragment_container) != null &&
                                getSupportFragmentManager().findFragmentById(R.id.fragment_container).
                                        toString().startsWith("ServerFragment")){

                            ServerFragment serverFragment = (ServerFragment)getSupportFragmentManager().
                                    findFragmentById(R.id.fragment_container);
                            serverFragment.setNetworkStatus(networkDetails);
                        }

                    }

                }
                else {
                    Log.v("Connectivity", "no extras");
                }

            }
        }


    };

    /////////////////////////////// Sync Button Click Listener /////////////////////////////////////
    public void syncClick(View view) {

        ////////////// if wifi or mobile data available, upload data to server /////////////////////
        ///////////// if nothing available, set TextView as not connected and give a Toast//////////
        if(isWifi || isMobile){
            new FirebaseUpload().execute(dbHandler.readDB());
        }
        else{
            Toast.makeText(MainActivity.this, "Cannot upload data... No Connectivity", Toast.LENGTH_SHORT).show();
            setServerStatus("Not Connected");
            serverStauts = "Not Connected";
        }

    }

    ////////////////////// Method checks portrait(if yes, is server fragment available), or landscape
    ////////////////////// and sets the 'server status' TextView
    public void setServerStatus(String status){
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){

            if(getSupportFragmentManager().findFragmentById(R.id.fragment_container) != null &&
                    getSupportFragmentManager().findFragmentById(R.id.fragment_container).
                            toString().startsWith("ServerFragment")){

                ServerFragment sf = (ServerFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                sf.setConnectionStatus(status);

            }
        }
        else{

            if(getSupportFragmentManager().findFragmentById(R.id.fragment_server) != null){
                ServerFragment sf = (ServerFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_server);
                sf.setConnectionStatus("Connected");
                serverStauts = "Connected";
            }
        }
    }



    ///////////////////////// Async Task to upload data to server //////////////////////////////////
    public class FirebaseUpload extends AsyncTask<ArrayList<String>, Void, Void>{

        @Override
        protected Void doInBackground(ArrayList<String>... params) {

            ArrayList<String> data = params[0];

            String[] splitData;
            for(int i = 0; i < data.size(); i++) {

                splitData = data.get(i).split("\t");
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Students"); // What database can I actually talk to?
                DatabaseReference students = ref.child(netID);
                DatabaseReference bart = students.child(splitData[0]);
                bart.child("date").setValue(splitData[0]);
                bart.child("x").setValue(splitData[1]);
                bart.child("y").setValue(splitData[2]);
                bart.child("netid").setValue(netID);

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(getApplicationContext(), "Data Uploaded to Firebase", Toast.LENGTH_SHORT).show();
            setServerStatus("Connected");
            serverStauts = "Connected";

            super.onPostExecute(aVoid);
        }
    }

}
