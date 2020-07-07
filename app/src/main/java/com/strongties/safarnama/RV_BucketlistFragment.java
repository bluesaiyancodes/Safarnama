package com.strongties.safarnama;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.strongties.safarnama.adapters.RecyclerViewAdapter_bucketlist;
import com.strongties.safarnama.user_classes.RV_Bucketlist;

import java.util.ArrayList;
import java.util.List;

public class RV_BucketlistFragment extends Fragment {

    View v;
    private RecyclerView myrecyclerview;
    private List<RV_Bucketlist> list_Bucketlist;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_lists_bucketlist,container,false);
        myrecyclerview = v.findViewById(R.id.bucketlist_recyclerview);
        RecyclerViewAdapter_bucketlist recyclerAdapter = new RecyclerViewAdapter_bucketlist(getContext(),list_Bucketlist);
        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        myrecyclerview.setAdapter(recyclerAdapter);


        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DatabaseHelper dbhelper = new DatabaseHelper(getContext());
        SQLiteDatabase database = dbhelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT id, name, city, district, description, type, url, date, visit FROM LANDMARKS ORDER BY name ASC", new String[]{});

        list_Bucketlist = new ArrayList<>();


        if(cursor != null){
            cursor.moveToFirst();
        }
        do{
            if(cursor.getString(8).equals("tovisit")) {
                list_Bucketlist.add(new RV_Bucketlist(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7)));
            }
        }while (cursor.moveToNext());
        //lstExplore.add(new Explore("Jagannath Temple","Puri","Odisha",R.string.PuriDescription, R.drawable.puri));
    }
}
