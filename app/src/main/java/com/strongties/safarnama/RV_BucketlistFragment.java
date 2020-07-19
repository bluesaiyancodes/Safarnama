package com.strongties.safarnama;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.strongties.safarnama.adapters.RecyclerViewAdapter_bucketlist;
import com.strongties.safarnama.user_classes.LandmarkList;

import java.util.Objects;

public class RV_BucketlistFragment extends Fragment {

    View v;
    private RecyclerView myrecyclerview;

    Context mContext;
    FirebaseFirestore mDb;

    private RecyclerViewAdapter_bucketlist adapter_bucketlist = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_lists_bucketlist,container,false);


        mContext = getContext();
        mDb = FirebaseFirestore.getInstance();


        myrecyclerview = v.findViewById(R.id.bucketlist_recyclerview);
        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        myrecyclerview.setAdapter(adapter_bucketlist);



        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (adapter_bucketlist!= null) {
            adapter_bucketlist.startListening();
        }


    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter_bucketlist != null) {
            adapter_bucketlist.stopListening();
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
                .collection(mContext.getString(R.string.collection_bucket_list));

        Query query = collRef
                .orderBy("timestamp", Query.Direction.DESCENDING);


        FirestoreRecyclerOptions<LandmarkList> option = new FirestoreRecyclerOptions.Builder<LandmarkList>()
                .setQuery(query, LandmarkList.class)
                .build();
        adapter_bucketlist = new RecyclerViewAdapter_bucketlist(option);




    }
}
