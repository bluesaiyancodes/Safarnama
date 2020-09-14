package com.strongties.safarnama.background_tasks;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class preBackgroundTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "Avatar BG";
    FusedLocationProviderClient mFusedLocationClient;

    private Context mContext;

    public preBackgroundTask(Context context) {
        this.mContext = context;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            return !TextUtils.isEmpty(locationProviders);
        }
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
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    assert location != null;

                    String address = getaddres(location);

                    String[] tokens = address.split(",");
                    String[] stateToken = tokens[tokens.length - 2].split(" ");
                    String LocalState = stateToken[1];


                    SharedPreferences pref = mContext.getSharedPreferences("myPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("localState", LocalState);
                    editor.apply();

                    Log.d(TAG, "local Testing-> " + stateToken[1]);

                }
            }
        });

    }

    public String getaddres(Location loc) {

        StringBuffer address = new StringBuffer();
        Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);

            if (addresses.size() > 0)
                System.out.println(addresses.get(0).getLocality());
            Log.d(TAG, "Testing" + addresses.get(0).getLocality());
            address.append(addresses.get(0).getAddressLine(0)).append("\n");

        } catch (IOException e) {
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