package com.strongties.safarnama;

import android.content.Context;
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
import com.strongties.safarnama.adapters.RecyclerViewAdapter_explore;
import com.strongties.safarnama.user_classes.LandmarkMeta;

public class RV_ExploreFragment extends Fragment {

    View v;
    private RecyclerView myrecyclerview;

    Context mContext;
    FirebaseFirestore mDb;

    private RecyclerViewAdapter_explore adapter_explore = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_lists_explore,container,false);
        mContext = getContext();
        mDb = FirebaseFirestore.getInstance();



        myrecyclerview = v.findViewById(R.id.explore_recyclerview);
        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        myrecyclerview.setAdapter(adapter_explore);

        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (adapter_explore!= null) {
            adapter_explore.startListening();
        }


    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter_explore != null) {
            adapter_explore.stopListening();
        }

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mDb = FirebaseFirestore.getInstance();


        CollectionReference collRef = mDb
                .collection(getString(R.string.collection_landmarks))
                .document(getString(R.string.document_meta))
                .collection(getString(R.string.collection_all));

        Query query = collRef
                .orderBy("city", Query.Direction.ASCENDING);


        FirestoreRecyclerOptions<LandmarkMeta> option = new FirestoreRecyclerOptions.Builder<LandmarkMeta>()
                .setQuery(query, LandmarkMeta.class)
                .build();
        adapter_explore = new RecyclerViewAdapter_explore(option);



        /*
        *
        *  Old Database Queries


        dbhelper = new DatabaseHelper(getContext());
        SQLiteDatabase database = dbhelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT id, name, city, district, description, type, url, visit FROM LANDMARKS ORDER BY name ASC", new String[]{});

        lstExplore = new ArrayList<>();

        if(cursor != null){
            cursor.moveToFirst();
        }
        do{
            lstExplore.add(new RV_Explore(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7)));
        }while (cursor.moveToNext());
        //lstExplore.add(new Explore("Jagannath Temple","Puri","Odisha",R.string.PuriDescription, R.drawable.puri));

         */
     }
}
