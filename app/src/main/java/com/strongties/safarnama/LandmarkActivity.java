package com.strongties.safarnama;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.strongties.safarnama.user_classes.Landmark;
import com.strongties.safarnama.user_classes.LandmarkList;
import com.strongties.safarnama.user_classes.LandmarkMeta;
import com.strongties.safarnama.user_classes.LandmarkStat;
import com.strongties.safarnama.user_classes.UserFeed;

import java.text.DecimalFormat;
import java.util.Objects;

public class LandmarkActivity extends AppCompatActivity {
    private static final String TAG = "Landmark->";

    FusedLocationProviderClient mFusedLocationClient;


    GoogleMap googleMap;
    Context mContext;
    Landmark landmark;
    Double calDistance;

    TextView dist;

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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_top);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark);

        mContext = getApplicationContext();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(mContext));

        TextView name = findViewById(R.id.landmark_name);
        TextView state = findViewById(R.id.landmark_state);
        TextView district = findViewById(R.id.landmark_district);
        TextView city = findViewById(R.id.landmark_city);
        TextView type = findViewById(R.id.landmark_type);
        dist = findViewById(R.id.landmark_distance_val);
        //TextView hours;
        TextView short_desc = findViewById(R.id.landmark_description_shrt);
        TextView long_desc = findViewById(R.id.landmark_description);
        TextView history = findViewById(R.id.landmark_history);
        ImageView category_img = findViewById(R.id.image_category);
        ImageView img_main = findViewById(R.id.landmark_photo_main);
        ImageView img_1 = findViewById(R.id.landmark_img_1);
        ImageView img_2 = findViewById(R.id.landmark_img_2);
        ImageView img_3 = findViewById(R.id.landmark_img_3);

        Button back_pressed =findViewById(R.id.landmark_go_back);
        Button map_view = findViewById(R.id.landmark_view_on_map);
        Button view_more = findViewById(R.id.landmark_view_more);
        Button add_wish = findViewById(R.id.landmark_add_to_bucket);
        Button add_accomplish = findViewById(R.id.landmark_add_to_accomplish);


        ProgressDialog mProgressDialog = ProgressDialog.show(LandmarkActivity.this, "Loading", "Fetching Information from Server");
        mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside


        DocumentReference documentReference = FirebaseFirestore.getInstance()
                .collection(getString(R.string.collection_landmarks))
                .document(Objects.requireNonNull(getIntent().getExtras().getString("state")))
                .collection(Objects.requireNonNull(getIntent().getExtras().getString("district")))
                .document(Objects.requireNonNull(getIntent().getExtras().getString("id")));

        Log.d(TAG, "Doc Ref -> " + documentReference.getPath());


        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: successfully got the landmark details.");

                    LandmarkMeta landmarkMeta = task.getResult().toObject(LandmarkMeta.class);
                    assert landmarkMeta != null;
                    landmark = landmarkMeta.getLandmark();
                    assert landmark != null;
                    //Check if the landmark is in user's bucketlist

                    DocumentReference documentRef = FirebaseFirestore.getInstance()
                            .collection(mContext.getString(R.string.collection_users))
                            .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                            .collection(mContext.getString(R.string.collection_bucket_list))
                            .document(landmark.getId());

                    documentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (task.isSuccessful() && documentSnapshot.exists()) {
                                Button btn_add = findViewById(R.id.landmark_add_to_bucket);
                                btn_add.setText(R.string.wishlistin);
                                btn_add.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bucketlist_white, 0, 0, 0);
                                btn_add.setBackgroundResource(R.drawable.dialog_bg_colored);
                                btn_add.setTextColor(getResources().getColor(R.color.white));
                            }
                        }
                    });

                    //Check if the landmark is in user's Accomplished List

                    documentRef = FirebaseFirestore.getInstance()
                            .collection(mContext.getString(R.string.collection_users))
                            .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                            .collection(mContext.getString(R.string.collection_accomplished_list))
                            .document(landmark.getId());

                    documentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (task.isSuccessful() && documentSnapshot.exists()) {
                                Log.d(TAG, "Accomplished -> " + documentSnapshot);
                                Button btn_add = findViewById(R.id.landmark_add_to_accomplish);
                                btn_add.setText(R.string.accomplishlistin);
                                btn_add.setCompoundDrawablesWithIntrinsicBounds(R.drawable.accomplished_white, 0, 0, 0);
                                btn_add.setBackgroundResource(R.drawable.dialog_bg_colored_cyan);
                                btn_add.setTextColor(getResources().getColor(R.color.white));
                            }
                        }
                    });


                    name.setText(landmark.getName());
                    state.setText(landmark.getState());
                    district.setText(landmark.getDistrict());
                    city.setText(landmark.getCity());
                    type.setText(landmark.getCategory());


                    if (getIntent().hasExtra("distance")) {
                        calDistance = Objects.requireNonNull(getIntent().getExtras()).getDouble("distance");
                        DecimalFormat df = new DecimalFormat("0.00");
                        String dist_text = "0";
                        if (calDistance != null) {
                            dist_text = df.format(calDistance / 1000.0);
                        }
                        dist_text += "KM";
                        dist.setText(dist_text);

                    } else {
                        if (isLocationEnabled(mContext)) {
                            getLastKnownLocation(landmark.getGeo_point());
                        }
                    }


                    short_desc.setText(landmark.getShort_desc().replace("\"", ""));
                    long_desc.setText(landmark.getLong_desc().replace("\"", ""));


                    switch (landmark.getCategory()) {
                        case "Dams & Water Reservoirs":
                            category_img.setImageResource(R.drawable.category_dams);
                            break;
                        case "Education & History":
                            category_img.setImageResource(R.drawable.category_education_and_history);
                            break;
                        case "Garden & Parks":
                            category_img.setImageResource(R.drawable.category_garden_and_parks);
                            break;
                        case "Hills & Caves":
                            category_img.setImageResource(R.drawable.category_hills_and_caves);
                            break;
                        case "Iconic Places":
                            category_img.setImageResource(R.drawable.category_historical_monuments);
                            break;
                        case "Nature & Wildlife":
                            category_img.setImageResource(R.drawable.category_nature_and_wildlife);
                            break;
                        case "Port & Sea Beach":
                            category_img.setImageResource(R.drawable.category_port_and_sea_beach);
                            break;
                        case "Religious Sites":
                            category_img.setImageResource(R.drawable.category_religious);
                            break;
                        case "Waterbodies":
                            category_img.setImageResource(R.drawable.category_waterfalls);
                            break;
                        case "Zoos & Reserves":
                            category_img.setImageResource(R.drawable.category_zoo);
                            break;
                        default:
                            category_img.setImageResource(R.drawable.add_icon);
                            break;
                    }


                    String[] long_desc_tokens = landmark.getHistory().split(":");
                    Log.d(TAG, "Token[0] -> " + long_desc_tokens[0]);
                    StringBuilder htmlString = new StringBuilder();
                    int count = 0;
                    for (String token : long_desc_tokens) {
                        count++;
                        token = token.replace("\"\"", "\"");
                        if (count == 1) {
                            if (token.charAt(0) == '"') {
                                token=token.substring(1);
                            }
                        }
                        if(count<long_desc_tokens.length){
                            htmlString.append("&#8226;  ").append(token).append("<br/> <br/>");
                        }else {
                            if(token.charAt(token.length()-1) == '"'){
                                token=token.substring(0, token.length()-1);
                            }else if(token.charAt(token.length()-2) == '"'){
                                token=token.substring(0, token.length()-2);
                            }
                            htmlString.append("&#8226;  ").append(token).append("<br/>");
                        }

                    }
                    history.setText(Html.fromHtml(htmlString.toString()));



                    Glide.with(mContext)
                            .load(landmark.getImg_url())
                            .centerCrop()
                            .placeholder(R.drawable.loading_image)
                            //.apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                            .into(img_main);

                    String[] imgs = landmark.getImg_all_url().split(" ");

                    try {

                        Glide.with(mContext)
                                .load(imgs[0])
                                .centerCrop()
                                .placeholder(R.drawable.loading_image)
                                //.apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                                .into(img_1);

                        Glide.with(mContext)
                                .load(imgs[1])
                                .centerCrop()
                                .placeholder(R.drawable.loading_image)
                                // .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                                .into(img_2);

                        Glide.with(mContext)
                                .load(imgs[2])
                                .centerCrop()
                                .placeholder(R.drawable.loading_image)
                                //.apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                                .into(img_3);

                        img_1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                v.startAnimation(new AlphaAnimation(1F, 0.7F));
                                Intent intent = new Intent(LandmarkActivity.this, imageViewActivity.class);
                                intent.putExtra("imageUrl", imgs[0]);
                                intent.putExtra("name", landmark.getName());
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                            }
                        });

                        img_2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                v.startAnimation(new AlphaAnimation(1F, 0.7F));
                                Intent intent = new Intent(LandmarkActivity.this, imageViewActivity.class);
                                intent.putExtra("imageUrl", imgs[1]);
                                intent.putExtra("name", landmark.getName());
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                            }
                        });

                        img_3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                v.startAnimation(new AlphaAnimation(1F, 0.7F));
                                Intent intent = new Intent(LandmarkActivity.this, imageViewActivity.class);
                                intent.putExtra("imageUrl", imgs[2]);
                                intent.putExtra("name", landmark.getName());
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                            }
                        });





                    }catch (ArrayIndexOutOfBoundsException e){
                        Log.d(TAG, "Images are not present");
                    }

                }
                mProgressDialog.dismiss();
            }
        });


        add_wish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(new AlphaAnimation(1F, 0.7F));
                Button btn_bucket = findViewById(R.id.landmark_add_to_bucket);

                String btn_state = btn_bucket.getText().toString();
                if (btn_state.equals(getString(R.string.wishlistin))) {
                    new AlertDialog.Builder(LandmarkActivity.this)
                            .setTitle(getString(R.string.confirm))
                            .setMessage(getString(R.string.wishlistremove_confirmation_msg))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    bucketlist_remove();
                                    btn_bucket.setText(R.string.BucketList);
                                    btn_bucket.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bucketlistadd, 0, 0, 0);
                                    btn_bucket.setBackgroundResource(R.drawable.dialog_bg);
                                    btn_bucket.setTextColor(getResources().getColor(R.color.textItemMenu));
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(R.drawable.app_main_icon)
                            .show();
                } else {
                    bucketlist_add();
                    btn_bucket.setText(R.string.wishlistin);
                    btn_bucket.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bucketlist_white, 0, 0, 0);
                    btn_bucket.setBackgroundResource(R.drawable.dialog_bg_colored);
                    btn_bucket.setTextColor(getResources().getColor(R.color.white));

                    Button btn_accomplish = findViewById(R.id.landmark_add_to_accomplish);
                    if (btn_accomplish.getText().toString().equals(getString(R.string.accomplishlistin))) {
                        btn_accomplish.setText(R.string.Accomplished);
                        btn_accomplish.setCompoundDrawablesWithIntrinsicBounds(R.drawable.accomplished_add, 0, 0, 0);
                        btn_accomplish.setBackgroundResource(R.drawable.dialog_bg);
                        btn_accomplish.setTextColor(getResources().getColor(R.color.textItemMenu));
                    }
                }
            }
        });

        add_accomplish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(new AlphaAnimation(1F, 0.7F));
                Button btn_accomplish = findViewById(R.id.landmark_add_to_accomplish);
                String btn_state = btn_accomplish.getText().toString();
                if (btn_state.equals(getString(R.string.accomplishlistin))) {
                    new AlertDialog.Builder(LandmarkActivity.this)
                            .setTitle(getString(R.string.confirm))
                            .setMessage(getString(R.string.accomplishlistremove_confirmation_msg))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    accomplish_remove();
                                    btn_accomplish.setText(R.string.Accomplished);
                                    btn_accomplish.setCompoundDrawablesWithIntrinsicBounds(R.drawable.accomplished_add, 0, 0, 0);
                                    btn_accomplish.setBackgroundResource(R.drawable.dialog_bg);
                                    btn_accomplish.setTextColor(getResources().getColor(R.color.textItemMenu));
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(R.drawable.app_main_icon)
                            .show();
                } else {
                    accomplish_add();
                    btn_accomplish.setText(R.string.accomplishlistin);
                    btn_accomplish.setCompoundDrawablesWithIntrinsicBounds(R.drawable.accomplished_white, 0, 0, 0);
                    btn_accomplish.setBackgroundResource(R.drawable.dialog_bg_colored_cyan);
                    btn_accomplish.setTextColor(getResources().getColor(R.color.white));

                    Button btn_bucket = findViewById(R.id.landmark_add_to_bucket);
                    if (btn_bucket.getText().toString().equals(getString(R.string.wishlistin))) {
                        btn_bucket.setText(R.string.BucketList);
                        btn_bucket.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bucketlistadd, 0, 0, 0);
                        btn_bucket.setBackgroundResource(R.drawable.dialog_bg);
                        btn_bucket.setTextColor(getResources().getColor(R.color.textItemMenu));
                    }
                }
            }
        });


        back_pressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(new AlphaAnimation(1F, 0.7F));
                onBackPressed();
            }
        });

        map_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(new AlphaAnimation(1F, 0.7F));
                Dialog myDialog = new Dialog(LandmarkActivity.this);
                myDialog.setContentView(R.layout.dialog_map_view);
                Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


                MapView mMapView = myDialog.findViewById(R.id.dialog_mapview);
                MapsInitializer.initialize(LandmarkActivity.this);

                mMapView.onCreate(myDialog.onSaveInstanceState());
                mMapView.onResume();


                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(final GoogleMap googleMap) {

                        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                            case Configuration.UI_MODE_NIGHT_YES:
                                MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(LandmarkActivity.this, R.raw.mapstyle_night);
                                googleMap.setMapStyle(style);
                                break;
                            case Configuration.UI_MODE_NIGHT_NO:
                                break;
                        }


                        LatLng latLng = new LatLng(landmark.getGeo_point().getLatitude(), landmark.getGeo_point().getLongitude());
                        googleMap.addMarker(new MarkerOptions().position(latLng).title(landmark.getName()));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        googleMap.getUiSettings().setZoomControlsEnabled(true);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                    }
                });


                Button dialogButton = myDialog.findViewById(R.id.dialog_map_back);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(new AlphaAnimation(1F, 0.7F));
                        myDialog.dismiss();
                    }
                });

                myDialog.show();


            }
        });

        view_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(new AlphaAnimation(1F, 0.7F));
                Intent intent = new Intent(LandmarkActivity.this, placeImagesActivity.class);
                intent.putExtra("img_urls", landmark.getImg_all_url());
                intent.putExtra("name", landmark.getName());
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);


            }
        });

    }

    private void getLastKnownLocation(GeoPoint geoPoint) {
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

                    calDistance = distance(location.getLatitude(), location.getLongitude(), geoPoint.getLatitude(), geoPoint.getLongitude(), 0, 0);

                    DecimalFormat df = new DecimalFormat("0.00");
                    String dist_text = "0";
                    if (calDistance != null) {
                        dist_text = df.format(calDistance / 1000.0);
                    }
                    dist_text += "KM";
                    dist.setText(dist_text);
                }
            }
        });

    }

    private void bucketlist_add() {
        DocumentReference bucketRef = FirebaseFirestore.getInstance()
                .collection(mContext.getString(R.string.collection_users))
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection(mContext.getString(R.string.collection_bucket_list))
                .document(landmark.getId());

        LandmarkList landmarkList = new LandmarkList();
        LandmarkMeta landmarkMeta = new LandmarkMeta();
        landmarkMeta.setLandmark(landmark);
        landmarkMeta.setGeoPoint(landmark.getGeo_point());
        landmarkMeta.setState(landmark.getState());
        landmarkMeta.setdistrict(landmark.getDistrict());
        landmarkMeta.setId(landmark.getId());

        landmarkList.setLandmarkMeta(landmarkMeta);

        bucketRef.set(landmarkList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // add to log
                }
            }
        });

        bucketRef = FirebaseFirestore.getInstance()
                .collection(mContext.getString(R.string.collection_users))
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection(mContext.getString(R.string.collection_accomplished_list))
                .document(landmark.getId());

        bucketRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast toast = Toast.makeText(mContext, mContext.getString(R.string.wishlistadd), Toast.LENGTH_SHORT);
                    toast.getView().setBackground(ContextCompat.getDrawable(LandmarkActivity.this, R.drawable.dialog_bg_toast_colored));
                    TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                    toastmsg.setTextColor(Color.WHITE);
                    toast.show();

                }
            }
        });

        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection(mContext.getString(R.string.collection_stats))
                .document(mContext.getString(R.string.collection_landmarks))
                .collection(mContext.getString(R.string.collection_lists))
                .document(mContext.getString(R.string.document_bucketlist))
                .collection(mContext.getString(R.string.document_meta))
                .document(landmarkMeta.getId());

        DocumentReference docRef1 = FirebaseFirestore.getInstance()
                .collection(mContext.getString(R.string.collection_stats))
                .document(mContext.getString(R.string.collection_landmarks))
                .collection(mContext.getString(R.string.collection_lists))
                .document(mContext.getString(R.string.document_bucketlist))
                .collection(landmarkMeta.getState())
                .document(landmarkMeta.getId());


        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    LandmarkStat landmarkStat = documentSnapshot.toObject(LandmarkStat.class);
                    assert landmarkStat != null;
                    landmarkStat.setLandmarkCounter(landmarkStat.getLandmarkCounter() + 1);

                    DocumentReference documentReference = FirebaseFirestore.getInstance()
                            .collection(mContext.getString(R.string.collection_stats))
                            .document(mContext.getString(R.string.collection_landmarks))
                            .collection(mContext.getString(R.string.collection_lists))
                            .document(mContext.getString(R.string.document_bucketlist))
                            .collection(mContext.getString(R.string.document_meta))
                            .document(landmarkMeta.getId());

                    documentReference.set(landmarkStat).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //do nothing
                        }
                    });

                    docRef1.set(landmarkStat).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //do nothing
                        }
                    });

                } else {
                    LandmarkStat landmarkStat = new LandmarkStat();
                    landmarkStat.setLandmark(landmarkMeta.getLandmark());
                    landmarkStat.setLandmarkCounter(1);
                    DocumentReference documentReference = FirebaseFirestore.getInstance()
                            .collection(mContext.getString(R.string.collection_stats))
                            .document(mContext.getString(R.string.collection_landmarks))
                            .collection(mContext.getString(R.string.collection_lists))
                            .document(mContext.getString(R.string.document_bucketlist))
                            .collection(mContext.getString(R.string.document_meta))
                            .document(landmarkMeta.getId());

                    documentReference.set(landmarkStat).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //do nothing
                        }
                    });
                    docRef1.set(landmarkStat).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //do nothing
                        }
                    });
                }
            }
        });



    }

    private void bucketlist_remove() {
        DocumentReference bucketRef = FirebaseFirestore.getInstance()
                .collection(mContext.getString(R.string.collection_users))
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection(mContext.getString(R.string.collection_bucket_list))
                .document(landmark.getId());

        bucketRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast toast = Toast.makeText(mContext, mContext.getString(R.string.wishlistremove), Toast.LENGTH_SHORT);
                    toast.getView().setBackground(ContextCompat.getDrawable(LandmarkActivity.this, R.drawable.dialog_bg_toast_colored));
                    TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                    toastmsg.setTextColor(Color.WHITE);
                    toast.show();
                }
            }
        });

    }

    private void accomplish_add() {
        DocumentReference accomplishRef = FirebaseFirestore.getInstance()
                .collection(mContext.getString(R.string.collection_users))
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection(mContext.getString(R.string.collection_accomplished_list))
                .document(landmark.getId());

        LandmarkList landmarkList = new LandmarkList();
        LandmarkMeta landmarkMeta = new LandmarkMeta();
        landmarkMeta.setLandmark(landmark);
        landmarkMeta.setGeoPoint(landmark.getGeo_point());
        landmarkMeta.setState(landmark.getState());
        landmarkMeta.setdistrict(landmark.getDistrict());
        landmarkMeta.setId(landmark.getId());

        landmarkList.setLandmarkMeta(landmarkMeta);

        accomplishRef.set(landmarkList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

        accomplishRef = FirebaseFirestore.getInstance()
                .collection(mContext.getString(R.string.collection_users))
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection(mContext.getString(R.string.collection_bucket_list))
                .document(landmark.getId());

        accomplishRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast toast = Toast.makeText(mContext, mContext.getString(R.string.accomplishlistadd), Toast.LENGTH_SHORT);
                    toast.getView().setBackground(ContextCompat.getDrawable(LandmarkActivity.this, R.drawable.dialog_bg_toast_colored));
                    TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                    toastmsg.setTextColor(Color.WHITE);
                    toast.show();
                }
            }
        });


        UserFeed userFeed = new UserFeed();
        userFeed.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        String msgBuilder = mContext.getString(R.string.feed_msg_accomplished) + landmarkMeta.getLandmark().getName();
        msgBuilder += "\n \n" + landmarkMeta.getLandmark().getShort_desc();

        userFeed.setDatacontent(msgBuilder);

        userFeed.setType(mContext.getString(R.string.public_feed));
        userFeed.setImgIncluded(Boolean.TRUE);
        userFeed.setImgUrl(landmarkMeta.getLandmark().getImg_url());
        userFeed.setLandmarkIncluded(Boolean.TRUE);
        userFeed.setLandmarkId(landmarkMeta.getId());


        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();

        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection(mContext.getString(R.string.collection_feed))
                .document(mContext.getString(R.string.document_global))
                .collection(mContext.getString(R.string.collection_posts))
                .document(ts);


        docRef.set(userFeed).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Successfully added to Global feed list ");
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });

        docRef = FirebaseFirestore.getInstance()
                .collection(mContext.getString(R.string.collection_feed))
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection(mContext.getString(R.string.collection_posts))
                .document(ts);

        docRef.set(userFeed).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //  Toast.makeText(mContext, mContext.getString(R.string.feed_post_success), Toast.LENGTH_SHORT).show();

                    Log.d(TAG, "Successfully added to User feed list ");
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });

        docRef = FirebaseFirestore.getInstance()
                .collection(mContext.getString(R.string.collection_stats))
                .document(mContext.getString(R.string.collection_landmarks))
                .collection(mContext.getString(R.string.collection_lists))
                .document(mContext.getString(R.string.document_accomplished))
                .collection(mContext.getString(R.string.document_meta))
                .document(landmarkMeta.getId());

        DocumentReference docRef1 = FirebaseFirestore.getInstance()
                .collection(mContext.getString(R.string.collection_stats))
                .document(mContext.getString(R.string.collection_landmarks))
                .collection(mContext.getString(R.string.collection_lists))
                .document(mContext.getString(R.string.document_accomplished))
                .collection(landmarkMeta.getState())
                .document(landmarkMeta.getId());


        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    LandmarkStat landmarkStat = documentSnapshot.toObject(LandmarkStat.class);
                    assert landmarkStat != null;
                    landmarkStat.setLandmarkCounter(landmarkStat.getLandmarkCounter() + 1);

                    DocumentReference documentReference = FirebaseFirestore.getInstance()
                            .collection(mContext.getString(R.string.collection_stats))
                            .document(mContext.getString(R.string.collection_landmarks))
                            .collection(mContext.getString(R.string.collection_lists))
                            .document(mContext.getString(R.string.document_accomplished))
                            .collection(mContext.getString(R.string.document_meta))
                            .document(landmarkMeta.getId());

                    documentReference.set(landmarkStat).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //do nothing
                        }
                    });

                    docRef1.set(landmarkStat).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //do nothing
                        }
                    });

                } else {
                    LandmarkStat landmarkStat = new LandmarkStat();
                    landmarkStat.setLandmark(landmarkMeta.getLandmark());
                    landmarkStat.setLandmarkCounter(1);
                    DocumentReference documentReference = FirebaseFirestore.getInstance()
                            .collection(mContext.getString(R.string.collection_stats))
                            .document(mContext.getString(R.string.collection_landmarks))
                            .collection(mContext.getString(R.string.collection_lists))
                            .document(mContext.getString(R.string.document_accomplished))
                            .collection(mContext.getString(R.string.document_meta))
                            .document(landmarkMeta.getId());

                    documentReference.set(landmarkStat).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //do nothing
                        }
                    });
                    docRef1.set(landmarkStat).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //do nothing
                        }
                    });
                }
            }
        });


    }

    private void accomplish_remove() {
        DocumentReference accomplishRef = FirebaseFirestore.getInstance()
                .collection(mContext.getString(R.string.collection_users))
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection(mContext.getString(R.string.collection_accomplished_list))
                .document(landmark.getId());

        accomplishRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast toast = Toast.makeText(mContext, mContext.getString(R.string.accomplishlistremove), Toast.LENGTH_SHORT);
                    toast.getView().setBackground(ContextCompat.getDrawable(LandmarkActivity.this, R.drawable.dialog_bg_toast_colored));
                    TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                    toastmsg.setTextColor(Color.WHITE);
                    toast.show();
                }
            }
        });
    }

}
