package com.strongties.safarnama;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.strongties.safarnama.adapters.RecyclerViewAdaptor_distance_search;
import com.strongties.safarnama.user_classes.RV_DistanceSearch;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class distanceSearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchDistance Activity";


    FusedLocationProviderClient mFusedLocationClient;
    RecyclerViewAdaptor_distance_search recyclerAdapter;
    Double minDist;
    Double maxDist;
    private List<RV_DistanceSearch> list_DistanceSearch;

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }

    public static double distance(double lat1, double lon1, double lat2,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_search);


        Objects.requireNonNull(this.getSupportActionBar()).setTitle("Distance Search");


        Button back = findViewById(R.id.distancesearch_go_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        RecyclerView myrecyclerview = findViewById(R.id.distanceSearch_recyclerview);
        myrecyclerview.setHasFixedSize(true);

        myrecyclerview.setLayoutManager(new LinearLayoutManager(this));
        myrecyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        Log.d(TAG, "Activity Started");


        //Get the extras, convert to double and into meters
        minDist = Double.parseDouble(Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).getString("minDist"))) * 1000;
        maxDist = Double.parseDouble(Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).getString("maxDist"))) * 1000;

        Log.d(TAG, "Distance => Min -> " + minDist);
        Log.d(TAG, "Distance => Max -> " + maxDist);

        list_DistanceSearch = new ArrayList<>();

        if (isLocationEnabled(this)) {
            getLastKnownLocation();
        }


        Log.d(TAG, "List Size => " + list_DistanceSearch.size());
        recyclerAdapter = new RecyclerViewAdaptor_distance_search(this, list_DistanceSearch);

        myrecyclerview.setAdapter(recyclerAdapter);


    }

    public String getaddres(Location loc) {

        StringBuffer address = new StringBuffer();
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);

            if (addresses.size() > 0)
                System.out.println(addresses.get(0).getLocality());
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


    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: called.");


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    assert location != null;
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    String address = getaddres(location);

                    addtoLists(latLng, address);

                }
            }
        });

    }

    private void addtoLists(LatLng current_loc, String address) {

        String[] tokens = address.split(",");
        String[] stateToken = tokens[tokens.length - 2].split(" ");
        String local = stateToken[1];
        Log.d(TAG, "local -> " + stateToken[1]);

        DatabaseHelper dbhelper = new DatabaseHelper(this);
        SQLiteDatabase database = dbhelper.getReadableDatabase();

        Cursor cursor;

        cursor = database.rawQuery("SELECT name, lat, lon, url, type, place_id, district, state, city FROM LANDMARKS", new String[]{});

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            Toast toast = Toast.makeText(this, getString(R.string.error_fetching), Toast.LENGTH_SHORT);
            toast.getView().setBackground(ContextCompat.getDrawable(this, R.drawable.dialog_bg_toast_colored));
            TextView toastmsg = toast.getView().findViewById(android.R.id.message);
            toastmsg.setTextColor(Color.WHITE);
            toast.show();
        }


        //for Double Formatting
        DecimalFormat df = new DecimalFormat("0.00");

        do {
            assert cursor != null;
            String name = cursor.getString(0);
            double place_lat = cursor.getDouble(1);
            double place_lon = cursor.getDouble(2);
            String img_url = cursor.getString(3);
            String type = cursor.getString(4);
            String place_id = cursor.getString(5);
            String district = cursor.getString(6);
            String state = cursor.getString(7);
            String city = cursor.getString(8);


            if (state.equals(local)) {
                double dist = distance(current_loc.latitude, current_loc.longitude, place_lat, place_lon, 0, 0);
                Log.d(TAG, "placeName -> " + name + ", Nearby -> " + dist);
                if (dist >= minDist && dist <= maxDist) {


                    String dist_text = df.format(dist / 1000.0);
                    dist_text += "KM (Approx.)";

                    list_DistanceSearch.add(new RV_DistanceSearch(place_id, name, city, district, type, img_url, dist_text, dist));
                    Log.d(TAG, "Added to List, Name -> " + name);
                }

            }

        } while (cursor.moveToNext());

        cursor.close();


        // Sort the List
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            list_DistanceSearch.sort(Comparator.comparingDouble(RV_DistanceSearch::getDistance));
        }


        recyclerAdapter.notifyDataSetChanged();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_top, R.anim.exit_to_bottom);
    }


}