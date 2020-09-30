package com.strongties.safarnama;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.strongties.safarnama.adapters.RecyclerViewAdaptor_distance_search;
import com.strongties.safarnama.user_classes.RV_DistanceSearch;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.strongties.safarnama.MainActivity.current_location;

public class distanceSearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchDistance Activity";

    RecyclerViewAdaptor_distance_search recyclerAdapter;
    Double minDist;
    Double maxDist;
    private List<RV_DistanceSearch> list_DistanceSearch;


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
        recyclerAdapter = new RecyclerViewAdaptor_distance_search(this, list_DistanceSearch);


        try {
            LatLng latLng = new LatLng(current_location.getLatitude(), current_location.getLongitude());
            addtoLists(latLng);
        } catch (NullPointerException e) {
            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(distanceSearchActivity.this);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    addtoLists(latLng);
                }
            });
        }


        Log.d(TAG, "List Size => " + list_DistanceSearch.size());

        myrecyclerview.setAdapter(recyclerAdapter);
        //recyclerAdapter.notifyDataSetChanged();


    }


    private void addtoLists(LatLng current_loc) {

        SharedPreferences pref = getSharedPreferences("myPrefs", MODE_PRIVATE);

        String local = pref.getString("localState", "Odisha");

        DatabaseHelper dbhelper = new DatabaseHelper(this);
        SQLiteDatabase database = dbhelper.getReadableDatabase();

        Cursor cursor;

        cursor = database.rawQuery("SELECT name, lat, lon, url, type, place_id, district, city FROM LANDMARKS WHERE state = ?", new String[]{local});

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
            String city = cursor.getString(7);


            double dist = distance(current_loc.latitude, current_loc.longitude, place_lat, place_lon, 0, 0);
            Log.d(TAG, "placeName -> " + name + ", Nearby -> " + dist);
            if (dist >= minDist && dist <= maxDist) {


                String dist_text = df.format(dist / 1000.0);
                dist_text += "KM (Approx.)";

                list_DistanceSearch.add(new RV_DistanceSearch(place_id, name, city, district, type, img_url, dist_text, dist));
                Log.d(TAG, "Added to List, Name -> " + name);
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
        overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_top);
    }


}