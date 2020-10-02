package com.strongties.safarnama;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.strongties.safarnama.background_tasks.MapBackgroundTask;
import com.strongties.safarnama.services.LocationService;
import com.strongties.safarnama.user_classes.Landmark;
import com.strongties.safarnama.user_classes.LandmarkMeta;
import com.strongties.safarnama.user_classes.User;
import com.strongties.safarnama.user_classes.UserLocation;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class fragment_menu_googleMap extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    GoogleMap googleMap;
    View mapView;

    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;

    private UserLocation mUserLocation;
    private FirebaseFirestore mDb;

    DatabaseHelper dbhelper;
    Context mcontext;

    String req;     //Type of request
    //Check for Location Permission
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    String address;

    private static final String TAG = "Map Fragment";
    ProgressBar loading;    //Set the progress bar



    public fragment_menu_googleMap() {
        this.req = "all";
    }

    public fragment_menu_googleMap(String req) {
        this.req = req;
    }

    MapBackgroundTask backgroundTask;
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
                if (isLocationEnabled(getContext())) {
                    getUserDetails();
                }
            }
        }
    };

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                if (context != null) {
                    locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
                }
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_google_map, container, false);

        mcontext = getContext();

        if (!isLocationEnabled(getContext())) {
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

        loading = root.findViewById(R.id.map_view_progress);
        loading.setVisibility(View.VISIBLE);


        mDb = FirebaseFirestore.getInstance();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        checkLocationPermission();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.gmapview);
        assert mapFragment != null;
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);


        ImageView iv_filter_btn = root.findViewById(R.id.menu1_iv_filter);
        LinearLayout layout_filters = root.findViewById(R.id.menu1_filter_layout);

        AtomicReference<Boolean> iv_filter_btn_flag = new AtomicReference<>(false);
        iv_filter_btn.setOnClickListener(view -> {
            if (iv_filter_btn_flag.get()) {
                layout_filters.setVisibility(View.GONE);
                iv_filter_btn.setImageResource(R.drawable.ic_up_arrow);

                iv_filter_btn_flag.set(false);
            } else {
                layout_filters.setVisibility(View.VISIBLE);
                iv_filter_btn.setImageResource(R.drawable.ic_down_arrow);

                iv_filter_btn_flag.set(true);
            }
        });


        ImageView btn_all = root.findViewById(R.id.menu1_mixed_iv);
        ImageView btn_new = root.findViewById(R.id.menu1_explore_iv);
        ImageView btn_wish = root.findViewById(R.id.menu1_wish_iv);
        ImageView btn_accomplish = root.findViewById(R.id.menu1_accomplish_iv);

        getallfriends();

        btn_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  backgroundTask.cancel(true);
                v.startAnimation(new AlphaAnimation(1F, 0.7F));
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new fragment_menu_googleMap("all"), "Google Map Fragment").commit();

                Toast toast = Toast.makeText(getContext(), getString(R.string.show_all), Toast.LENGTH_SHORT);
                toast.getView().setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.dialog_bg_toast_colored));
                TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                toastmsg.setTextColor(Color.WHITE);
                toast.show();
            }
        });

        btn_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  backgroundTask.cancel(true);
                v.startAnimation(new AlphaAnimation(1F, 0.7F));
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new fragment_menu_googleMap("new"), "Google Map Fragment").commit();

                Toast toast = Toast.makeText(getContext(), getString(R.string.show_new), Toast.LENGTH_SHORT);
                toast.getView().setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.dialog_bg_toast_colored));
                TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                toastmsg.setTextColor(Color.WHITE);
                toast.show();

            }
        });

        btn_wish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundTask.cancel(true);
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new fragment_menu_googleMap("wish"), "Google Map Fragment").commit();

                Toast toast = Toast.makeText(getContext(), getString(R.string.show_bucket), Toast.LENGTH_SHORT);
                toast.getView().setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.dialog_bg_toast_colored));
                TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                toastmsg.setTextColor(Color.WHITE);
                toast.show();

            }
        });

        btn_accomplish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundTask.cancel(true);
                v.startAnimation(new AlphaAnimation(1F, 0.7F));
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new fragment_menu_googleMap("accomplish"), "Google Map Fragment").commit();

                Toast toast = Toast.makeText(getContext(), getString(R.string.show_accomplished), Toast.LENGTH_SHORT);
                toast.getView().setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.dialog_bg_toast_colored));
                TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                toastmsg.setTextColor(Color.WHITE);
                toast.show();

            }
        });


        return root;
    }

    private void getallfriends() {

        CollectionReference collRef = FirebaseFirestore.getInstance()
                .collection(getString(R.string.collection_relations))
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection(getString(R.string.collection_friendlist));

        collRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        String user_id = document.getId();
                        if (!MainActivity.FriendList.contains(user_id)) {
                            MainActivity.FriendList.add(user_id);
                        }
                    }
                }
            }
        });

        Log.d(TAG, "FriendList" + MainActivity.FriendList);

    }

    //Is Called When Map is Ready Show
    @Override
    public void onMapReady(GoogleMap mMap) {

        googleMap = mMap;
        //googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
       // googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);

        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }

     /*
        for night Mode
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(mcontext, R.raw.mapstyle_night);
                mMap.setMapStyle(style);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                break;
        }

      */


        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(300000); // Five min interval
        mLocationRequest.setFastestInterval(300000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
                googleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            googleMap.setMyLocationEnabled(true);
        }



        //Add Markers
     //   addMarkers();
        backgroundTask = new MapBackgroundTask(mcontext, googleMap, req);
        backgroundTask.execute();





        if (ContextCompat.checkSelfPermission(mcontext, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mcontext, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.setOnInfoWindowClickListener(this);
            startLocationService();
        } else {
            Toast.makeText(mcontext, R.string.error_permission_map, Toast.LENGTH_LONG).show();
        }


    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(requireContext())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(requireActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    //Permission Dialog Result
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
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

    @Override
    public void onInfoWindowClick(Marker marker) {


        GeoPoint Landmark_geoPoint = new GeoPoint(marker.getPosition().latitude, marker.getPosition().longitude);
        Log.d(TAG, "Landmark Geopoint -> " + Landmark_geoPoint);

        CollectionReference CollRef = mDb
                .collection(getString(R.string.collection_landmarks))
                .document(getString(R.string.document_meta))
                .collection(getString(R.string.collection_all));


        CollRef.whereEqualTo("geoPoint", Landmark_geoPoint).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                assert queryDocumentSnapshots != null;
                for (QueryDocumentSnapshot doc1 : queryDocumentSnapshots) {
                    LandmarkMeta landmarkMeta = doc1.toObject(LandmarkMeta.class);
                    Landmark landmark = landmarkMeta.getLandmark();
                    Log.d(TAG, "Fetched Geopoint -> " + landmark.getGeo_point());
                    String name = landmark.getName();
                    String desc = landmark.getShort_desc();
                    String url = landmark.getImg_url();
                    String type = landmark.getCategory();


                    Log.d(TAG, "Landmark -> " + name);


                    Dialog myDialog = new Dialog(mcontext);
                    myDialog.setContentView(R.layout.menu1_dialogue_map_marker);
                    Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                    ImageView imageView = myDialog.findViewById(R.id.dialog_marker_image);
                    final ProgressBar progressBar = myDialog.findViewById(R.id.dialog_progress);

                    TextView tv_name = myDialog.findViewById(R.id.dialog_marker_name);
                    TextView tv_type = myDialog.findViewById(R.id.dialog_marker_type);
                    TextView tv_desc = myDialog.findViewById(R.id.dialog_marker_desc);

                    Button btn = myDialog.findViewById(R.id.dialog_btn);
                    Button details = myDialog.findViewById(R.id.dialog_btn_details);


                    tv_name.setText(name);
                    tv_type.setText(type);
                    tv_desc.setText(desc);


                    Glide.with(mcontext).load(url)
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
                            v.startAnimation(new AlphaAnimation(1F, 0.7F));
                            myDialog.dismiss();
                        }
                    });

                    details.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            v.startAnimation(new AlphaAnimation(1F, 0.7F));
                            Intent intent = new Intent(mcontext, LandmarkActivity.class);
                            Bundle args = new Bundle();
                            args.putString("state", landmark.getState());
                            args.putString("district", landmark.getDistrict());
                            args.putString("id", landmark.getId());
                            intent.putExtras(args);
                            startActivity(intent);
                            ((Activity) mcontext).overridePendingTransition(R.anim.enter_from_top, R.anim.exit_to_bottom);
                        }
                    });


                    myDialog.show();


                }
            }
        });




        /*
        *
        * Old Code -> Fetching details using sqlite
        *

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
        myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

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

         */

    }

    private void getUserDetails() {
        if (mUserLocation == null) {
            mUserLocation = new UserLocation();
            DocumentReference userRef = mDb.collection(mcontext.getString(R.string.collection_users))
                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: successfully set the user client.");
                        User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                        mUserLocation.setUser(user);
                        ((UserClient) (mcontext.getApplicationContext())).setUser(user);
                        getLastKnownLocation();
                    }
                }
            });
        } else {
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

    private void saveUserLocation() {

        if (mUserLocation != null) {
            DocumentReference locationRef = mDb
                    .collection(mcontext.getString(R.string.collection_user_locations))
                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

            locationRef.set(mUserLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "saveUserLocation: \ninserted user location into database." +
                                "\n latitude: " + mUserLocation.getGeo_point().getLatitude() +
                                "\n longitude: " + mUserLocation.getGeo_point().getLongitude());
                    }
                }
            });
        }
    }


    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent serviceIntent = new Intent(mcontext, LocationService.class);
//        this.startService(serviceIntent);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                requireContext().startForegroundService(serviceIntent);
            } else {
                mcontext.startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) mcontext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.strongties.safarnama.services.LocationService".equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }


}
