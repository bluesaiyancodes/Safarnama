package com.strongties.safarnama.background_tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.strongties.safarnama.R;
import com.strongties.safarnama.user_classes.Landmark;
import com.strongties.safarnama.user_classes.LandmarkMeta;

import java.util.Objects;

public class MapBackgroundTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "BG Task";
    GoogleMap googleMap;
    String req;
    FirebaseFirestore mDb;
    private ProgressDialog mProgressDialog;
    private Context mContext;

    public MapBackgroundTask(Context context, GoogleMap gmap, String req) {
        this.mContext = context;
        this.googleMap = gmap;
        this.req = req;
        mDb = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mProgressDialog = ProgressDialog.show(mContext, "Loading", "Fetching the Locations");
        mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dlg) {
                MapBackgroundTask.this.cancel(true);
            }
        });
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // do some background work here














        if (req.equals("all")) {

            // For all requests


            CollectionReference landmarkColl = mDb
                    .collection(mContext.getString(R.string.collection_landmarks))
                    .document(mContext.getString(R.string.document_meta))
                    .collection(mContext.getString(R.string.collection_all));


            landmarkColl.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.e(TAG, "onEvent: Listen failed.", e);
                    }

                    if (queryDocumentSnapshots != null) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            LandmarkMeta landmarkMeta = doc.toObject(LandmarkMeta.class);

                            CollectionReference collRef = mDb
                                    .collection(mContext.getString(R.string.collection_landmarks))
                                    .document(landmarkMeta.getState())
                                    .collection(landmarkMeta.getCity());

                            collRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                    if (e != null) {
                                        Log.e(TAG, "onEvent: Listen failed.", e);
                                    }

                                    if (queryDocumentSnapshots != null) {
                                        for (QueryDocumentSnapshot places : queryDocumentSnapshots) {
                                            Landmark landmark = places.toObject(Landmark.class);

                                            DocumentReference bucketRef = mDb
                                                    .collection(mContext.getString(R.string.collection_users))
                                                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                                    .collection(mContext.getString(R.string.collection_bucket_list))
                                                    .document(landmark.getId());

                                            bucketRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            Log.d(TAG, "Landmark exists in Bucket List");
                                                            LatLng place = new LatLng(landmark.getGeo_point().getLatitude(), landmark.getGeo_point().getLongitude());
                                                            googleMap.addMarker(new MarkerOptions().position(place).title(landmark.getName())
                                                                    .snippet(landmark.getCategory() + "  " + mContext.getString(R.string.i_circle))
                                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                                                        }
                                                    }
                                                }
                                            });



                                            Log.d(TAG, "Landmark does not exist in BL");
                                            DocumentReference accomplishedRef = mDb
                                                    .collection(mContext.getString(R.string.collection_users))
                                                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                                    .collection(mContext.getString(R.string.collection_accomplished_list))
                                                    .document(landmark.getId());

                                            accomplishedRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot documentSnapshot = task.getResult();
                                                        if (documentSnapshot.exists()) {
                                                            Log.d(TAG, "Landmark exists in Accomplished List");
                                                            LatLng place = new LatLng(landmark.getGeo_point().getLatitude(), landmark.getGeo_point().getLongitude());
                                                            googleMap.addMarker(new MarkerOptions().position(place).title(landmark.getName())
                                                                    .snippet(landmark.getCategory() + "  " + mContext.getString(R.string.i_circle))
                                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                                                        } else {
                                                            Log.d(TAG, "Landmark does not exist in Accomplished List");
                                                            LatLng place = new LatLng(landmark.getGeo_point().getLatitude(), landmark.getGeo_point().getLongitude());
                                                            googleMap.addMarker(new MarkerOptions().position(place).title(landmark.getName())
                                                                    .snippet(landmark.getCategory() + "  " + mContext.getString(R.string.i_circle))
                                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                                        }
                                                    }
                                                }
                                            });


                                        }
                                    }
                                }
                            });

                        }
                    }
                }
            });





            /*
            *
            * Old Code -> Fetching details using sqlite
            *
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
             */
        } else if (req.equals("new")) {

            // For all new requests

//            Toast.makeText(mContext, mContext.getString(R.string.show_new), Toast.LENGTH_SHORT).show();

            CollectionReference landmarkColl = mDb
                    .collection(mContext.getString(R.string.collection_landmarks))
                    .document(mContext.getString(R.string.document_meta))
                    .collection(mContext.getString(R.string.collection_all));


            landmarkColl.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.e(TAG, "onEvent: Listen failed.", e);
                    }

                    if (queryDocumentSnapshots != null) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            LandmarkMeta landmarkMeta = doc.toObject(LandmarkMeta.class);

                            CollectionReference collRef = mDb
                                    .collection(mContext.getString(R.string.collection_landmarks))
                                    .document(landmarkMeta.getState())
                                    .collection(landmarkMeta.getCity());

                            collRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                    if (e != null) {
                                        Log.e(TAG, "onEvent: Listen failed.", e);
                                    }

                                    if (queryDocumentSnapshots != null) {
                                        for (QueryDocumentSnapshot places : queryDocumentSnapshots) {
                                            Landmark landmark = places.toObject(Landmark.class);

                                            DocumentReference bucketRef = mDb
                                                    .collection(mContext.getString(R.string.collection_users))
                                                    .document(mContext.getString(R.string.document_lists))
                                                    .collection(mContext.getString(R.string.collection_bucket_list))
                                                    .document(landmark.getId());

                                            bucketRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            Log.d(TAG, "Landmark exists in Bucket List");
                                                        } else {
                                                            Log.d(TAG, "Landmark does not exist in Bucket List");
                                                            DocumentReference accomplishedRef = mDb
                                                                    .collection(mContext.getString(R.string.collection_users))
                                                                    .document(mContext.getString(R.string.document_lists))
                                                                    .collection(mContext.getString(R.string.collection_accomplished_list))
                                                                    .document(landmark.getId());

                                                            accomplishedRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        DocumentSnapshot documentSnapshot = task.getResult();
                                                                        if (document.exists()) {
                                                                            Log.d(TAG, "Landmark exists in Accomplished List");
                                                                        } else {
                                                                            Log.d(TAG, "Landmark does not exist in Accomplished List");
                                                                            LatLng place = new LatLng(landmark.getGeo_point().getLatitude(), landmark.getGeo_point().getLongitude());
                                                                            googleMap.addMarker(new MarkerOptions().position(place).title(landmark.getName())
                                                                                    .snippet(landmark.getCategory() + "  " + mContext.getString(R.string.i_circle))
                                                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                                                        }
                                                                    }
                                                                }
                                                            });


                                                        }
                                                    }
                                                }
                                            });


                                        }
                                    }
                                }
                            });

                        }
                    }
                }
            });



            /*
            *
            * Old Code -> Fetching details using sqlite
            *
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
             */


        } else if (req.equals("wish")) {

            //for wishlist

            // Toast.makeText(mContext, mContext.getString(R.string.show_bucket), Toast.LENGTH_SHORT).show();

            CollectionReference landmarkColl = mDb
                    .collection(mContext.getString(R.string.collection_landmarks))
                    .document(mContext.getString(R.string.document_meta))
                    .collection(mContext.getString(R.string.collection_all));


            landmarkColl.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.e(TAG, "onEvent: Listen failed.", e);
                    }

                    if (queryDocumentSnapshots != null) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            LandmarkMeta landmarkMeta = doc.toObject(LandmarkMeta.class);

                            CollectionReference collRef = mDb
                                    .collection(mContext.getString(R.string.collection_landmarks))
                                    .document(landmarkMeta.getState())
                                    .collection(landmarkMeta.getCity());

                            collRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                    if (e != null) {
                                        Log.e(TAG, "onEvent: Listen failed.", e);
                                    }

                                    if (queryDocumentSnapshots != null) {
                                        for (QueryDocumentSnapshot places : queryDocumentSnapshots) {
                                            Landmark landmark = places.toObject(Landmark.class);

                                            DocumentReference bucketRef = FirebaseFirestore.getInstance()
                                                    .collection(mContext.getString(R.string.collection_users))
                                                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                                    .collection(mContext.getString(R.string.collection_bucket_list))
                                                    .document(landmark.getId());


                                            bucketRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            Log.d(TAG, "Landmark exists in Bucket List");
                                                            LatLng place = new LatLng(landmark.getGeo_point().getLatitude(), landmark.getGeo_point().getLongitude());
                                                            googleMap.addMarker(new MarkerOptions().position(place).title(landmark.getName())
                                                                    .snippet(landmark.getCategory() + "  " + mContext.getString(R.string.i_circle))
                                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                                                        } else {
                                                            Log.d(TAG, "Landmark does not exist in BL");
                                                        }
                                                    }
                                                }
                                            });


                                        }
                                    }
                                }
                            });

                        }
                    }
                }
            });


            /*
            *
            * Old Code -> Fetching details using sqlite
            *
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
             */

        } else {

            //For accomplished
            //Toast.makeText(mContext, mContext.getString(R.string.show_accomplished), Toast.LENGTH_SHORT).show();

            CollectionReference landmarkColl = mDb
                    .collection(mContext.getString(R.string.collection_landmarks))
                    .document(mContext.getString(R.string.document_meta))
                    .collection(mContext.getString(R.string.collection_all));


            landmarkColl.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.e(TAG, "onEvent: Listen failed.", e);
                    }

                    if (queryDocumentSnapshots != null) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            LandmarkMeta landmarkMeta = doc.toObject(LandmarkMeta.class);

                            CollectionReference collRef = mDb
                                    .collection(mContext.getString(R.string.collection_landmarks))
                                    .document(landmarkMeta.getState())
                                    .collection(landmarkMeta.getCity());

                            collRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                    if (e != null) {
                                        Log.e(TAG, "onEvent: Listen failed.", e);
                                    }

                                    if (queryDocumentSnapshots != null) {
                                        for (QueryDocumentSnapshot places : queryDocumentSnapshots) {
                                            Landmark landmark = places.toObject(Landmark.class);

                                            DocumentReference accomplishedRef = FirebaseFirestore.getInstance()
                                                    .collection(mContext.getString(R.string.collection_users))
                                                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                                    .collection(mContext.getString(R.string.collection_accomplished_list))
                                                    .document(landmark.getId());

                                            accomplishedRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            Log.d(TAG, "Landmark exists in Bucket List");
                                                            LatLng place = new LatLng(landmark.getGeo_point().getLatitude(), landmark.getGeo_point().getLongitude());
                                                            googleMap.addMarker(new MarkerOptions().position(place).title(landmark.getName())
                                                                    .snippet(landmark.getCategory() + "  " + mContext.getString(R.string.i_circle))
                                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                                                        }
                                                    }
                                                }
                                            });


                                        }
                                    }
                                }
                            });

                        }
                    }
                }
            });



            /*
            *
            * Old Code -> Fetching details using sqlite
            *
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
             */
        }








        return true;
    }

    @Override
    protected void onPostExecute(final Boolean result) {
        if (this.isCancelled()) {
            return;
        }

        if (mProgressDialog != null) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mProgressDialog.dismiss();
                }
            }, 2000);


        }

    }
}