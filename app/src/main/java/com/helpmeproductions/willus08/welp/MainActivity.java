package com.helpmeproductions.willus08.welp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.nfc.Tag;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

import com.helpmeproductions.willus08.welp.RestCallBoundService.MyBinder;
import com.helpmeproductions.willus08.welp.model.Buisnesses;
import com.helpmeproductions.willus08.welp.model.Business;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "test" ;
    @BindView(R.id.rvResultsView)
    RecyclerView resultsView;

    private static final int MY_PERMISSIONS_REQUEST_REQUEST_LOCATION = 0;
    private boolean serviceBound = false;
    private boolean locationPermisionGiven = false;
    RestCallBoundService boundService;
    FusedLocationProviderClient locationProvider;
    double lattitude =0;
    double longitude =0;
    RecyclerAdapter adapter;
    DefaultItemAnimator animator;
    RecyclerView.LayoutManager layout;
    List<Business> businesses = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        layout = new LinearLayoutManager(this);
        animator = new DefaultItemAnimator();

        adapter = new RecyclerAdapter(businesses);

        resultsView.setAdapter(adapter);
        resultsView.setLayoutManager(layout);
        resultsView.setItemAnimator(animator);


        // use this to see if the user version is greater than or equal to m
        // if its less than then permissions were already accepted at runtime
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissons();
        }
    }


    // checks for the permissions needed for the app
    private void checkPermissons() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: Permission not granted");

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d(TAG, "onCreate: We need this");

                Toast.makeText(this, "App can not Continue Location Tracking is off", Toast.LENGTH_LONG).show();
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_REQUEST_LOCATION);
                Log.d(TAG, "onCreate: Requesting permission");

                return;
            }
        } else {
            getLocation();
            //use this to to start if everything is accepted
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        locationProvider = new FusedLocationProviderClient(this);
        Log.d(TAG, "getLocation: gets the location");
        locationProvider.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            lattitude = location.getLatitude();
                            longitude = location.getLongitude();
                            getBuisness(lattitude,longitude);
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, RestCallBoundService.class);
        startService(intent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (serviceBound) {
            unbindService(connection);
            serviceBound = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_REQUEST_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Thank You For Permission", Toast.LENGTH_SHORT).show();
                    // permission was granted, yay!

                } else {

                    Toast.makeText(this, "Can not Continue without Permission", Toast.LENGTH_SHORT).show();


                    //make it so nothing can happen till accepted
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
        }
    }


    public void getBuisness(double lattitude,double longitude) {
        retrofit2.Call<Buisnesses> fullWeatherCall = boundService.BuisnessCall(lattitude,longitude);
        fullWeatherCall.enqueue(new retrofit2.Callback<Buisnesses>() {


            @Override
            public void onResponse(@NonNull Call<Buisnesses> call, @NonNull Response<Buisnesses> response) {
                businesses.addAll(response.body().getBusinesses());
                adapter = new RecyclerAdapter(businesses);

                resultsView.setAdapter(adapter);
            }

            @Override
            public void onFailure(@NonNull Call<Buisnesses> call, @NonNull Throwable t) {
                Toast.makeText(boundService, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            RestCallBoundService.MyBinder myBinder = (MyBinder) service;
            boundService = myBinder.getService();
            serviceBound = true;


        }
    };


}

