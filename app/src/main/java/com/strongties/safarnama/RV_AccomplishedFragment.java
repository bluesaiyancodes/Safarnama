package com.strongties.safarnama;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.strongties.safarnama.adapters.RecyclerViewAdapter_accomplished;
import com.strongties.safarnama.user_classes.LandmarkList;
import com.strongties.safarnama.user_classes.LandmarkMeta;
import com.strongties.safarnama.user_classes.LandmarkStat;
import com.strongties.safarnama.user_classes.UserFeed;

import java.util.Objects;

import static com.strongties.safarnama.MainActivity.accomplished_id_list;
import static com.strongties.safarnama.MainActivity.accomplished_list;

public class RV_AccomplishedFragment extends Fragment {
    private static final String TAG = "AccomFrag";
    View v;
    private RecyclerView myrecyclerview;


    Context mContext;
    FirebaseFirestore mDb;
    private ProgressDialog mProgressDialog;

    private RecyclerViewAdapter_accomplished adapter_accomplished = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_lists_accomplished, container, false);


        mContext = getContext();
        mDb = FirebaseFirestore.getInstance();


        myrecyclerview = v.findViewById(R.id.accomplished_recyclerview);
        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        myrecyclerview.setAdapter(adapter_accomplished);


        // Auto complete text view
        AutoCompleteTextView accomplished_search = v.findViewById(R.id.accomplished_actv);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, accomplished_list);
        accomplished_search.setAdapter(adapter);

        // Place Search Action
        Button search_btn = v.findViewById(R.id.accomplished_search_btn);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(new AlphaAnimation(1F, 0.7F));
                //Set Progressbar
                mProgressDialog = ProgressDialog.show(mContext, "Searching", "Fetching Information from Server");
                mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside

                String inputPlace = accomplished_search.getText().toString();
                String inputPlaceID = "null";
                try {
                    inputPlaceID = accomplished_id_list.get(accomplished_list.indexOf(inputPlace));
                } catch (ArrayIndexOutOfBoundsException e) {
                    Toast toast = Toast.makeText(mContext, mContext.getString(R.string.place_not_found), Toast.LENGTH_SHORT);
                    toast.getView().setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.dialog_bg_toast_colored));
                    TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                    toastmsg.setTextColor(Color.WHITE);
                    toast.show();
                    mProgressDialog.dismiss();
                }
                Log.d(TAG, "PlaceID -> " + inputPlaceID);

                DocumentReference docRef = FirebaseFirestore.getInstance()
                        .collection(getString(R.string.collection_landmarks))
                        .document(getString(R.string.document_meta))
                        .collection(getString(R.string.collection_all))
                        .document(inputPlaceID);

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            assert document != null;
                            if (document.exists()) {
                                LandmarkMeta landmarkMeta = document.toObject(LandmarkMeta.class);

                                //Dialog Initiation
                                Dialog myDialog = new Dialog(mContext);
                                myDialog.setContentView(R.layout.rv_dialog_accomplished);
                                Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


                                TextView dialog_placename_tv = myDialog.findViewById(R.id.dialog_accomplished_place_name);
                                TextView dialog_description_tv = myDialog.findViewById(R.id.dialog_accomplished_description);
                                TextView dialog_type_tv = myDialog.findViewById(R.id.dialog_accomplished_type);
                                ImageView dialog_img = myDialog.findViewById(R.id.dialog_accomplished_img);

                                ImageButton btn_wish = myDialog.findViewById(R.id.dialog_accomplished_addtowishlist);
                                ImageButton btn_cmpl = myDialog.findViewById(R.id.dialog_accomplished_removecompletelist);
                                Button btn_details = myDialog.findViewById(R.id.dialog_accomplished_btn_details);

                                assert landmarkMeta != null;
                                dialog_placename_tv.setText(landmarkMeta.getLandmark().getName());
                                dialog_description_tv.setText(landmarkMeta.getLandmark().getLong_desc());
                                dialog_type_tv.setText(landmarkMeta.getLandmark().getCategory());


                                Glide.with(mContext)
                                        .load(landmarkMeta.getLandmark().getImg_url())
                                        .placeholder(R.drawable.loading_image)
                                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                                        .into(dialog_img);

                                //dialog_img.setImageResource(mData.get(vHolder.getAdapterPosition()).getPhoto());


                                btn_wish.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        v.startAnimation(new AlphaAnimation(1F, 0.7F));
                                        DocumentReference bucketRef = FirebaseFirestore.getInstance()
                                                .collection(mContext.getString(R.string.collection_users))
                                                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                                .collection(mContext.getString(R.string.collection_bucket_list))
                                                .document(landmarkMeta.getId());

                                        LandmarkList landmarkList = new LandmarkList();
                                        landmarkList.setLandmarkMeta(landmarkMeta);

                                        bucketRef.set(landmarkList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast toast = Toast.makeText(mContext, mContext.getString(R.string.wishlistadd), Toast.LENGTH_SHORT);
                                                    toast.getView().setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.dialog_bg_toast_colored));
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

                                });

                                btn_cmpl.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        v.startAnimation(new AlphaAnimation(1F, 0.7F));

                                        DocumentReference bucketRef = FirebaseFirestore.getInstance()
                                                .collection(mContext.getString(R.string.collection_users))
                                                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                                .collection(mContext.getString(R.string.collection_accomplished_list))
                                                .document(landmarkMeta.getId());

                                        LandmarkList landmarkList = new LandmarkList();
                                        landmarkList.setLandmarkMeta(landmarkMeta);

                                        bucketRef.set(landmarkList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast toast = Toast.makeText(mContext, mContext.getString(R.string.accomplishlistadd), Toast.LENGTH_SHORT);
                                                    toast.getView().setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.dialog_bg_toast_colored));
                                                    TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                                                    toastmsg.setTextColor(Color.WHITE);
                                                    toast.show();
                                                }
                                            }
                                        });


                                        UserFeed userFeed = new UserFeed();
                                        userFeed.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                                        String msgBuilder = mContext.getString(R.string.feed_msg_accomplished) + landmarkMeta.getLandmark().getName();
                                        msgBuilder += "\n \n" + landmarkMeta.getLandmark().getLong_desc();

                                        userFeed.setDatacontent(msgBuilder);

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

                                    }
                                });

                                btn_details.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        v.startAnimation(new AlphaAnimation(1F, 0.7F));
                                        Intent intent = new Intent(mContext, LandmarkActivity.class);
                                        Bundle args = new Bundle();
                                        args.putString("state", landmarkMeta.getState());
                                        args.putString("district", landmarkMeta.getdistrict());
                                        args.putString("id", landmarkMeta.getId());
                                        intent.putExtras(args);
                                        mContext.startActivity(intent);
                                        ((Activity) mContext).overridePendingTransition(R.anim.enter_from_top, R.anim.exit_to_bottom);
                                    }
                                });

                                mProgressDialog.dismiss();
                                myDialog.show();

                                accomplished_search.setText("");


                            }
                        } else {
                            Toast toast = Toast.makeText(mContext, mContext.getString(R.string.place_not_found), Toast.LENGTH_SHORT);
                            toast.getView().setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.dialog_bg_toast_colored));
                            TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                            toastmsg.setTextColor(Color.WHITE);
                            toast.show();
                            mProgressDialog.dismiss();
                        }
                    }
                });


            }
        });


        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (adapter_accomplished!= null) {
            adapter_accomplished.startListening();
        }


    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter_accomplished != null) {
            adapter_accomplished.stopListening();
        }

    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        mDb = FirebaseFirestore.getInstance();

        CollectionReference collRef = mDb
                .collection(mContext.getString(R.string.collection_users))
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection(mContext.getString(R.string.collection_accomplished_list));

        Query query = collRef
                .orderBy("timestamp", Query.Direction.DESCENDING);


        FirestoreRecyclerOptions<LandmarkList> option = new FirestoreRecyclerOptions.Builder<LandmarkList>()
                .setQuery(query, LandmarkList.class)
                .build();
        adapter_accomplished = new RecyclerViewAdapter_accomplished(option);
    }
}
