package com.strongties.safarnama;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.strongties.safarnama.adapters.RecyclerViewAdaptor_distance_places;
import com.strongties.safarnama.user_classes.RV_Distance;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.strongties.safarnama.MainActivity.current_location;
import static com.strongties.safarnama.MainActivity.list_hot;

public class fragment_menu_distance extends Fragment {

    private static final String TAG = "Menu Distance Fragment";

    Context mContext;
    DatabaseHelper dbhelper;


    private List<RV_Distance> list_distance1;
    private List<RV_Distance> list_distance2;
    private List<RV_Distance> list_distance3;
    private List<RV_Distance> list_distance4;
    private List<RV_Distance> list_distance5;


    RecyclerView rV_hot;
    RecyclerView rV_2;
    RecyclerView rV_3;
    RecyclerView rV_4;
    RecyclerView rV_5;
    RecyclerView rV_6;
    RecyclerView rV_7;

    TextView tv_label_hot;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_distance, container, false);

        mContext = getContext();


        Log.d(TAG, "Fragment Started");


        list_distance1 = new ArrayList<>();
        list_distance2 = new ArrayList<>();
        list_distance3 = new ArrayList<>();
        list_distance4 = new ArrayList<>();
        list_distance5 = new ArrayList<>();


        rV_hot = root.findViewById(R.id.rv_dist_hottest);
        rV_2 = root.findViewById(R.id.rv_dist_2);
        //  rV_3 = root.findViewById(R.id.rv_dist_3);
        rV_4 = root.findViewById(R.id.rv_dist_4);
        rV_5 = root.findViewById(R.id.rv_dist_5);
        rV_6 = root.findViewById(R.id.rv_dist_6);
        rV_7 = root.findViewById(R.id.rv_dist_7);


        tv_label_hot = root.findViewById(R.id.rv_dist_label_hot);


        try {
            LatLng latLng = new LatLng(current_location.getLatitude(), current_location.getLongitude());
            addtoLists(latLng);
        } catch (NullPointerException e) {
            retrieveLocationandAddtoLists();
        }


        // Custom Search

        Button custom_search = root.findViewById(R.id.menu2_custom_search);
        custom_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(new AlphaAnimation(1F, 0.7F));
                //Dialog Initiation
                Dialog myDialog = new Dialog(mContext);
                myDialog.setContentView(R.layout.menu2_dialogue_custom_search);
                Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                EditText tv_min = myDialog.findViewById(R.id.menu2_custom_search_min);
                EditText tv_max = myDialog.findViewById(R.id.menu2_custom_search_max);

                Button search = myDialog.findViewById(R.id.menu2_search_dialog_btn);
                search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(new AlphaAnimation(1F, 0.7F));

                        if (tv_min.getText().toString().matches("")) {
                            Toast toast = Toast.makeText(mContext, "Enter Minimum Distance", Toast.LENGTH_SHORT);
                            toast.getView().setBackground(ContextCompat.getDrawable(mContext, R.drawable.dialog_bg_toast_colored));
                            TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                            toastmsg.setTextColor(Color.WHITE);
                            toast.show();
                            return;
                        } else if (tv_max.getText().toString().matches("")) {
                            Toast toast = Toast.makeText(mContext, "Enter Maximum Distance", Toast.LENGTH_SHORT);
                            toast.getView().setBackground(ContextCompat.getDrawable(mContext, R.drawable.dialog_bg_toast_colored));
                            TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                            toastmsg.setTextColor(Color.WHITE);
                            toast.show();
                            return;
                        }

                        Intent intent = new Intent(mContext, distanceSearchActivity.class);
                        Bundle args = new Bundle();
                        args.putString("minDist", tv_min.getText().toString());
                        args.putString("maxDist", tv_max.getText().toString());
                        intent.putExtras(args);
                        startActivity(intent);
                        ((Activity) mContext).overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_top);

                    }
                });

                myDialog.show();

            }
        });

        return root;
    }


    private void addtoLists(LatLng current_loc) {

        SharedPreferences pref = mContext.getSharedPreferences("myPrefs", MODE_PRIVATE);
        String local = pref.getString("localState", "Odisha");


        dbhelper = new DatabaseHelper(getContext());
        SQLiteDatabase database = dbhelper.getReadableDatabase();

        Cursor cursor;

        cursor = database.rawQuery("SELECT name, lat, lon, url, type, place_id, district,city FROM LANDMARKS WHERE state = ?", new String[]{local});

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            Toast toast = Toast.makeText(getContext(), getString(R.string.error_fetching), Toast.LENGTH_SHORT);
            toast.getView().setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.dialog_bg_toast_colored));
            TextView toastmsg = toast.getView().findViewById(android.R.id.message);
            toastmsg.setTextColor(Color.WHITE);
            toast.show();
        }

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

            // add landmarks in Nearby menu only if they have the same state
            //    Log.d(TAG, "local -> "+ local);
            //   Log.d(TAG, "state -> "+ state);

            double dist = distance(current_loc.latitude, current_loc.longitude, place_lat, place_lon, 0, 0);
            Log.d(TAG, "placeName -> " + name + ", Nearby -> " + dist);
            if (dist <= 1000) {


            } else if (dist > 0 && dist <= 5000) {
                Log.d(TAG, "newDebug -> \nPlace -> " + name);

                list_distance1.add(new RV_Distance(place_id, name, img_url, type, local, district, city, dist, getString(R.string.inside_5)));


            } else if (dist > 5000 && dist <= 40000) {

                list_distance2.add(new RV_Distance(place_id, name, img_url, type, local, district, city, dist, getString(R.string.inside_40)));


            } else if (dist > 40000 && dist <= 100000) {

                list_distance3.add(new RV_Distance(place_id, name, img_url, type, local, district, city, dist, getString(R.string.inside_100)));


            } else if (dist > 100000 && dist <= 200000) {

                list_distance4.add(new RV_Distance(place_id, name, img_url, type, local, district, city, dist, getString(R.string.inside_200)));

            } else if (dist > 200000) {

                Log.d(TAG, "above 200 -> " + name);

                list_distance5.add(new RV_Distance(place_id, name, img_url, type, local, district, city, dist, getString(R.string.above_200)));


            }

        } while (cursor.moveToNext());

        cursor.close();


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


    private void sortLists() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            list_distance1.sort(Comparator.comparingDouble(RV_Distance::getDistance));
            list_distance2.sort(Comparator.comparingDouble(RV_Distance::getDistance));
            list_distance3.sort(Comparator.comparingDouble(RV_Distance::getDistance));
            list_distance4.sort(Comparator.comparingDouble(RV_Distance::getDistance));
            list_distance5.sort(Comparator.comparingDouble(RV_Distance::getDistance));
        }
    }


    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview");
        sortLists();

        if (list_hot.size() == 0) {
            tv_label_hot.setVisibility(View.GONE);
        }

        LinearLayoutManager layoutManagerhot = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rV_hot.setLayoutManager(layoutManagerhot);
        RecyclerViewAdaptor_distance_places adapterhot = new RecyclerViewAdaptor_distance_places(mContext, list_hot);
        rV_hot.setAdapter(adapterhot);


        LinearLayoutManager layoutManager2 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rV_2.setLayoutManager(layoutManager2);
        RecyclerViewAdaptor_distance_places adapter2 = new RecyclerViewAdaptor_distance_places(mContext, list_distance1);
        rV_2.setAdapter(adapter2);

        LinearLayoutManager layoutManager4 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rV_4.setLayoutManager(layoutManager4);
        RecyclerViewAdaptor_distance_places adapter4 = new RecyclerViewAdaptor_distance_places(mContext, list_distance2);
        rV_4.setAdapter(adapter4);

        LinearLayoutManager layoutManager5 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rV_5.setLayoutManager(layoutManager5);
        RecyclerViewAdaptor_distance_places adapter5 = new RecyclerViewAdaptor_distance_places(mContext, list_distance3);
        rV_5.setAdapter(adapter5);

        LinearLayoutManager layoutManager6 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rV_6.setLayoutManager(layoutManager6);
        RecyclerViewAdaptor_distance_places adapter6 = new RecyclerViewAdaptor_distance_places(mContext, list_distance4);
        rV_6.setAdapter(adapter6);

        LinearLayoutManager layoutManager7 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rV_7.setLayoutManager(layoutManager7);
        RecyclerViewAdaptor_distance_places adapter7 = new RecyclerViewAdaptor_distance_places(mContext, list_distance5);
        rV_7.setAdapter(adapter7);

    }

    private void retrieveLocationandAddtoLists() {
        Log.d(TAG, "retrieveLocationandAddtoLists: called.");
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);


        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast toast = Toast.makeText(mContext, "Location Permission Denied", Toast.LENGTH_SHORT);
            toast.getView().setBackground(ContextCompat.getDrawable(mContext, R.drawable.dialog_bg_toast_colored));
            TextView toastmsg = toast.getView().findViewById(android.R.id.message);
            toastmsg.setTextColor(Color.WHITE);
            toast.show();
        }

        mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Location location = task.getResult();
                assert location != null;

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                addtoLists(latLng);
                //Log.d(TAG, "Location Check 1->" + location.toString());

            }
        });

//        Log.d(TAG, "Location Check 2->" + locationObject.getLocation().toString());

    }

}
