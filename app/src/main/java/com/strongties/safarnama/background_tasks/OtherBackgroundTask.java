package com.strongties.safarnama.background_tasks;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.strongties.safarnama.MainActivity.RequestList;
import static com.strongties.safarnama.MainActivity.accomplished_id_list;
import static com.strongties.safarnama.MainActivity.accomplished_list;
import static com.strongties.safarnama.MainActivity.bucket_id_list;
import static com.strongties.safarnama.MainActivity.bucket_list;
import static com.strongties.safarnama.MainActivity.list_hot;
import static com.strongties.safarnama.MainActivity.localState;
import static com.strongties.safarnama.MainActivity.places_id_list;
import static com.strongties.safarnama.MainActivity.places_list;

public class OtherBackgroundTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "Avatar BG";
    FusedLocationProviderClient mFusedLocationClient;

    private Context mContext;

    public OtherBackgroundTask(Context context) {
        this.mContext = context;
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
    protected void onPreExecute() {
        super.onPreExecute();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // do some background work here
        getLastKnownLocation();

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
                .limit(5);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        LandmarkStat landmarkStat = document.toObject(LandmarkStat.class);
                        list_hot.add(new RV_Distance(landmarkStat.getLandmark().getId(), landmarkStat.getLandmark().getName(), landmarkStat.getLandmark().getImg_url(),
                                landmarkStat.getLandmark().getCategory(), landmarkStat.getLandmark().getState(), landmarkStat.getLandmark().getCity(), 0.0));

                    }
                }
            }
        });


        return true;
    }

    @Override
    protected void onPostExecute(final Boolean result) {
        if (this.isCancelled()) {
            return;
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
                    String address = getaddres(location);

                    String[] tokens = address.split(",");
                    String[] stateToken = tokens[tokens.length - 2].split(" ");
                    localState = stateToken[1];


                    SharedPreferences pref = mContext.getSharedPreferences("myPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("localState", localState);
                    editor.apply();

                    Log.d(TAG, "local Testing-> " + stateToken[1]);

                }
            }
        });

    }

    public String getaddres(Location loc) {

        StringBuffer address = new StringBuffer();
        Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
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
}