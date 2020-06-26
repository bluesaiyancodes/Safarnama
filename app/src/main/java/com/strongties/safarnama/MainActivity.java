package com.strongties.safarnama;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.strongties.safarnama.adapters.RecyclerViewAdaptor_menu;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    Context context;

    //Variables
    private ArrayList<String> mNames = new ArrayList<>();
    private  ArrayList<String> mImageUrls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new fragment_menu_googleMap(), "Google Map Fragment").commit();


        getImages();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.commonmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.wishlist:
                Intent intent = new Intent(this, TransitionActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_top,R.anim.exit_to_bottom);
                return true;
            case R.id.contactus:
                context = getBaseContext();
                AlertDialog.Builder builder_ = new AlertDialog.Builder(this);
                builder_.setTitle("Contact Us");
                builder_.setMessage("demo");
                builder_.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void getImages(){
        Log.d(TAG, "initImageBitmaps: preparing bitmaps.");


        mImageUrls.add("https://i.redd.it/tpsnoz5bzo501.jpg");
        mNames.add(getString(R.string.menu_1));

        mImageUrls.add("https://i.redd.it/qn7f9oqu7o501.jpg");
        mNames.add("Item 2");

        mImageUrls.add("https://i.redd.it/j6myfqglup501.jpg");
        mNames.add("Item 3");


        mImageUrls.add("https://i.redd.it/0h2gm1ix6p501.jpg");
        mNames.add("Item 4");

        mImageUrls.add("https://i.redd.it/k98uzl68eh501.jpg");
        mNames.add("Item 5");


        initRecyclerView();

    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerview");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdaptor_menu adapter = new RecyclerViewAdaptor_menu(this, mNames, mImageUrls);
        recyclerView.setAdapter(adapter);
    }




}
