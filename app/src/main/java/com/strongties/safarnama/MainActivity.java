package com.strongties.safarnama;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.strongties.safarnama.adapters.RecyclerViewAdaptor_menu;
import com.strongties.safarnama.background_tasks.AvatarBackgroundTask;
import com.strongties.safarnama.background_tasks.OtherBackgroundTask;

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
    private static final String SHOWCASE_ID = "1";
    public static ArrayList<String> places_list;
    private ArrayList<String> mImageUrls = new ArrayList<>();
    public static ArrayList<String> places_id_list;

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
        places_list = new ArrayList<>();
        places_id_list = new ArrayList<>();

        //add places into places ArrayList
        OtherBackgroundTask otherBackgroundTask = new OtherBackgroundTask(context);
        otherBackgroundTask.execute();

        // Fetch User Avatar details in Background
        AvatarBackgroundTask avatarBackgroundTask = new AvatarBackgroundTask(context);
        avatarBackgroundTask.execute();

        Log.d(TAG, "FriendList" + FriendList.toString());
        Log.d(TAG, "RequestList" + RequestedList.toString());

        //Firsttime usage
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        if (pref.getBoolean("first_run", true)) {
            showcaseViewer();
        }


    }

    private void showcaseViewer() {


        //This code creates a new layout parameter so the button in the showcase can move to a new spot.
        final RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // This aligns button to the bottom left side of screen
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.CENTER_HORIZONTAL);
        // Set margins to the button, we add 16dp margins here
        int margin = ((Number) (getResources().getDisplayMetrics().density * 16)).intValue();
        lps.setMargins(margin, margin, margin, margin+250);


        //This creates the first showcase.
        ShowcaseView showCase = new ShowcaseView.Builder(this)
                .withMaterialShowcase()
                .setTarget(new ViewTarget( findViewById(R.id.menu1_mixed)))
                .setContentTitle("Mixed Map Filter")
                .setContentText("Show all the markers from Bucket List, Acomplished Lists, and also many Known-Unknown places")
                .setStyle(R.style.CustomShowcaseTheme)
                .build();
        showCase.setButtonText("next");
        showCase.setButtonPosition(lps);


        //When the button is clicked then the switch statement will check the counter and make the new showcase.
        showCase.overrideButtonClick(new View.OnClickListener() {
                                         int count1 = 0;

                                         @Override
                                         public void onClick(View v) {
                                             count1++;
                                             switch (count1) {
                                                 default:
                                                     showCase.hide();
                                                     break;
                                                 case 1:
                                                     showCase.setTarget(new ViewTarget(findViewById(R.id.menu1_explore)));
                                                     showCase.setContentTitle("Explore Map Filter");
                                                     showCase.setContentText("This Filter shows new places that you have not visited before. The places that are not present in your Bucket List or Accomplished List.");
                                                     showCase.setButtonText("next");
                                                     break;
                                                 case 2:
                                                     showCase.setTarget(new ViewTarget(findViewById(R.id.menu1_wish)));
                                                     showCase.setContentTitle("Bucket List Map Filter");
                                                     showCase.setContentText("This filter shows all the places that you have in your bucket list.");
                                                     showCase.setButtonText("next");
                                                     break;
                                                 case 3:
                                                     showCase.setTarget(new ViewTarget(findViewById(R.id.menu1_accomplish)));
                                                     showCase.setContentTitle("Accomplished List Map Filter");
                                                     showCase.setContentText("This filter shows all the places that the user has already visited. These are the places that are present in the Accomplished List.");
                                                     showCase.setButtonText("Close");
                                                     break;

                                             }
                                         }
        });

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("first_run", false);
        editor.apply();


        /*
        ViewTarget target = new ViewTarget( findViewById(R.id.menu1_mixed));
        new ShowcaseView.Builder(this)
                .setTarget(target)
                .setContentTitle("Mixed Map Filter")
                .setContentText("Show all the markers from Bucket List, Acomplished Lists, and also many Known-Unknown places")
                .setStyle(R.style.CustomShowcaseTheme_next)
                .build();

         */


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
            case R.id.walkthrough:
                intent = new Intent(this, WalkThroughActivity.class);
                intent.putExtra("forcedShow", "false");
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                return true;
            case R.id.app_mode:
                /*
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
                 */
                Toast.makeText(context, "Feature Coming Soon", Toast.LENGTH_SHORT).show();
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
