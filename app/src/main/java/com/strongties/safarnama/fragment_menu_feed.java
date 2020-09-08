package com.strongties.safarnama;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.strongties.safarnama.adapters.RecyclerViewAdapter_feed;
import com.strongties.safarnama.user_classes.UserFeed;

import java.util.Objects;

public class fragment_menu_feed extends Fragment {
    public static final String TAG = "FeedFragment";
    Context mContext;
    FirebaseFirestore mDb;
    Button public_feed, buddy_feed;
    private String mode = "global";
    private RecyclerViewAdapter_feed adapter_feed = null;

    public fragment_menu_feed() {
        this.mode = "global";
    }
    public fragment_menu_feed(String mode) {
        this.mode = mode;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_feed, container, false);

        mContext = getContext();
        mDb = FirebaseFirestore.getInstance();


        FloatingActionButton fab = root.findViewById(R.id.fab);
        public_feed = root.findViewById(R.id.menu_feed_public);
        buddy_feed = root.findViewById(R.id.menu_feed_buddy);

        if(mode.equals("global")){
            buddy_feed.setAlpha((float) 0.5);
        }else {
            public_feed.setAlpha((float) 0.5);
        }


        RecyclerView myrecyclerview = root.findViewById(R.id.rv_feed);
        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        myrecyclerview.setAdapter(adapter_feed);


        fab.setOnClickListener(view -> {

            view.startAnimation(new AlphaAnimation(1F, 0.7F));

            Dialog feed_insert_dialog = new Dialog(mContext);
            feed_insert_dialog.setContentView(R.layout.dialog_feed_insert);
            Objects.requireNonNull(feed_insert_dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            feed_insert_dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            final EditText feed_body = feed_insert_dialog.findViewById(R.id.feed_insert_text);

            Button post = feed_insert_dialog.findViewById(R.id.feed_post_btn);
            post.setOnClickListener(v -> {
                v.startAnimation(new AlphaAnimation(1F, 0.7F));

                UserFeed userFeed = new UserFeed();
                userFeed.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                userFeed.setDatacontent(feed_body.getText().toString());

                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();


                DocumentReference docRef = mDb
                        .collection(getString(R.string.collection_feed))
                        .document(getString(R.string.document_global))
                        .collection(getString(R.string.collection_posts))
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

                docRef = mDb
                        .collection(getString(R.string.collection_feed))
                        .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                        .collection(getString(R.string.collection_posts))
                        .document(ts);

                docRef.set(userFeed).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast toast = Toast.makeText(mContext, getString(R.string.feed_post_success), Toast.LENGTH_SHORT);
                            toast.getView().setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.dialog_bg_toast_colored));
                            TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                            toastmsg.setTextColor(Color.WHITE);
                            toast.show();
                            feed_insert_dialog.cancel();

                            Log.d(TAG, "Successfully added to User feed list ");
                        }else{
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


            });
            feed_insert_dialog.show();


        });


        public_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(new AlphaAnimation(1F, 0.7F));
                ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, new fragment_menu_feed("global"), "Global Feed Fragment").commit();
            }
        });

        buddy_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(new AlphaAnimation(1F, 0.7F));
                ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                        .replace(R.id.fragment_container, new fragment_menu_feed("buddy"), "Buddy Feed Fragment").commit();
            }
        });
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        getallfriends();
        if (adapter_feed != null) {
            adapter_feed.startListening();
        }


    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter_feed != null) {
            adapter_feed.stopListening();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDb = FirebaseFirestore.getInstance();

        if(mode.equals("global")){
            CollectionReference collRef = mDb
                    .collection(getString(R.string.collection_feed))
                    .document(getString(R.string.document_global))
                    .collection(getString(R.string.collection_posts));

            Query query = collRef
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(30);

                FirestoreRecyclerOptions<UserFeed> option = new FirestoreRecyclerOptions.Builder<UserFeed>()
                        .setQuery(query, UserFeed.class)
                        .build();
                adapter_feed = new RecyclerViewAdapter_feed(option, mode);

        }else {
            //adapter_feed = null;
            CollectionReference collRef = FirebaseFirestore.getInstance()
                    .collection(getString(R.string.collection_feed))
                    .document(getString(R.string.document_global))
                    .collection(getString(R.string.collection_posts));

            Query query = collRef
                    .whereIn("user_id", MainActivity.FriendList)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(30);

            FirestoreRecyclerOptions<UserFeed> option = new FirestoreRecyclerOptions.Builder<UserFeed>()
                    .setQuery(query, UserFeed.class)
                    .build();
            adapter_feed = new RecyclerViewAdapter_feed(option, mode);


/*
            collRef
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null){
                                Log.e(TAG, "onEvent: Listen failed.", e);
                            }

                            if(queryDocumentSnapshots != null){
                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                                    UserRelation requesteduserRelation = doc.toObject(UserRelation.class);

                                    userFriends.add(requesteduserRelation.getUser_id());



                                }
                            }
                        }
                    });
 */


        }


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

}
