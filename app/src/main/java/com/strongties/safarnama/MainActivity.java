package com.strongties.safarnama;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.strongties.safarnama.background_tasks.AvatarBackgroundTask;
import com.strongties.safarnama.background_tasks.OtherBackgroundTask;
import com.strongties.safarnama.user_classes.RV_Distance;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    Context context;

    //Variables
    private ArrayList<String> mNames = new ArrayList<>();
    public static ArrayList<String> FriendList;

    GoogleSignInClient googleSignInClient;
    public static ArrayList<String> RequestedList;
    public static ArrayList<String> RequestList;
    private static final String SHOWCASE_ID = "1";
    public static ArrayList<String> places_list;
    private ArrayList<String> mImageUrls = new ArrayList<>();
    public static ArrayList<String> places_id_list;
    public static ArrayList<String> bucket_id_list;
    public static ArrayList<String> bucket_list;
    public static ArrayList<String> accomplished_id_list;
    public static ArrayList<String> accomplished_list;
    public static List<RV_Distance> list_hot;
    public static String localState;

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

        FriendList = new ArrayList<>();
        RequestedList = new ArrayList<>();
        RequestList = new ArrayList<>();
        places_list = new ArrayList<>();
        places_id_list = new ArrayList<>();
        bucket_list = new ArrayList<>();
        accomplished_list = new ArrayList<>();
        bucket_id_list = new ArrayList<>();
        accomplished_id_list = new ArrayList<>();
        list_hot = new ArrayList<>();

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
            //  showcaseViewer();
        }


        CardView menu1 = findViewById(R.id.menu1);
        CardView menu2 = findViewById(R.id.menu2);
        CardView menu3 = findViewById(R.id.menu3);
        CardView menu4 = findViewById(R.id.menu4);


        menu1.setOnClickListener(view -> {
            view.startAnimation(new AlphaAnimation(1F, 0.7F));
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.fragment_container, new fragment_menu_googleMap(), "Menu Google map").commit();
        });

        menu2.setOnClickListener(view -> {
            view.startAnimation(new AlphaAnimation(1F, 0.7F));
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                    .replace(R.id.fragment_container, new fragment_menu_distance(), "Menu Distance").commit();
        });

        menu3.setOnClickListener(view -> {
            view.startAnimation(new AlphaAnimation(1F, 0.7F));
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                    .replace(R.id.fragment_container, new fragment_menu_buddies(), "Menu Buddies").commit();
        });

        menu4.setOnClickListener(view -> {
            view.startAnimation(new AlphaAnimation(1F, 0.7F));
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                    .replace(R.id.fragment_container, new fragment_menu_feed(), "Menu Feed").commit();
        });

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

        CollectionReference colRef = FirebaseFirestore.getInstance()
                .collection("Dev")
                .document("developers")
                .collection("admin");

        colRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<String> dev_emails = new ArrayList<>();
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Log.d(TAG, "devs - > " + document.get("email"));
                    dev_emails.add(Objects.requireNonNull(document.get("email")).toString());
                }
                if (dev_emails.contains(email)) {
                    insert_FBFS.setEnabled(true);
                    insert_FBFS.getIcon().setAlpha(255);
                } else {
                    insert_FBFS.setEnabled(false);
                    insert_FBFS.getIcon().setAlpha(0);
                }
            }
        });
/*
        if (!(email.equals("bluebishal@gmail.com") || email.equals("jeevanjyotisahoo1@gmail.com"))) {
            insert_FBFS.setEnabled(false);
            insert_FBFS.getIcon().setAlpha(0);
        } else {
            insert_FBFS.setEnabled(true);
            insert_FBFS.getIcon().setAlpha(255);
        }

 */
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
                Toast toast = Toast.makeText(context, "Feature Coming Soon", Toast.LENGTH_SHORT);
                toast.getView().setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.dialog_bg_toast_colored));
                TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                toastmsg.setTextColor(Color.WHITE);
                toast.show();
                return true;
            case R.id.add_new_places:
                intent = new Intent(this, configurePlacesActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_top, R.anim.exit_to_bottom);
                return true;
            case R.id.contactus:
                /*
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
                            Toast toast = Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT);
                            toast.getView().setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.dialog_bg_toast_colored));
                            TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                            toastmsg.setTextColor(Color.WHITE);
                            toast.show();
                        }

                    }
                });
                myDialog.show();
                 */
                ReviewManager manager = ReviewManagerFactory.create(context);
                com.google.android.play.core.tasks.Task<ReviewInfo> request = manager.requestReviewFlow();
                SharedPreferences prefs = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
                int count = prefs.getInt("reviewcount", 0);
                request.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ReviewInfo reviewInfo = task.getResult();
                        com.google.android.play.core.tasks.Task<Void> flow = manager.launchReviewFlow(MainActivity.this, reviewInfo);
                        flow.addOnSuccessListener(task1 -> {
                            //return

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt("reviewcount", count + 1);
                            editor.apply();
                            if (count > 0) {
                                rateApp();
                            }

                        });
                    } else {
                        // There was some problem
                    }
                });

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

    public void rateApp() {
        try {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        } catch (ActivityNotFoundException e) {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }
}
