package com.hamac.hamacapplication.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.hamac.hamacapplication.R;

public class SplashScreenActivity extends Activity {
    private static int SPLASH_TIME_OUT = 3000;
//    private LocationManager lm;
    private boolean firstLaunch_flag = true;
//    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //Get Data Offline persistent
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //Manage FireBase From here and pass HamacList To MapsActivity by Intent



        //Launch of the Location Service
//        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, listener);
//        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, listener);
//
//        Intent myIntent = SplashScreenActivity.this.getIntent();
//        myIntent.putExtra("LOCATION", currentLocation);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent i = new Intent(SplashScreenActivity.this, MapsActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
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
