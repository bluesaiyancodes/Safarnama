package com.strongties.safarnama;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.strongties.safarnama.services.LocationService;
import com.strongties.safarnama.user_classes.User;
import com.strongties.safarnama.user_classes.UserLocation;

import java.util.List;
import java.util.Objects;

public class fragment_menu_googleMap extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    GoogleMap googleMap;

    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;

    private UserLocation mUserLocation;
    private FirebaseFirestore mDb;

    DatabaseHelper dbhelper;
    Context mcontext;

    String req;     //Type of request

    private static final String TAG = "Map Fragment";

    public fragment_menu_googleMap() {
        this.req = "all";
    }
    public fragment_menu_googleMap(String req) {
        this.req = req;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_google_map, container, false);

        mcontext = getContext();
        if(!isLocationEnabled(getContext())){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setTitle(getString(R.string.get_loc_check));
            alertDialogBuilder.setMessage(getString(R.string.get_loc_msg));
            alertDialogBuilder.setIcon(R.drawable.location);
            alertDialogBuilder.setPositiveButton(getString(R.string.give_permission), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            alertDialogBuilder.show();
        }


        mDb = FirebaseFirestore.getInstance();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.gmapview);
        mapFragment.getMapAsync(this);


        Button btn_all = root.findViewById(R.id.menu1_mixed);
        Button btn_new = root.findViewById(R.id.menu1_explore);
        Button btn_wish = root.findViewById(R.id.menu1_wish);
        Button btn_accomplish = root.findViewById(R.id.menu1_accomplish);

        checkLocationPermission();


        btn_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new fragment_menu_googleMap("all"), "Google Map Fragment").commit();

                Toast.makeText(getContext(), getString(R.string.show_all), Toast.LENGTH_SHORT).show();
            }
        });

        btn_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new fragment_menu_googleMap("new"), "Google Map Fragment").commit();

            }
        });

        btn_wish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new fragment_menu_googleMap("wish"), "Google Map Fragment").commit();

            }
        });

        btn_accomplish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new fragment_menu_googleMap("accomplish"), "Google Map Fragment").commit();

            }
        });


        return root;
    }


    //Is Called When Map is Ready Show
    @Override
    public void onMapReady(GoogleMap mMap) {

        googleMap = mMap;
        //googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK){
            case Configuration.UI_MODE_NIGHT_YES:
                MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(mcontext, R.raw.mapstyle_night);
                mMap.setMapStyle(style);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                break;
        }


        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(300000); // Five min interval
        mLocationRequest.setFastestInterval(300000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
                googleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            googleMap.setMyLocationEnabled(true);
        }



        addMarkers();
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnInfoWindowClickListener(this);
        startLocationService();
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@org.jetbrains.annotations.NotNull LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                // MarkerOptions markerOptions = new MarkerOptions();
                // markerOptions.position(latLng);
                // markerOptions.title("Current Position");
                // markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                // mCurrLocationMarker = googleMap.addMarker(markerOptions);

                //move map camera
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                if(isLocationEnabled(getContext())){
                    getUserDetails();
                }
            }
        }
    };




    //Check for Location Permission
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getContext())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    //Permission Dialog Result
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {


                        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                if (task.isSuccessful()) {
                                    Location location = task.getResult();
                                    assert location != null;
                                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                                }
                            }
                        });

                        googleMap.setMyLocationEnabled(true);

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private void addMarkers() {

        dbhelper = new DatabaseHelper(getContext());
        SQLiteDatabase database = dbhelper.getReadableDatabase();

        Cursor cursor;

        if(req.equals("all")){

            // For all requests

            cursor = database.rawQuery("SELECT name, lat, lon, type, visit FROM LANDMARKS", new String[]{});
            if(cursor != null){
                cursor.moveToFirst();
            }else{
                Toast.makeText(getContext(), getString(R.string.show_all_void), Toast.LENGTH_SHORT).show();
            }
            do{
                Log.i(TAG, "count =  " + cursor.getCount());
                String name = cursor.getString(0);
                double lat = cursor.getDouble(1);
                double lon = cursor.getDouble(2);
                String type = cursor.getString(3);

                LatLng place = new LatLng(lat, lon);
                if(cursor.getString(4).equals("notvisited")){
                    googleMap.addMarker(new MarkerOptions().position(place).title(name)
                            .snippet(type + "  "+ getString(R.string.i_circle))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                }else if(cursor.getString(4).equals("tovisit")){
                    googleMap.addMarker(new MarkerOptions().position(place).title(name)
                            .snippet(type + "  "+ getString(R.string.i_circle))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                }else{
                    googleMap.addMarker(new MarkerOptions().position(place).title(name)
                            .snippet(type + "  "+ getString(R.string.i_circle))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                }

            }while (cursor.moveToNext());
        }else if(req.equals("new")){

            // For all new requests

            cursor = database.rawQuery("SELECT name, lat, lon, type FROM LANDMARKS WHERE visit = ?", new String[]{"notvisited"});
            if(cursor != null){
                cursor.moveToFirst();
            }else{
                Toast.makeText(getContext(), getString(R.string.show_new_void), Toast.LENGTH_SHORT).show();
            }

            if(cursor.getCount() > 0){

                Toast.makeText(getContext(), getString(R.string.show_new), Toast.LENGTH_SHORT).show();

                do{
                    String name = cursor.getString(0);
                    double lat = cursor.getDouble(1);
                    double lon = cursor.getDouble(2);
                    String type = cursor.getString(3);

                    LatLng place = new LatLng(lat, lon);
                    googleMap.addMarker(new MarkerOptions().position(place).title(name)
                            .snippet(type + "  "+ getString(R.string.i_circle))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                }while (cursor.moveToNext());

            }else {
                Toast.makeText(getContext(), getString(R.string.show_new_void), Toast.LENGTH_SHORT).show();
            }


        }else if(req.equals("wish")){

            //for wishlist

            cursor = database.rawQuery("SELECT name, lat, lon, type FROM LANDMARKS WHERE visit = ?", new String[]{"tovisit"});
            if(cursor != null){
                cursor.moveToFirst();
            }else{
                Toast.makeText(getContext(), getString(R.string.show_bucket_void), Toast.LENGTH_SHORT).show();
            }

            if(cursor.getCount() > 0){

                Toast.makeText(getContext(), getString(R.string.show_bucket), Toast.LENGTH_SHORT).show();

                do{
                    String name = cursor.getString(0);
                    double lat = cursor.getDouble(1);
                    double lon = cursor.getDouble(2);
                    String type = cursor.getString(3);

                    LatLng place = new LatLng(lat, lon);
                    googleMap.addMarker(new MarkerOptions().position(place).title(name)
                            .snippet(type + "  "+ getString(R.string.i_circle))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                }while (cursor.moveToNext());

            }else{
                Toast.makeText(getContext(), getString(R.string.show_bucket_void), Toast.LENGTH_SHORT).show();
            }


        }else{

            //For accomplished

            cursor = database.rawQuery("SELECT name, lat, lon, type FROM LANDMARKS WHERE visit = ?", new String[]{"visited"});
            if(cursor != null){
                cursor.moveToFirst();
            }else{
                Toast.makeText(getContext(), getString(R.string.show_accomplished_void), Toast.LENGTH_SHORT).show();
            }
            if(cursor.getCount() > 0){

                Toast.makeText(getContext(), getString(R.string.show_accomplished), Toast.LENGTH_SHORT).show();

                do{
                    String name = cursor.getString(0);
                    double lat = cursor.getDouble(1);
                    double lon = cursor.getDouble(2);
                    String type = cursor.getString(3);

                    LatLng place = new LatLng(lat, lon);
                    googleMap.addMarker(new MarkerOptions().position(place).title(name)
                            .snippet(type + "  "+ getString(R.string.i_circle))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                }while (cursor.moveToNext());

            }else {
                Toast.makeText(getContext(), getString(R.string.show_accomplished_void), Toast.LENGTH_SHORT).show();
            }
        }



    }


    @Override
    public void onInfoWindowClick(Marker marker) {

        String lat =  Double.toString(marker.getPosition().latitude);
        String lon = Double.toString(marker.getPosition().longitude);

        //Get Name and Desc from DB
        dbhelper = new DatabaseHelper(getContext());
        SQLiteDatabase database = dbhelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT name, descshort, url, visit, type FROM LANDMARKS where lat = ? and lon = ?",
                new String[]{Double.toString(marker.getPosition().latitude), Double.toString(marker.getPosition().longitude)});

        if(cursor != null){
            cursor.moveToFirst();
        }

        assert cursor != null;
        String name =  cursor.getString(0);
        String desc = cursor.getString(1);
        String url = cursor.getString(2);
        String visit = cursor.getString(3);
        String type = cursor.getString(4);




        Dialog myDialog = new Dialog(mcontext);
        myDialog.setContentView(R.layout.menu1_dialogue_map_marker);
        Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView imageView = myDialog.findViewById(R.id.dialog_marker_image);
        final ProgressBar progressBar = myDialog.findViewById(R.id.dialog_progress);

        TextView tv_name = myDialog.findViewById(R.id.dialog_marker_name);
        TextView tv_type = myDialog.findViewById(R.id.dialog_marker_type);
        TextView tv_desc = myDialog.findViewById(R.id.dialog_marker_desc);

        Button btn = myDialog.findViewById(R.id.dialog_btn);


        tv_name.setText(name);
        tv_type.setText(type);
        tv_desc.setText(desc);


        Glide.with(this).load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });


        myDialog.show();

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


    private void getUserDetails(){
        if(mUserLocation == null){
            mUserLocation = new UserLocation();
            DocumentReference userRef = mDb.collection(mcontext.getString(R.string.collection_users))
                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: successfully set the user client.");
                        User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                        mUserLocation.setUser(user);
                        ((UserClient)(mcontext.getApplicationContext())).setUser(user);
                        getLastKnownLocation();
                    }
                }
            });
        }
        else{
            getLastKnownLocation();
        }
    }

    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: called.");


        if (ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    assert location != null;
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    mUserLocation.setGeo_point(geoPoint);
                    mUserLocation.setTimestamp(null);
                    saveUserLocation();
                }
            }
        });

    }

    private void saveUserLocation(){

        if(mUserLocation != null){
            DocumentReference locationRef = mDb
                    .collection(mcontext.getString(R.string.collection_user_locations))
                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

            locationRef.set(mUserLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "saveUserLocation: \ninserted user location into database." +
                                "\n latitude: " + mUserLocation.getGeo_point().getLatitude() +
                                "\n longitude: " + mUserLocation.getGeo_point().getLongitude());
                    }
                }
            });
        }
    }



    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent serviceIntent = new Intent(mcontext, LocationService.class);
//        this.startService(serviceIntent);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){

                Objects.requireNonNull(getContext()).startForegroundService(serviceIntent);
            }else{
                mcontext.startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) mcontext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.strongties.safarnama.services.LocationService".equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }

}
