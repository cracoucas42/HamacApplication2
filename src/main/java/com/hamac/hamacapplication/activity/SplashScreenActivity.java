package com.hamac.hamacapplication.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hamac.hamacapplication.R;
import com.hamac.hamacapplication.data.Hamac;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SplashScreenActivity extends Activity {
    private static int SPLASH_TIME_OUT = 3000;
//    private LocationManager lm;
    private boolean firstLaunch_flag = true;
    private View splashScreenView;
    private DatabaseReference mDatabase;
    private ValueEventListener mMessageListener;
    private ArrayList<Hamac> hamacList = new ArrayList<Hamac>();
//    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

//        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        splashScreenView = this.findViewById(R.id.splash_screen_view);

        //Get Data Offline persistent
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //Manage FireBase From here and pass HamacList To MapsActivity by Intent

        //Manage PERMISSIONS
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED |
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) == true |
                    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) == true |
                    shouldShowRequestPermissionRationale(Manifest.permission.INTERNET) == true |
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) == true)
                {
                    explain();
                }
                else
                {
                    //Ask for Permission
                    askForPermission();
                    Toast.makeText(getApplicationContext(), "Request PERMISSION", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else
        {
            //REQUEST PERMISSION NOT NEEDED
            Toast.makeText(getApplicationContext(), "PERMISSION OK", Toast.LENGTH_LONG).show();
        }

        //Manage DataBase
        mDatabase = FirebaseDatabase.getInstance().getReference("HAMAC_LIST_ONLINE");

        //Get Data From FireBase DataBase
        ValueEventListener messageListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    Toast.makeText(getBaseContext(), "Read FireBase Before", Toast.LENGTH_SHORT).show();
                    for(DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        Hamac currentHamac = new Hamac(
                                ds.child("name").getValue(String.class),
                                ds.child("id").getValue(String.class),
                                ds.child("description").getValue(String.class),
                                ds.child("lat").getValue(Double.class),
                                ds.child("lng").getValue(Double.class),
                                ds.child("user").getValue(String.class),
                                ds.child("photoUrl_1").getValue(String.class),
                                ds.child("photoUrl_2").getValue(String.class),
                                ds.child("photoUrl_3").getValue(String.class),
                                ds.child("photoUrl_4").getValue(String.class),
                                ds.child("photoUrl_5").getValue(String.class));

                        Log.d("Reading Firebase", "Current Hamac name: " + ds.child("name").getValue(String.class)
                                + " LNG :" + ds.child("lng").getValue(Double.class)
                                + " LAT :" + ds.child("lat").getValue(Double.class));

//
//                        String arrival = ds.child("Arrival").getValue(String.class);
//                        String departure = ds.child("Departure").getValue(String.class);
//                        String time = ds.child("Time").getValue(String.class);
//                        Log.d("TAG", arrival + " / " + departure  + " / " + time);
                        // Get Post object and use the values to update the UI
//                        Hamac currentHamac = ds.child(ds.getKey()).getValue(Hamac.class);

//                        Log.d("Reading Firebase", "Current Hamac : " + ds.getKey());

                        hamacList.add(currentHamac);
                    }
                }
                Toast.makeText(getBaseContext(), "Read FireBase After HamacList SIZE : " + hamacList.size(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.e("Error Reading Firebase", "onCancelled: Failed to read message");
            }
        };
        mDatabase.addValueEventListener(messageListener);
        // copy for removing at onStop()
        mMessageListener = messageListener;

//        Toast.makeText(getApplicationContext(), "Start to delay time...", Toast.LENGTH_LONG).show();
        //Manage Timing
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent i = new Intent(SplashScreenActivity.this, MapsActivity.class);

                i.putExtra("HAMAC_LIST_FROM_ONLINE_DB", hamacList);

                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);

//        Toast.makeText(getApplicationContext(), "Stop to delay time...", Toast.LENGTH_LONG).show();
    }

    private void askForPermission()
    {
        ActivityCompat.requestPermissions(this,
                new String[]
                        {
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.INTERNET,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        },
                1);
    }

    private void explain()
    {
        Snackbar.make(splashScreenView, "Cette permission est nécessaire pour appeler", Snackbar.LENGTH_LONG).setAction("Activer", new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                askForPermission();
            }
        }).show();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        if (mMessageListener != null)
        {
            mDatabase.removeEventListener(mMessageListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == PackageManager.PERMISSION_GRANTED)
        {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

            }
            else
            {
                // Permission was denied. Display an error message.
                Toast.makeText(getApplicationContext(), "PERMISSION WAS NOT GRANTED", Toast.LENGTH_LONG).show();
            }
        }
    }
}
