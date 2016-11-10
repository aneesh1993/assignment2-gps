package com.mobileappclass.assignment3;

import android.app.Service;

import android.content.Context;
import android.content.Intent;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.net.ConnectivityManager;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Handler;

import android.util.Log;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.TimeZone;


public class GPSDataService extends Service {
    public GPSDataService() {
    }

    private static final String LOG_TAG = "gpsdataapp-LOG";
    private static final int WAIT_TIME = 10000;

    public boolean isNetwork = false;
    public boolean isWifi = false;
    public boolean isRoaming = false;

    Handler timeHandler;
    public String[] dataFromGDP = new String[3];

    MyDBHandler dbHandler;

    public ConnectivityManager connectivityManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        getGPSData();

        dbHandler = new MyDBHandler(this, null, null, 1);

        timeHandler = new Handler();
        Runnable timeRunnable = new Runnable() {
            @Override
            public void run() {

                if(dataFromGDP[0] != null && dataFromGDP[1] != null && dataFromGDP[2] != null){

                    GPSDataDb dbEntry = new GPSDataDb(dataFromGDP[0], dataFromGDP[1], dataFromGDP[2]);
                    dbHandler.addEntry(dbEntry);

                    Intent broadcast = new Intent();
                    broadcast.setAction(MainActivity.broadcastString);
                    sendBroadcast(broadcast);
                }
                timeHandler.postDelayed(this, WAIT_TIME);
            }
        };

        timeHandler.postDelayed(timeRunnable, 0);
        //return super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;
    }

    public String formatDate(long time){

        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd hh:mm:ss a");

        sdf.setTimeZone(TimeZone.getTimeZone("EST"));
        return sdf.format(date);

    }

    public void getGPSData(){

        LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        try {
            final Location loc = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    dataFromGDP[0] = formatDate(location.getTime());
                    dataFromGDP[1] = location.getLatitude() + "";
                    dataFromGDP[2] = location.getLongitude() + "";


                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });

        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy is called");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
