package com.strongties.safarnama.background_tasks;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.strongties.safarnama.MainActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class preBackgroundTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "Pre BG";
    FusedLocationProviderClient mFusedLocationClient;

    private final Context mContext;

    public preBackgroundTask(Context context) {
        this.mContext = context;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;

        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // do some background work here

        if (isLocationEnabled(mContext)) {
            getLastKnownLocation();
        }


        return true;
    }

    @Override
    protected void onPostExecute(final Boolean result) {
        if (this.isCancelled()) {
            return;
        }
    }

    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: called.");


        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);

        try {

            mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Location location = task.getResult();

                    String address = getaddres(location);
                    MainActivity.current_location = location;

                    String[] tokens = address.split(",");
                    String[] stateToken = tokens[tokens.length - 2].split(" ");
                    String LocalState = stateToken[1];
                    String Locality = "";
                    try {
                        Locality = tokens[tokens.length - 3];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        Locality = "Unknown";
                    }


                    SharedPreferences pref = mContext.getSharedPreferences("myPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("localState", LocalState);
                    editor.putString("locality", Locality);
                    editor.apply();

                    Log.d(TAG, "local Testing-> " + stateToken[1]);
                    Log.d(TAG, "local Testing-> " + tokens[tokens.length - 3]);
                }
            });
        } catch (NullPointerException e) {

            LocationCallback locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    Location location = locationResult.getLastLocation();

                    String address = getaddres(location);
                    MainActivity.current_location = location;

                    String[] tokens = address.split(",");
                    String[] stateToken = tokens[tokens.length - 2].split(" ");
                    String LocalState = stateToken[1];


                    SharedPreferences pref = mContext.getSharedPreferences("myPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("localState", LocalState);
                    editor.apply();


                }
            };

            try {
                Looper.prepare();
            } catch (RuntimeException e1) {
                //do nothing
            }

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setNumUpdates(1);
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }


    }

    public String getaddres(Location loc) {

        StringBuilder address = new StringBuilder();
        Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);

            if (addresses.size() > 0) {
                System.out.println(addresses.get(0).getLocality());
                Log.d(TAG, "Testing" + addresses.get(0).getLocality());
                address.append(addresses.get(0).getAddressLine(0)).append("\n");
            }
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }

        String[] tokens = address.toString().split(",");
        Log.d(TAG, "Address -> " + address.toString());
        Log.d(TAG, "Address Local -> " + tokens[1]);
        Log.d(TAG, "Address City -> " + tokens[2]);
        Log.d(TAG, "Address State -> " + tokens[3]);
        Log.d(TAG, "Address State -> " + tokens[tokens.length - 2]);

        return address.toString();
    }

}