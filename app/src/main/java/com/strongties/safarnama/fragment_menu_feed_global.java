package com.strongties.safarnama;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.strongties.safarnama.adapters.RecyclerViewAdapter_feed;
import com.strongties.safarnama.user_classes.UserFeed;

import java.util.ArrayList;

public class fragment_menu_feed_global extends Fragment {


    private RecyclerViewAdapter_feed adapter_feed = null;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.layout_screen_feed_global, container, false);

        RecyclerView myrecyclerview = root.findViewById(R.id.rv_feed_v2_global);
        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        myrecyclerview.setAdapter(adapter_feed);


        return root;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<String> typeList = new ArrayList<>();
        typeList.add(getString(R.string.public_feed));


        CollectionReference collRef = FirebaseFirestore.getInstance()
                .collection(getString(R.string.collection_feed))
                .document(getString(R.string.document_global))
                .collection(getString(R.string.collection_posts));

        Query query = collRef
                .whereIn("type", typeList)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(30);

        FirestoreRecyclerOptions<UserFeed> option = new FirestoreRecyclerOptions.Builder<UserFeed>()
                .setQuery(query, UserFeed.class)
                .build();
        adapter_feed = new RecyclerViewAdapter_feed(option);

    }

    @Override
    public void onStart() {
        super.onStart();
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
}
