package com.strongties.safarnama;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.strongties.safarnama.user_classes.Landmark;
import com.strongties.safarnama.user_classes.LandmarkList;
import com.strongties.safarnama.user_classes.LandmarkMeta;
import com.strongties.safarnama.user_classes.UserFeed;

import java.util.Objects;

public class LandmarkActivity extends AppCompatActivity{
    private static final String TAG = "Landmark->";


    GoogleMap googleMap;
    Context mContext;
    Landmark landmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark);

        mContext = getApplicationContext();

        TextView name = findViewById(R.id.landmark_name);
        TextView state = findViewById(R.id.landmark_state);
        TextView district = findViewById(R.id.landmark_district);
        TextView city = findViewById(R.id.landmark_city);
        TextView type = findViewById(R.id.landmark_type);
        TextView fee = findViewById(R.id.landmark_fee);
        //TextView hours;
        TextView short_desc = findViewById(R.id.landmark_description_shrt);
        TextView long_desc = findViewById(R.id.landmark_description);
        TextView history = findViewById(R.id.landmark_history);
        ImageView img_main = findViewById(R.id.landmark_photo_main);
        ImageView img_1 = findViewById(R.id.landmark_img_1);
        ImageView img_2 = findViewById(R.id.landmark_img_2);
        ImageView img_3 = findViewById(R.id.landmark_img_3);

        Button back_pressed =findViewById(R.id.landmark_go_back);
        Button map_view = findViewById(R.id.landmark_view_on_map);
        Button view_more = findViewById(R.id.landmark_view_more);
        Button add_wish = findViewById(R.id.landmark_add_to_bucket);
        Button add_accomplish = findViewById(R.id.landmark_add_to_accomplish);




        DocumentReference documentReference = FirebaseFirestore.getInstance()
                .collection(getString(R.string.collection_landmarks))
                .document(Objects.requireNonNull(getIntent().getExtras().getString("state")))
                .collection(Objects.requireNonNull(getIntent().getExtras().getString("city")))
                .document(Objects.requireNonNull(getIntent().getExtras().getString("id")));


        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: successfully got the landmark details.");

                    landmark = Objects.requireNonNull(task.getResult()).toObject(Landmark.class);

                    assert landmark != null;
                    name.setText(landmark.getName());
                    state.setText(landmark.getState());
                    district.setText(landmark.getDistrict());
                    city.setText(landmark.getCity());
                    type.setText(landmark.getCategory());
                    fee.setText(landmark.getFee());
                    short_desc.setText(landmark.getShort_desc());
                    long_desc.setText(landmark.getLong_desc());
                    history.setText(landmark.getHistory());



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
            }
        });



        add_wish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                landmarkMeta.setCity(landmark.getCity());
                landmarkMeta.setId(landmark.getId());

                landmarkList.setLandmarkMeta(landmarkMeta);

                bucketRef.set(landmarkList).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(mContext, mContext.getString(R.string.wishlistadd), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        add_accomplish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DocumentReference bucketRef = FirebaseFirestore.getInstance()
                        .collection(mContext.getString(R.string.collection_users))
                        .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                        .collection(mContext.getString(R.string.collection_accomplished_list))
                        .document(landmark.getId());

                LandmarkList landmarkList = new LandmarkList();
                LandmarkMeta landmarkMeta = new LandmarkMeta();
                landmarkMeta.setLandmark(landmark);
                landmarkMeta.setGeoPoint(landmark.getGeo_point());
                landmarkMeta.setState(landmark.getState());
                landmarkMeta.setCity(landmark.getCity());
                landmarkMeta.setId(landmark.getId());

                landmarkList.setLandmarkMeta(landmarkMeta);

                bucketRef.set(landmarkList).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(mContext, mContext.getString(R.string.accomplishlistadd), Toast.LENGTH_SHORT).show();
                        }
                    }
                });




                UserFeed userFeed = new UserFeed();
                userFeed.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                String msgBuilder = mContext.getString(R.string.feed_msg_accomplished) + landmarkMeta.getLandmark().getName();
                msgBuilder += "\n \n" + landmarkMeta.getLandmark().getLong_desc();

                userFeed.setDatacontent(msgBuilder);

                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();

                DocumentReference docRef = FirebaseFirestore.getInstance()
                        .collection(mContext.getString(R.string.collection_feed))
                        .document(mContext.getString(R.string.document_global))
                        .collection(mContext.getString(R.string.collection_posts))
                        .document(ts);


                docRef.set(userFeed).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "Successfully added to Global feed list ");
                        }else{
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
                        if(task.isSuccessful()){
                            //  Toast.makeText(mContext, mContext.getString(R.string.feed_post_success), Toast.LENGTH_SHORT).show();

                            Log.d(TAG, "Successfully added to User feed list ");
                        }else{
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


            }
        });



        back_pressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        map_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        myDialog.dismiss();
                    }
                });

                myDialog.show();


            }
        });

        view_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandmarkActivity.this, placeImagesActivity.class);
                intent.putExtra("img_urls", landmark.getImg_all_url());
                intent.putExtra("name", landmark.getName());
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);



            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_top);
    }

}
