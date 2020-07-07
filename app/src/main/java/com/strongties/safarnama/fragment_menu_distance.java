package com.strongties.safarnama;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.GeoApiContext;
import com.strongties.safarnama.adapters.RecyclerViewAdaptor_distance_places;

import java.util.ArrayList;
import java.util.Objects;

public class fragment_menu_distance extends Fragment {

    private static final String TAG = "Menu Distance Fragment";

    Context mContext;
    DatabaseHelper dbhelper;

    FusedLocationProviderClient mFusedLocationClient;

    private GeoApiContext geoApiContext = null;
    ArrayList <String> inside_1, img_inside_1, type_inside_1;
    ArrayList <String> inside_5, img_inside_5, type_inside_5;
    ArrayList <String> inside_10, img_inside_10, type_inside_10;
    ArrayList <String> inside_50, img_inside_50, type_inside_50;
    ArrayList <String> inside_100, img_inside_100, type_inside_100;
    ArrayList <String> inside_200, img_inside_200, type_inside_200;
    ArrayList <String> above_200, img_above_200, type_above_200;

    RecyclerView rV_1;
    RecyclerView rV_2;
    RecyclerView rV_3;
    RecyclerView rV_4;
    RecyclerView rV_5;
    RecyclerView rV_6;
    RecyclerView rV_7;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_distance, container, false);

        mContext = getContext();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getContext()));


        if (isLocationEnabled(getContext())) {
        //    User user = ((UserClient) (mContext.getApplicationContext())).getUser();
//            Log.d(TAG, "Current User -> " + user.getUsername());
            getLastKnownLocation();
        }

        if(geoApiContext == null){
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_api_key))
                    .build();
        }

        inside_1 = new ArrayList<>();
        inside_5 = new ArrayList<>();
        inside_10 = new ArrayList<>();
        inside_50 = new ArrayList<>();
        inside_100 = new ArrayList<>();
        inside_200 = new ArrayList<>();
        above_200 = new ArrayList<>();

        img_inside_1 = new ArrayList<>();
        img_inside_5 = new ArrayList<>();
        img_inside_10 = new ArrayList<>();
        img_inside_50 = new ArrayList<>();
        img_inside_100 = new ArrayList<>();
        img_inside_200 = new ArrayList<>();
        img_above_200 = new ArrayList<>();


        type_inside_1 = new ArrayList<>();
        type_inside_5 = new ArrayList<>();
        type_inside_10 = new ArrayList<>();
        type_inside_50 = new ArrayList<>();
        type_inside_100 = new ArrayList<>();
        type_inside_200 = new ArrayList<>();
        type_above_200 = new ArrayList<>();



        rV_1 = root.findViewById(R.id.rv_dist_1);
        rV_2 = root.findViewById(R.id.rv_dist_2);
        rV_3 = root.findViewById(R.id.rv_dist_3);
        rV_4 = root.findViewById(R.id.rv_dist_4);
        rV_5 = root.findViewById(R.id.rv_dist_5);
        rV_6 = root.findViewById(R.id.rv_dist_6);
        rV_7 = root.findViewById(R.id.rv_dist_7);


       // calculateDirections(place);




        ///Log.d(TAG, "Show coordinates: current user: " + currentUserLocation.getGeo_point().getLatitude() +  ", " + currentUserLocation.getGeo_point().getLongitude());
        //currentUserLocation.getGeo_point();


        return root;
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
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    //currentUserLocation.setGeo_point(geoPoint);

                   // LatLng place = new LatLng(20.233721,85.838676);

                    addtoLists(latLng);
                   // calculateDirections(latLng, place);
                   // Double dist = distance(latLng.latitude, place.latitude, latLng.longitude, place.longitude, 0, 0);
                   // Log.d(TAG, "calculateDirections: distance: " + Double.toString(dist));
                }
            }
        });

    }

    private void addtoLists(LatLng current_loc) {

        dbhelper = new DatabaseHelper(getContext());
        SQLiteDatabase database = dbhelper.getReadableDatabase();

        Cursor cursor;

        cursor = database.rawQuery("SELECT name, lat, lon, url, type FROM LANDMARKS", new String[]{});

        if(cursor != null){
            cursor.moveToFirst();
        }else{
            Toast.makeText(getContext(), getString(R.string.error_fetching), Toast.LENGTH_SHORT).show();
        }

        do{
            assert cursor != null;
            String name = cursor.getString(0);
            double place_lat = cursor.getDouble(1);
            double place_lon = cursor.getDouble(2);
            String img_url = cursor.getString(3);
            String type = cursor.getString(4);

            double dist = distance(current_loc.latitude, current_loc.longitude, place_lat, place_lon, 0, 0);
            if(dist <= 1000){
                inside_1.add(name);
                img_inside_1.add(img_url);
                type_inside_1.add(type);
            }else if(dist >100 && dist <= 5000){
                inside_5.add(name);
                img_inside_5.add(img_url);
                type_inside_5.add(type);
            }else if(dist > 5000 && dist <= 10000){
                inside_10.add(name);
                img_inside_10.add(img_url);
                type_inside_10.add(type);
            }else if(dist >10000 && dist <= 50000){
                inside_50.add(name);
                img_inside_50.add(img_url);
                type_inside_50.add(type);
            }else if(dist > 50000 && dist <= 100000){
                inside_100.add(name);
                img_inside_100.add(img_url);
                type_inside_100.add(type);
            }else if(dist > 100000 && dist <= 200000){
                inside_200.add(name);
                img_inside_200.add(img_url);
                type_inside_200.add(type);
            }else {
                above_200.add(name);
                img_above_200.add(img_url);
                type_above_200.add(type);
            }
        }while (cursor.moveToNext());

        initRecyclerView();

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



    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerview");


        LinearLayoutManager layoutManager1 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rV_1.setLayoutManager(layoutManager1);
        RecyclerViewAdaptor_distance_places adapter1 = new RecyclerViewAdaptor_distance_places(mContext, inside_1, img_inside_1, type_inside_1);
        rV_1.setAdapter(adapter1);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rV_2.setLayoutManager(layoutManager2);
        RecyclerViewAdaptor_distance_places adapter2 = new RecyclerViewAdaptor_distance_places(mContext, inside_5, img_inside_5, type_inside_5);
        rV_2.setAdapter(adapter2);

        LinearLayoutManager layoutManager3 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rV_3.setLayoutManager(layoutManager3);
        RecyclerViewAdaptor_distance_places adapter3 = new RecyclerViewAdaptor_distance_places(mContext, inside_10, img_inside_10, type_inside_10);
        rV_3.setAdapter(adapter3);

        LinearLayoutManager layoutManager4 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rV_4.setLayoutManager(layoutManager4);
        RecyclerViewAdaptor_distance_places adapter4 = new RecyclerViewAdaptor_distance_places(mContext, inside_50, img_inside_50, type_inside_50);
        rV_4.setAdapter(adapter4);

        LinearLayoutManager layoutManager5 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rV_5.setLayoutManager(layoutManager5);
        RecyclerViewAdaptor_distance_places adapter5 = new RecyclerViewAdaptor_distance_places(mContext, inside_100, img_inside_100, type_inside_100);
        rV_5.setAdapter(adapter5);

        LinearLayoutManager layoutManager6 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rV_6.setLayoutManager(layoutManager6);
        RecyclerViewAdaptor_distance_places adapter6 = new RecyclerViewAdaptor_distance_places(mContext, inside_200, img_inside_200, type_inside_200);
        rV_6.setAdapter(adapter6);

        LinearLayoutManager layoutManager7 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rV_7.setLayoutManager(layoutManager7);
        RecyclerViewAdaptor_distance_places adapter7 = new RecyclerViewAdaptor_distance_places(mContext, above_200, img_above_200, type_above_200);
        rV_7.setAdapter(adapter7);

    }

}
