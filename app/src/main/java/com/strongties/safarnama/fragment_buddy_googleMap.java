package com.strongties.safarnama;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
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
import com.strongties.safarnama.user_classes.User;
import com.strongties.safarnama.user_classes.UserLocation;
import com.strongties.safarnama.user_classes.UserRelation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class fragment_buddy_googleMap extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    GoogleMap googleMap;

    LocationRequest mLocationRequest;
    Location mLastLocation;
    FusedLocationProviderClient mFusedLocationClient;

    private UserLocation mUserLocation;
    private FirebaseFirestore mDb;

    Context mcontext;

    private User currentuser;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    private static final String TAG = "Map Fragment";
    public static List<Marker> markers;
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@org.jetbrains.annotations.NotNull LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;

                // Place Markers
                removeMarkers();
                addMarkers();


                //Place current location marker
               // LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                //move map camera
              //  googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                if (isLocationEnabled(getContext())) {
                    getUserDetails();
                }
            }
        }
    };


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_buddy_gmap, container, false);

        Button btn_back = root.findViewById(R.id.buddy_gmap_back);
        mcontext = getContext();
        if (!isLocationEnabled(getContext())) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
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

        //initialize marker arraylist
        markers = new ArrayList<>();


        mDb = FirebaseFirestore.getInstance();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getContext()));
        currentuser = ((UserClient) (mcontext.getApplicationContext())).getUser();

        if (isLocationEnabled(getContext())) {
            getUserDetails();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.buddy_gmapview);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppCompatActivity) mcontext).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_bottom)
                        .replace(R.id.fragment_container, new fragment_menu_buddies(), "Menu Buddies").commit();
            }
        });


        return root;
    }


    //Is Called When Map is Ready Show
    @Override
    public void onMapReady(GoogleMap mMap) {

        googleMap = mMap;
        //googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(mcontext, R.raw.mapstyle_night);
                mMap.setMapStyle(style);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                break;
        }

        //addMarkers();


        if (ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
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
        googleMap.setOnInfoWindowClickListener(this);



        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(2000); // One sec interval
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                googleMap.setMyLocationEnabled(true);
                googleMap.setOnInfoWindowClickListener(this);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnInfoWindowClickListener(this);
        }


    }


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
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }


    //calculate dp according to device density

    public int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(getResources().getDisplayMetrics().density * value);
    }


    //custom User Bitmap with Marker

    private Bitmap createUserBitmap(Bitmap resource) {
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(dp(62), dp(76), Bitmap.Config.ARGB_8888);
            result.eraseColor(Color.TRANSPARENT);
            Canvas canvas = new Canvas(result);
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.livepin, null);
            drawable.setBounds(0, 0, dp(62), dp(76));
            drawable.draw(canvas);

            Paint roundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            RectF bitmapRect = new RectF();
            canvas.save();

            //Bitmap bitmap = BitmapFactory.decodeFile(path.toString()); /*generate bitmap here if your image comes from any url*/
            if (resource != null) {
                BitmapShader shader = new BitmapShader(resource, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                Matrix matrix = new Matrix();
                float scale = dp(52) / (float) resource.getWidth();
                matrix.postTranslate(dp(5), dp(5));
                matrix.postScale(scale, scale);
                roundPaint.setShader(shader);
                shader.setLocalMatrix(matrix);
                bitmapRect.set(dp(5), dp(5), dp(52 + 5), dp(52 + 5));
                canvas.drawRoundRect(bitmapRect, dp(26), dp(26), roundPaint);
            }
            canvas.restore();
            try {
                canvas.setBitmap(null);
            } catch (Exception e) {
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }


    private void addMarkers() {

        CollectionReference colRef = mDb
                .collection(mcontext.getString(R.string.collection_relations))
                .document(currentuser.getUser_id())
                .collection(mcontext.getString(R.string.collection_friendlist));

        colRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.e(TAG, "onEvent: Listen failed.", e);
                }

                if (queryDocumentSnapshots != null) {

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        UserRelation userRelation = doc.toObject(UserRelation.class);
                        DocumentReference docRef = mDb
                                .collection(mcontext.getString(R.string.collection_user_locations))
                                .document(userRelation.getUser_id());

                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null) {
                                        final UserLocation userLocation = document.toObject(UserLocation.class);
                                        assert userLocation != null;

                                        final Marker[] marker = new Marker[1];

                                        Glide.with(mcontext)
                                                .asBitmap()
                                                .load(userLocation.getUser().getPhoto())
                                                .into(new CustomTarget<Bitmap>() {
                                                    @Override
                                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                        //imageView.setImageBitmap(resource);
                                                        String pattern1 = " HH:mm";
                                                        String pattern2 = " dd/MM";
                                                        DateFormat df1 = new SimpleDateFormat(pattern1);
                                                        DateFormat df2 = new SimpleDateFormat(pattern2);
                                                        String timestring = df1.format(userLocation.getTimestamp()) + " - " + df2.format(userLocation.getTimestamp());

                                                        marker[0] = googleMap.addMarker(new MarkerOptions()
                                                                .position(new LatLng(userLocation.getGeo_point().getLatitude(), userLocation.getGeo_point().getLongitude()))
                                                                .title(userLocation.getUser().getUsername())
                                                                .snippet("Last Known: " + timestring)
                                                                .icon(BitmapDescriptorFactory.fromBitmap(createUserBitmap(resource))));
                                                    }

                                                    @Override
                                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                                    }
                                                });

                                        if(marker[0] !=null) {
                                            markers.add(marker[0]);
                                        }


                                    } else {
                                        Log.d("LOGGER", "No such document");
                                    }
                                } else {
                                    Log.d("LOGGER", "get failed with ", task.getException());
                                }
                            }
                        });


                    }

                }


            }
        });


    }

    private void removeMarkers() {
        if (markers != null) {
            for (Marker marker : markers) {
                marker.remove();
            }
        }
        assert markers != null;
        markers.clear();
    }


    @Override
    public void onInfoWindowClick(final Marker marker) {


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
                  //  LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                  //  googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
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

}
