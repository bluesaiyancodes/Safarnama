package com.strongties.safarnama;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.strongties.safarnama.adapters.RecyclerViewAdapter_explore;
import com.strongties.safarnama.user_classes.RV_Explore;

import java.util.ArrayList;
import java.util.List;

public class RV_ExploreFragment extends Fragment {

    View v;
    private RecyclerView myrecyclerview;
    private List<RV_Explore> lstExplore;
    DatabaseHelper dbhelper;


    public static RV_ExploreFragment newInstance(int page) {
        RV_ExploreFragment rv_exploreFragment = new RV_ExploreFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
       // args.putString("someTitle", title);
        rv_exploreFragment.setArguments(args);
        return rv_exploreFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_lists_explore,container,false);
        myrecyclerview = v.findViewById(R.id.explore_recyclerview);
        RecyclerViewAdapter_explore recyclerAdapter = new RecyclerViewAdapter_explore(getContext(),lstExplore);
        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        myrecyclerview.setAdapter(recyclerAdapter);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
     }
}
