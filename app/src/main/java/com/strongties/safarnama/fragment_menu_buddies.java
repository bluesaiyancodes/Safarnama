package com.strongties.safarnama;

import android.app.Dialog;
import android.content.Context;
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
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
import com.strongties.safarnama.user_classes.RV_friendrequest;
import com.strongties.safarnama.user_classes.User;
import com.strongties.safarnama.user_classes.UserRelation;

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
    private List<RV_friendrequest> list_request;

    private FirebaseFirestore mDb;
    private FusedLocationProviderClient mFusedLocationClient;
    private ListenerRegistration mUserListenerRegistration;

    RecyclerViewAdapter_friend_list friendrecyclerAdapter;
    RecyclerViewAdapter_request_list requestrecyclerAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_menu_buddies,container,false);

        friendrecyclerview = v.findViewById(R.id.menu3_rv_friend);
       // friendrecyclerview.setHasFixedSize(true);
        friendrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendrecyclerview.setAdapter(friendrecyclerAdapter);

        requestrecyclerview = v.findViewById(R.id.menu3_rv_request);
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

        DocumentReference documentReference = FirebaseFirestore.getInstance()
                .collection(mcontext.getString(R.string.collection_users))
                .document(FirebaseAuth.getInstance().getUid());

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "onComplete: successfully set the user client.");
                    User user = Objects.requireNonNull(task.getResult()).toObject(User.class);

                    Log.d(TAG, "username -> "+user.getUsername());
                    Log.d(TAG, "email -> "+user.getEmail());
                    Log.d(TAG, "uid -> "+user.getUser_id());
                    tv_profile_name.setText(user.getUsername());
                    tv_profile_email.setText(user.getEmail());

                    switch (user.getAvatar()){
                        case "0 Star":
                            tv_profile_badge.setText(getString(R.string.avatar_0_lvl));
                            break;
                        case "1 Star":
                            tv_profile_badge.setText(getString(R.string.avatar_1_lvl));
                            break;
                        case "2 Star":
                            tv_profile_badge.setText(getString(R.string.avatar_2_lvl));
                            break;
                        case "3 Star":
                            tv_profile_badge.setText(getString(R.string.avatar_3_lvl));
                            break;
                        case "4 Star":
                            tv_profile_badge.setText(getString(R.string.avatar_4_lvl));
                            break;
                        case "5 Star":
                            tv_profile_badge.setText(getString(R.string.avatar_5_lvl));
                            break;
                        default:
                            break;
                    }
                    Glide.with(mcontext)
                            .load(user.getPhoto())
                            .placeholder(R.drawable.loading_image)
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                            .into(iv_profile_img);


                }
            }
        });




        LinearLayout profile_layout = v.findViewById(R.id.menu3_buddies_profile);
        profile_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppCompatActivity)mcontext).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_bottom,R.anim.exit_to_top, R.anim.enter_from_top, R.anim.exit_to_bottom)
                        .replace(R.id.fragment_container, new fragment_menu_buddies_profile(), "Buddy Profile Fragment").addToBackStack( "buddyProfile" ).commit();
            }
        });





        Button friend_search = v.findViewById(R.id.menu3_btn1);
        friend_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog parentDialog = new Dialog(mcontext);
                parentDialog.setContentView(R.layout.dialog_friend_search);
                parentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                parentDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

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
                ((AppCompatActivity)mcontext).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_bottom,R.anim.exit_to_top, R.anim.enter_from_top, R.anim.exit_to_bottom)
                        .replace(R.id.fragment_container, new fragment_buddy_googleMap(), "Buddy Google map").addToBackStack( "buddyGmap" ).commit();
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

       FirestoreRecyclerOptions<UserRelation> options = new FirestoreRecyclerOptions.Builder<UserRelation>()
                .setQuery(requestusersRef, UserRelation.class)
                .build();

       requestrecyclerAdapter = new RecyclerViewAdapter_request_list(options);
        //For Friend List

        CollectionReference usersRef = mDb
                .collection(getString(R.string.collection_relations))
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection(getString(R.string.collection_friendlist));

        FirestoreRecyclerOptions<UserRelation> option = new FirestoreRecyclerOptions.Builder<UserRelation>()
                .setQuery(usersRef, UserRelation.class)
                .build();

        friendrecyclerAdapter = new RecyclerViewAdapter_friend_list(option);



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
                                    Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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
                                            //and
                                            //Check in current user's Friend list

                                            if(MainActivity.RequestedList.contains(user.getUser_id())){
                                                Toast.makeText(mcontext, getString(R.string.friend_request_pending), Toast.LENGTH_SHORT).show();
                                            }else if(MainActivity.FriendList.contains(user.getUser_id())){
                                                Toast.makeText(mcontext, getString(R.string.friend_request_already_accepted), Toast.LENGTH_SHORT).show();
                                            }else {


                                                //add into requested list for current user
                                                userRelation.setUser_id(user.getUser_id());
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
                                                userRelation.setUser_id(currentuser.getUser_id());
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

    @Override
    public void onStart() {
        super.onStart();
        friendrecyclerAdapter.startListening();
        requestrecyclerAdapter.startListening();
        getallfriends();
        getallrequested();
    }

    @Override
    public void onStop() {
        super.onStop();
        friendrecyclerAdapter.stopListening();
        requestrecyclerAdapter.startListening();
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
                        if(!MainActivity.FriendList.contains(user_id)){
                            MainActivity.FriendList.add(user_id);
                        }
                    }
                }
            }
        });

        Log.d(TAG, "FriendList" + MainActivity.FriendList);

    }

    private  void getallrequested(){

        CollectionReference collRef = FirebaseFirestore.getInstance()
                .collection(getString(R.string.collection_relations))
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection(getString(R.string.collection_requestedlist));

        collRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        String user_id = document.getId();
                        if(!MainActivity.RequestedList.contains(user_id)){
                            MainActivity.RequestedList.add(user_id);
                        }
                    }
                }
            }
        });

        Log.d(TAG, "Requested List" + MainActivity.RequestedList);

    }

}
