package com.strongties.safarnama;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.strongties.safarnama.adapters.RecyclerViewAdaptor_menu;
import com.strongties.safarnama.user_classes.AvatarBackgroundTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    Context context;

    //Variables
    private ArrayList<String> mNames = new ArrayList<>();
    public static ArrayList<String> FriendList;

    GoogleSignInClient googleSignInClient;
    public static ArrayList<String> RequestedList;
    private ArrayList<String> mImageUrls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_app_name);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new fragment_menu_googleMap(), "Google Map Fragment").commit();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        super.onStart();


        getImages();

        FriendList = new ArrayList<>();
        RequestedList = new ArrayList<>();

        AvatarBackgroundTask avatarBackgroundTask = new AvatarBackgroundTask(context);
        avatarBackgroundTask.execute();

        Log.d(TAG, "FriendList" + FriendList.toString());
        Log.d(TAG, "RequestList" + RequestedList.toString());


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.commonmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem insert_FBFS = menu.findItem(R.id.add_new_places);
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();


        MenuItem item = menu.findItem(R.id.app_mode);

        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                item.setTitle(getString(R.string.mode_light));
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                item.setTitle(getString(R.string.mode_dark));
                break;
        }

        if (!(email.equals("bluebishal@gmail.com") || email.equals("jeevanjyotisahoo1@gmail.com"))) {
            insert_FBFS.setEnabled(false);
            insert_FBFS.getIcon().setAlpha(0);
        } else {
            insert_FBFS.setEnabled(true);
            insert_FBFS.getIcon().setAlpha(255);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.wishlist:
                Intent intent = new Intent(this, WishlistActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_top, R.anim.exit_to_bottom);
                return true;
            case R.id.app_mode:
                switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                    case Configuration.UI_MODE_NIGHT_YES:
                        item.setTitle(getString(R.string.mode_light));
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;
                    case Configuration.UI_MODE_NIGHT_NO:
                        item.setTitle(getString(R.string.mode_dark));
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        break;
                }
                invalidateOptionsMenu();
                return true;
            case R.id.add_new_places:
                intent = new Intent(this, configurePlacesActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_top, R.anim.exit_to_bottom);
                return true;
            case R.id.contactus:
                Dialog myDialog = new Dialog(MainActivity.this);
                myDialog.setContentView(R.layout.dialog_contact_us);
                Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                Button contact_submit = myDialog.findViewById(R.id.contact_btn);
                final EditText contact_body = myDialog.findViewById(R.id.contact_body);
                contact_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        final String currentDate = sdf.format(new Date());

                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.support_email)});
                        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_sub) + currentDate);
                        i.putExtra(Intent.EXTRA_TEXT, contact_body.getText().toString());
                        try {
                            startActivity(Intent.createChooser(i, getString(R.string.support_title)));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                myDialog.show();
                return super.onOptionsItemSelected(item);
            case R.id.logout:
                googleSignInClient.signOut()
                        .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(context, LoginScreen.class);
                                startActivity(intent);
                                MainActivity.this.finish();

                            }
                        });
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void getImages() {
        Log.d(TAG, "initImageBitmaps: preparing bitmaps.");


        mImageUrls.add("https://i.redd.it/tpsnoz5bzo501.jpg");
        mNames.add(getString(R.string.menu_1));

        mImageUrls.add("https://i.redd.it/qn7f9oqu7o501.jpg");
        mNames.add(getString(R.string.menu_2));

        mImageUrls.add("https://i.redd.it/j6myfqglup501.jpg");
        mNames.add(getString(R.string.menu_3));

        mImageUrls.add("https://i.redd.it/k98uzl68eh501.jpg");
        mNames.add("Feed");

        mImageUrls.add("https://i.redd.it/0h2gm1ix6p501.jpg");
        mNames.add(getString(R.string.menu_4));


        initRecyclerView();

    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdaptor_menu adapter = new RecyclerViewAdaptor_menu(this, mNames, mImageUrls);
        recyclerView.setAdapter(adapter);

        Log.d(TAG, "Screen Height: " + getScreenWidth());


    }


    public static float getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels / Resources.getSystem().getDisplayMetrics().density;
    }

    public static float getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels / Resources.getSystem().getDisplayMetrics().density;
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm))
                .setMessage("Do you really want to exit the Application ? ")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        // System.exit(0);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }
}
