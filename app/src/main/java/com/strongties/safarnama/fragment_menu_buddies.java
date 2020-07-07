package com.strongties.safarnama;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.strongties.safarnama.adapters.RecyclerViewAdapter_friend_list;
import com.strongties.safarnama.adapters.RecyclerViewAdapter_request_list;
import com.strongties.safarnama.user_classes.RV_friend;
import com.strongties.safarnama.user_classes.RV_friendrequest;
import com.strongties.safarnama.user_classes.User;
import com.strongties.safarnama.user_classes.UserLocation;
import com.strongties.safarnama.user_classes.UserRelation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class fragment_menu_buddies extends Fragment {

    private static final String TAG = "Menu 3";

    EditText editText;
    Button btn1;

    Context mcontext;
    Dialog myDialog;
    User currentuser;

    View v;
    private RecyclerView friendrecyclerview;
    private RecyclerView requestrecyclerview;
    private List<RV_friend> list_friend;
    private List<RV_friendrequest> list_request;

    private FirebaseFirestore mDb;
    private FusedLocationProviderClient mFusedLocationClient;
    private ListenerRegistration mUserListenerRegistration;
    private ListenerRegistration mRequestListenerRegistration;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_menu_buddies,container,false);

        friendrecyclerview = v.findViewById(R.id.menu3_rv_friend);
        RecyclerViewAdapter_friend_list friendrecyclerAdapter = new RecyclerViewAdapter_friend_list(getContext(),list_friend);
        friendrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendrecyclerview.setAdapter(friendrecyclerAdapter);

        requestrecyclerview = v.findViewById(R.id.menu3_rv_request);
        RecyclerViewAdapter_request_list requestrecyclerAdapter = new RecyclerViewAdapter_request_list(getContext(),list_request);
        requestrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        requestrecyclerview.setAdapter(requestrecyclerAdapter);

        TextView buddy_requests = v.findViewById(R.id.menu3_buddy_requests);
        TextView buddy_list_placeholder = v.findViewById(R.id.menu3_buddy_list_placeholder);

       // if(list_request.size() == 0){
         //   buddy_requests.setVisibility(View.GONE);
       // }
       // if(list_friend.isEmpty()){
         //   buddy_list_placeholder.setVisibility(View.VISIBLE);
       // }


        mcontext = getContext();



        mDb = FirebaseFirestore.getInstance();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mcontext);

        DocumentReference userRef = mDb.collection(mcontext.getString(R.string.collection_users))
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));


        currentuser = ((UserClient)(mcontext.getApplicationContext())).getUser();


        //Set the user information
        TextView tv_profile_name = v.findViewById(R.id.menu3_buddies_profile_name);
        TextView tv_profile_email = v.findViewById(R.id.menu3_buddies_profile_email);
        TextView tv_profile_badge = v.findViewById(R.id.menu3_buddies_profile_badge);
        ImageView iv_profile_img = v.findViewById(R.id.menu3_buddies_profile_img);

        tv_profile_name.setText(currentuser.getUsername());
        tv_profile_email.setText(currentuser.getEmail());
        Glide.with(mcontext)
                .load(currentuser.getPhoto())
                .placeholder(R.drawable.loading_image)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                .into(iv_profile_img);


        LinearLayout profile_layout = v.findViewById(R.id.menu3_buddies_profile);
        profile_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppCompatActivity)mcontext).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_bottom,R.anim.exit_to_top)
                        .replace(R.id.fragment_container, new fragment_menu_buddies_profile(), "Buddy Profile Fragment").commit();
            }
        });





        Button friend_search = v.findViewById(R.id.menu3_btn1);
        friend_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog parentDialog = new Dialog(mcontext);
                parentDialog.setContentView(R.layout.dialog_friend_search);
                parentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                final EditText email = parentDialog.findViewById(R.id.friend_search_email);

                Button search = parentDialog.findViewById(R.id.search_btn);
                search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(email.getText().toString().equals(currentuser.getEmail())){
                            Toast.makeText(mcontext, getString(R.string.friend_request_self), Toast.LENGTH_SHORT).show();
                        }else{
                            buddysearch(email.getText().toString());
                        }
                    }
                });
                parentDialog.show();
            }
        });


        Button friend_map = v.findViewById(R.id.menu3_btn2);
        friend_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppCompatActivity)mcontext).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_bottom,R.anim.exit_to_top)
                        .replace(R.id.fragment_container, new fragment_buddy_googleMap(), "Buddy Google map").commit();
            }
        });



        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDb = FirebaseFirestore.getInstance();

        //For Request List
        list_request = new ArrayList<>();
        CollectionReference requestusersRef = mDb
                .collection(getString(R.string.collection_relations))
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection(getString(R.string.collection_requestlist));

        mRequestListenerRegistration = requestusersRef
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null){
                            Log.e(TAG, "onEvent: Listen failed.", e);
                        }

                        if(queryDocumentSnapshots != null){
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                                UserRelation requesteduserRelation = doc.toObject(UserRelation.class);
                                User requesteduser = requesteduserRelation.getUser();
                                String pattern = "dd-MM-YYYY";
                                DateFormat df = new SimpleDateFormat(pattern);
                                String dateasstring = df.format(requesteduserRelation.getTimestamp());

                                list_request.add(new RV_friendrequest(requesteduser.getUsername(), requesteduser.getEmail(), dateasstring, requesteduser.getPhoto(), requesteduserRelation));
                            }
                        }
                    }
                });


        //For Friend List
        list_friend = new ArrayList<>();

        CollectionReference usersRef = mDb
                .collection(getString(R.string.collection_relations))
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection(getString(R.string.collection_friendlist));

        mUserListenerRegistration = usersRef
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null){
                            Log.e(TAG, "onEvent: Listen failed.", e);
                        }

                        if(queryDocumentSnapshots != null){
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                                final UserRelation userRel = doc.toObject(UserRelation.class);
                                final User user = userRel.getUser();
                                String pattern = "dd-MM-YYYY";
                                DateFormat df = new SimpleDateFormat(pattern);
                                String dateasstring = df.format(userRel.getTimestamp());

                                list_friend.add(new RV_friend(user.getUsername(), user.getEmail(), dateasstring, user.getPhoto(), userRel));
                            }
                        }
                    }
                });


    }


    private void buddysearch(final String given_email) {
        CollectionReference usersRef = mDb
                .collection(getString(R.string.collection_users));

        mUserListenerRegistration = usersRef
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.e(TAG, "onEvent: Listen failed.", e);
                        }

                        if(queryDocumentSnapshots != null) {
                            boolean flag = false;

                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                final User user = doc.toObject(User.class);
                                if(user.getEmail().equals(given_email)){
                                    flag = true;

                                    final UserRelation userRelation = new UserRelation();
                                    userRelation.setRelation(getString(R.string.requested));


                                    myDialog = new Dialog(mcontext);
                                    myDialog.setContentView(R.layout.menu3_dialogue_findbuddy);
                                    myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                    TextView name_tv = myDialog.findViewById(R.id.menu3_findbuddy_name);
                                    TextView email_tv = myDialog.findViewById(R.id.menu3_findbuddy_email);
                                    ImageView photo = myDialog.findViewById(R.id.menu3_findbuddy_img);
                                    Button request_friend = myDialog.findViewById(R.id.menu3_findbuddy_btn);

                                    name_tv.setText(user.getUsername());
                                    email_tv.setText(user.getEmail());
                                    Glide.with(mcontext)
                                            .load(user.getPhoto())
                                            .placeholder(R.drawable.loading_image)
                                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                                            .into(photo);

                                    request_friend.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //Consraints

                                            //Check in current user's Requested list

                                            DocumentReference docIdRef1 = mDb.
                                                    collection(getString(R.string.collection_relations))
                                                    .document(currentuser.getUser_id())
                                                    .collection(getString(R.string.collection_requestedlist))
                                                    .document(user.getUser_id());

                                            docIdRef1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        assert document != null;
                                                        if (document.exists()) {
                                                            Log.d(TAG, "Document exists!");
                                                          //  Toast.makeText(mcontext, getString(R.string.friend_request_pending), Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Log.d(TAG, "Document does not exist!");
                                                        }
                                                    } else {
                                                        Log.d(TAG, "Failed with: ", task.getException());
                                                    }
                                                }
                                            });


                                            //Check in current user's Friend list

                                            DocumentReference docIdRef2 = mDb.
                                                    collection(getString(R.string.collection_relations))
                                                    .document(currentuser.getUser_id())
                                                    .collection(getString(R.string.collection_friendlist))
                                                    .document(user.getUser_id());

                                            docIdRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        assert document != null;
                                                        if (document.exists()) {
                                                            Log.d(TAG, "Document exists!");
                                                            //Toast.makeText(mcontext, getString(R.string.friend_request_already_accepted), Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Log.d(TAG, "Document does not exist!");
                                                        }
                                                    } else {
                                                        Log.d(TAG, "Failed with: ", task.getException());
                                                    }
                                                }
                                            });


                                            //add into requested list for current user
                                            userRelation.setUser(user);
                                            DocumentReference usercollectionRef = mDb.collection(getString(R.string.collection_relations))
                                                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                                    .collection(getString(R.string.collection_requestedlist))
                                                    .document(user.getUser_id());
                                            usercollectionRef.set(userRelation).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        //   Toast.makeText(mcontext, getString(R.string.friend_request_success), Toast.LENGTH_SHORT).show();
                                                        Log.d(TAG, "Successfull added to request list for sender");
                                                    }else{
                                                        //  Toast.makeText(mcontext, getString(R.string.friend_request_fail), Toast.LENGTH_SHORT).show();
                                                        Log.w(TAG, "Error getting documents.", task.getException());
                                                    }
                                                }
                                            });
                                            // add into request list for requested user
                                            userRelation.setUser(currentuser);
                                            usercollectionRef = mDb.collection(getString(R.string.collection_relations))
                                                    .document(user.getUser_id())
                                                    .collection(getString(R.string.collection_requestlist))
                                                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                                            usercollectionRef.set(userRelation).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(mcontext, getString(R.string.friend_request_success), Toast.LENGTH_SHORT).show();
                                                        Log.d(TAG, "Successfull added to request list for reciever");
                                                    }else{
                                                        Toast.makeText(mcontext, getString(R.string.friend_request_fail), Toast.LENGTH_SHORT).show();
                                                        Log.w(TAG, "Error getting documents.", task.getException());
                                                    }
                                                }
                                            });

                                        }
                                    });

                                    myDialog.show();
                                    //Toast.makeText(mcontext, "User Found", Toast.LENGTH_SHORT).show();
                                }

                            }

                            Handler handler = new Handler();
                            final boolean finalFlag = flag;
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(!finalFlag){
                                        Toast.makeText(mcontext, "User not found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, 2000);


                        }

                    }
                });


    }



}
