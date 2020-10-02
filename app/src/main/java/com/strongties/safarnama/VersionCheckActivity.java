package com.strongties.safarnama;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.strongties.safarnama.background_tasks.preBackgroundTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class VersionCheckActivity extends AppCompatActivity {
    private static final String TAG = "VersionCheck";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_check);

        //run pre background task
        preBackgroundTask prebackgroundTask = new preBackgroundTask(getApplicationContext());
        prebackgroundTask.execute();


        //Version Check
        ProgressDialog mProgressDialog = ProgressDialog.show(this, "Version Check", "Checking for Updates");
        mProgressDialog.setCanceledOnTouchOutside(false);
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection(getString(R.string.collection_maintainance))
                .document(getString(R.string.document_version));

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mProgressDialog.cancel();
                DocumentSnapshot document = task.getResult();
                assert document != null;
                int versionCode = BuildConfig.VERSION_CODE;
                int cloudVersion = Objects.requireNonNull(document.getLong("id")).intValue();

                if (cloudVersion > versionCode) {

                    ImageView imageView = findViewById(R.id.version_check_imageView);
                    imageView.setVisibility(View.VISIBLE);


                    new AlertDialog.Builder(VersionCheckActivity.this)
                            .setTitle(getString(R.string.outdated_version))
                            .setMessage(getString(R.string.outdated_version_message))
                            .setPositiveButton(getString(R.string.playstore), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                    }

                                }
                            })
                            .setNegativeButton(getString(R.string.dev_support), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    Dialog myDialog = new Dialog(VersionCheckActivity.this);
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
                                                Toast toast = Toast.makeText(VersionCheckActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT);
                                                toast.getView().setBackground(ContextCompat.getDrawable(VersionCheckActivity.this, R.drawable.dialog_bg_toast_colored));
                                                TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                                                toastmsg.setTextColor(Color.WHITE);
                                                toast.show();
                                            }

                                        }
                                    });
                                    myDialog.show();

                                }
                            })
                            .setIcon(R.drawable.app_main_icon)
                            .show();
                } else {
                    // Maintenance Check

                    DocumentReference documentReference = FirebaseFirestore.getInstance()
                            .collection(getString(R.string.collection_maintainance))
                            .document(getString(R.string.document_db_maintenance));

                    documentReference.get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Boolean maintenance = task1.getResult().getBoolean("flag");
                            if (!maintenance) {

                                Intent mainactivity = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(mainactivity);
                                finish();
                            } else {
                                // Dev Entry Only
                                DocumentReference docRef1 = FirebaseFirestore.getInstance()
                                        .collection("Dev")
                                        .document("developers")
                                        .collection("admin")
                                        .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                                docRef1.get().addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        DocumentSnapshot document1 = task2.getResult();
                                        if (document1.exists()) {
                                            new AlertDialog.Builder(VersionCheckActivity.this)
                                                    .setTitle(getString(R.string.maintenance))
                                                    .setMessage(getString(R.string.maintenance_msg_dev))
                                                    .setPositiveButton(getString(R.string.okay), (dialogInterface, i) -> {
                                                        //dismis alert dialog
                                                        dialogInterface.dismiss();
                                                        //start main activity
                                                        Intent mainActivity = new Intent(VersionCheckActivity.this, MainActivity.class);
                                                        startActivity(mainActivity);
                                                        //finish activity
                                                        finish();
                                                    })
                                                    .setIcon(R.drawable.app_main_icon)
                                                    .show();
                                        } else {
                                            new AlertDialog.Builder(VersionCheckActivity.this)
                                                    .setTitle(getString(R.string.maintenance))
                                                    .setMessage(getString(R.string.maintenance_msg))
                                                    .setPositiveButton(getString(R.string.okay), (dialogInterface, i) -> {
                                                        //dismis alert dialog
                                                        dialogInterface.dismiss();
                                                        //finish activity
                                                        finish();
                                                    })
                                                    .setIcon(R.drawable.app_main_icon)
                                                    .show();
                                        }
                                    }
                                });


                            }
                        }
                    });


                }
            }
        });


    }

}