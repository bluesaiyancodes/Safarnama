package com.strongties.safarnama.background_tasks;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.strongties.safarnama.DatabaseHelper;
import com.strongties.safarnama.MainActivity;
import com.strongties.safarnama.R;
import com.strongties.safarnama.user_classes.LandmarkList;
import com.strongties.safarnama.user_classes.LandmarkStat;
import com.strongties.safarnama.user_classes.RV_Distance;
import com.strongties.safarnama.user_classes.UserRelation;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.strongties.safarnama.MainActivity.RequestList;
import static com.strongties.safarnama.MainActivity.accomplish_type_count;
import static com.strongties.safarnama.MainActivity.accomplished_id_list;
import static com.strongties.safarnama.MainActivity.accomplished_list;
import static com.strongties.safarnama.MainActivity.bucket_id_list;
import static com.strongties.safarnama.MainActivity.bucket_list;
import static com.strongties.safarnama.MainActivity.bucket_type_count;
import static com.strongties.safarnama.MainActivity.current_location;
import static com.strongties.safarnama.MainActivity.list_hot;
import static com.strongties.safarnama.MainActivity.places_id_list;
import static com.strongties.safarnama.MainActivity.places_list;

public class OtherBackgroundTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "Other BG";
    FusedLocationProviderClient mFusedLocationClient;

    private Context mContext;

    public OtherBackgroundTask(Context context) {
        this.mContext = context;
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
    protected void onPostExecute(final Boolean result) {
        if (this.isCancelled()) {
            return;
        }
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);

        getallfriends();
        getallrequested();


        DatabaseHelper dbhelper = new DatabaseHelper(mContext);
        SQLiteDatabase database = dbhelper.getReadableDatabase();

        Cursor cursor;

        cursor = database.rawQuery("SELECT name, place_id FROM LANDMARKS", new String[]{});

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.error_fetching), Toast.LENGTH_SHORT).show();
        }
        do {
            assert cursor != null;
            places_list.add(cursor.getString(0));
            places_id_list.add(cursor.getString(1));
        } while (cursor.moveToNext());

        cursor.close();

        CollectionReference collRef = FirebaseFirestore.getInstance()
                .collection(mContext.getString(R.string.collection_relations))
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection(mContext.getString(R.string.collection_requestlist));

        collRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    assert querySnapshot != null;
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        UserRelation relation = document.toObject(UserRelation.class);
                        assert relation != null;
                        RequestList.add(relation.getUser_id());
                        Log.d(TAG, "Request List Added");
                    }
                }
            }
        });

        CollectionReference bucketRef = FirebaseFirestore.getInstance()
                .collection(mContext.getString(R.string.collection_users))
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection(mContext.getString(R.string.collection_bucket_list));

        bucketRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    assert querySnapshot != null;
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        LandmarkList landmarkList = document.toObject(LandmarkList.class);
                        assert landmarkList != null;
                        bucket_id_list.add(landmarkList.getLandmarkMeta().getId());
                        bucket_list.add(landmarkList.getLandmarkMeta().getLandmark().getName());
                        Log.d(TAG, "Bucket List Added");

                        Integer count = 0;
                        switch (landmarkList.getLandmarkMeta().getLandmark().getCategory()) {
                            case "Dams & Water Reservoirs":
                                count = bucket_type_count.get(mContext.getString(R.string.category1));
                                if (count == null) {
                                    bucket_type_count.put(mContext.getString(R.string.category1), 1);
                                }
                                bucket_type_count.put(mContext.getString(R.string.category1), count + 1);
                                break;
                            case "Education & History":
                                count = bucket_type_count.get(mContext.getString(R.string.category2));
                                if (count == null) {
                                    bucket_type_count.put(mContext.getString(R.string.category2), 1);
                                }
                                bucket_type_count.put(mContext.getString(R.string.category2), count + 1);
                                break;
                            case "Garden & Parks":
                                count = bucket_type_count.get(mContext.getString(R.string.category3));
                                if (count == null) {
                                    bucket_type_count.put(mContext.getString(R.string.category3), 1);
                                }
                                bucket_type_count.put(mContext.getString(R.string.category3), count + 1);
                                break;
                            case "Hills & Caves":
                                count = bucket_type_count.get(mContext.getString(R.string.category4));
                                bucket_type_count.put(mContext.getString(R.string.category4), count + 1);
                                if (count == null) {
                                    bucket_type_count.put(mContext.getString(R.string.category4), 1);
                                }
                                break;
                            case "Historical Monuments":
                                count = bucket_type_count.get(mContext.getString(R.string.category5));
                                if (count == null) {
                                    bucket_type_count.put(mContext.getString(R.string.category5), 1);
                                }
                                bucket_type_count.put(mContext.getString(R.string.category5), count + 1);
                                break;
                            case "Nature & Wildlife":
                                count = bucket_type_count.get(mContext.getString(R.string.category6));
                                if (count == null) {
                                    bucket_type_count.put(mContext.getString(R.string.category6), 1);
                                }
                                bucket_type_count.put(mContext.getString(R.string.category6), count + 1);
                                break;
                            case "Port & Sea Beach":
                                count = bucket_type_count.get(mContext.getString(R.string.category7));
                                if (count == null) {
                                    bucket_type_count.put(mContext.getString(R.string.category7), 1);
                                }
                                bucket_type_count.put(mContext.getString(R.string.category7), count + 1);
                                break;
                            case "Religious Sites":
                                count = bucket_type_count.get(mContext.getString(R.string.category8));
                                if (count == null) {
                                    bucket_type_count.put(mContext.getString(R.string.category8), 1);
                                }
                                bucket_type_count.put(mContext.getString(R.string.category8), count + 1);
                                break;
                            case "Waterfalls":
                                count = bucket_type_count.get(mContext.getString(R.string.category9));
                                if (count == null) {
                                    bucket_type_count.put(mContext.getString(R.string.category9), 1);
                                }
                                bucket_type_count.put(mContext.getString(R.string.category9), count + 1);
                                break;
                            case "Zoos & Reserves":
                                count = bucket_type_count.get(mContext.getString(R.string.category10));
                                if (count == null) {
                                    bucket_type_count.put(mContext.getString(R.string.category10), 1);
                                }
                                bucket_type_count.put(mContext.getString(R.string.category10), count + 1);
                                break;
                            default:
                                break;

                        }


                    }
                }
            }
        });

        CollectionReference accomplishRef = FirebaseFirestore.getInstance()
                .collection(mContext.getString(R.string.collection_users))
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection(mContext.getString(R.string.collection_accomplished_list));

        accomplishRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    assert querySnapshot != null;
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        LandmarkList landmarkList = document.toObject(LandmarkList.class);
                        assert landmarkList != null;
                        accomplished_id_list.add(landmarkList.getLandmarkMeta().getId());
                        accomplished_list.add(landmarkList.getLandmarkMeta().getLandmark().getName());
                        Log.d(TAG, "Accomplished List Added");

                        Integer count = 0;
                        switch (landmarkList.getLandmarkMeta().getLandmark().getCategory()) {
                            case "Dams & Water Reservoirs":
                                count = accomplish_type_count.get(mContext.getString(R.string.category1));
                                if (count == null) {
                                    accomplish_type_count.put(mContext.getString(R.string.category1), 1);
                                }
                                accomplish_type_count.put(mContext.getString(R.string.category1), count + 1);
                                break;
                            case "Education & History":
                                count = accomplish_type_count.get(mContext.getString(R.string.category2));
                                if (count == null) {
                                    accomplish_type_count.put(mContext.getString(R.string.category2), 1);
                                }
                                accomplish_type_count.put(mContext.getString(R.string.category2), count + 1);
                                break;
                            case "Garden & Parks":
                                count = accomplish_type_count.get(mContext.getString(R.string.category3));
                                if (count == null) {
                                    accomplish_type_count.put(mContext.getString(R.string.category3), 1);
                                }
                                accomplish_type_count.put(mContext.getString(R.string.category3), count + 1);
                                break;
                            case "Hills & Caves":
                                count = accomplish_type_count.get(mContext.getString(R.string.category4));
                                accomplish_type_count.put(mContext.getString(R.string.category4), count + 1);
                                if (count == null) {
                                    accomplish_type_count.put(mContext.getString(R.string.category4), 1);
                                }
                                break;
                            case "Historical Monuments":
                                count = accomplish_type_count.get(mContext.getString(R.string.category5));
                                if (count == null) {
                                    accomplish_type_count.put(mContext.getString(R.string.category5), 1);
                                }
                                accomplish_type_count.put(mContext.getString(R.string.category5), count + 1);
                                break;
                            case "Nature & Wildlife":
                                count = accomplish_type_count.get(mContext.getString(R.string.category6));
                                if (count == null) {
                                    accomplish_type_count.put(mContext.getString(R.string.category6), 1);
                                }
                                accomplish_type_count.put(mContext.getString(R.string.category6), count + 1);
                                break;
                            case "Port & Sea Beach":
                                count = accomplish_type_count.get(mContext.getString(R.string.category7));
                                if (count == null) {
                                    accomplish_type_count.put(mContext.getString(R.string.category7), 1);
                                }
                                accomplish_type_count.put(mContext.getString(R.string.category7), count + 1);
                                break;
                            case "Religious Sites":
                                count = accomplish_type_count.get(mContext.getString(R.string.category8));
                                if (count == null) {
                                    accomplish_type_count.put(mContext.getString(R.string.category8), 1);
                                }
                                accomplish_type_count.put(mContext.getString(R.string.category8), count + 1);
                                break;
                            case "Waterfalls":
                                count = accomplish_type_count.get(mContext.getString(R.string.category9));
                                if (count == null) {
                                    accomplish_type_count.put(mContext.getString(R.string.category9), 1);
                                }
                                accomplish_type_count.put(mContext.getString(R.string.category9), count + 1);
                                break;
                            case "Zoos & Reserves":
                                count = accomplish_type_count.get(mContext.getString(R.string.category10));
                                if (count == null) {
                                    accomplish_type_count.put(mContext.getString(R.string.category10), 1);
                                }
                                accomplish_type_count.put(mContext.getString(R.string.category10), count + 1);
                                break;
                            default:
                                break;

                        }


                    }
                }
            }
        });


        //To Display Hot Places
        SharedPreferences pref = mContext.getSharedPreferences("myPrefs", MODE_PRIVATE);


        CollectionReference colRef = FirebaseFirestore.getInstance()
                .collection(mContext.getString(R.string.collection_stats))
                .document(mContext.getString(R.string.collection_landmarks))
                .collection(mContext.getString(R.string.document_lists))
                .document(mContext.getString(R.string.document_accomplished))
                .collection(Objects.requireNonNull(pref.getString("localState", "ichy")));

        Query query = colRef
                .orderBy("landmarkCounter", Query.Direction.DESCENDING)
                .limit(10);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        LandmarkStat landmarkStat = document.toObject(LandmarkStat.class);
                        try {
                            Double dist = distance(current_location.getLatitude(), current_location.getLongitude(), landmarkStat.getLandmark().getGeo_point().getLatitude(), landmarkStat.getLandmark().getGeo_point().getLongitude(), 0, 0);

                            list_hot.add(new RV_Distance(landmarkStat.getLandmark().getId(), landmarkStat.getLandmark().getName(), landmarkStat.getLandmark().getImg_url(),
                                    landmarkStat.getLandmark().getCategory(), landmarkStat.getLandmark().getState(), landmarkStat.getLandmark().getDistrict(), landmarkStat.getLandmark().getCity(), dist, mContext.getString(R.string.hot_places)));
                        } catch (NullPointerException e) {
                            retrieveLocationandAddtoList(landmarkStat);
                        }
                    }
                }
            }
        });


        return true;
    }


    private void getallfriends() {

        CollectionReference collRef = FirebaseFirestore.getInstance()
                .collection(mContext.getString(R.string.collection_relations))
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection(mContext.getString(R.string.collection_friendlist));

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

    private void getallrequested() {

        CollectionReference collRef = FirebaseFirestore.getInstance()
                .collection(mContext.getString(R.string.collection_relations))
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection(mContext.getString(R.string.collection_requestedlist));

        collRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        String user_id = document.getId();
                        if (!MainActivity.RequestedList.contains(user_id)) {
                            MainActivity.RequestedList.add(user_id);
                        }
                    }
                }
            }
        });

        Log.d(TAG, "Requested List" + MainActivity.RequestedList);

    }

    private void retrieveLocationandAddtoList(LandmarkStat landmarkStat) {
        Log.d(TAG, "getLastKnownLocation: called.");


        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                Location location = locationResult.getLastLocation();
                current_location = location;

                Double dist = distance(location.getLatitude(), location.getLongitude(), landmarkStat.getLandmark().getGeo_point().getLatitude(), landmarkStat.getLandmark().getGeo_point().getLongitude(), 0, 0);

                list_hot.add(new RV_Distance(landmarkStat.getLandmark().getId(), landmarkStat.getLandmark().getName(), landmarkStat.getLandmark().getImg_url(),
                        landmarkStat.getLandmark().getCategory(), landmarkStat.getLandmark().getState(), landmarkStat.getLandmark().getDistrict(), landmarkStat.getLandmark().getCity(), dist, mContext.getString(R.string.hot_places)));

            }
        };

//        Looper.prepare();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setNumUpdates(1);
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());


    }
}