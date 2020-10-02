package com.strongties.safarnama;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.strongties.safarnama.user_classes.User;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import pl.droidsonroids.gif.GifDrawable;

public class LoginScreen extends AppCompatActivity {
        public static final String TAG = "LoginScreen";

        static final int GOOGLE_SIGN =123;
        FirebaseAuth firebaseAuth;
        Button login_btn;
        ImageView imageView;

        ProgressBar progressBar;
        GoogleSignInClient googleSignInClient;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login_screen);


            progressBar = findViewById(R.id.login_progressbar);
            imageView = findViewById(R.id.login_img);
            login_btn = findViewById(R.id.login_btn);
            TextView tv_login = findViewById(R.id.login_text);

            imageView.setVisibility(View.GONE);
            login_btn.setVisibility(View.GONE);
            tv_login.setVisibility(View.GONE);

            firebaseAuth = FirebaseAuth.getInstance();

            GifDrawable gifDrawable = null;
            try {
                gifDrawable = new GifDrawable(getResources(), R.raw.app_icon_animated);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageDrawable(gifDrawable);



            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                    .Builder()
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

            login_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoginScreen.this.SignInGoogle();
                }
            });

            if(firebaseAuth.getCurrentUser() != null) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Toast toast = Toast.makeText(this, "Signin Success", Toast.LENGTH_SHORT);
                toast.getView().setBackground(ContextCompat.getDrawable(LoginScreen.this, R.drawable.dialog_bg_toast_colored));
                TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                toastmsg.setTextColor(Color.WHITE);
                // toast.show();

                DocumentReference oldUserRef = FirebaseFirestore.getInstance()
                        .collection(getString(R.string.collection_users))
                        .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

                oldUserRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            User user1 = Objects.requireNonNull(task.getResult()).toObject(User.class);
                            assert user1 != null;
                            user1.setLastlogin(null);
                            oldUserRef.set(user1);
                            Intent myIntent = new Intent(LoginScreen.this, WalkThroughActivity.class);
                            // myIntent.putExtra("key", value); //Optional parameters
                            LoginScreen.this.startActivity(myIntent);
                            LoginScreen.this.finish();

                        } else {
                            new AlertDialog.Builder(LoginScreen.this)
                                    .setTitle(getString(R.string.outdated_version))
                                    .setMessage(getString(R.string.outdated_version_message))
                                    .setPositiveButton(getString(R.string.playstore), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                            try {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                dialog.dismiss();
                                                finish();
                                            } catch (android.content.ActivityNotFoundException anfe) {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                dialog.dismiss();
                                                finish();
                                            }

                                        }
                                    })
                                    .setNegativeButton(getString(R.string.dev_support), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            Dialog myDialog = new Dialog(LoginScreen.this);
                                            myDialog.setContentView(R.layout.dialog_contact_us);
                                            myDialog.setCancelable(false);
                                            myDialog.setCanceledOnTouchOutside(false);
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
                                                        myDialog.dismiss();
                                                        finish();
                                                    } catch (android.content.ActivityNotFoundException ex) {
                                                        Toast.makeText(LoginScreen.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }

                                                }
                                            });
                                            myDialog.show();

                                        }
                                    })
                                    .setIcon(R.drawable.app_main_icon)
                                    .setCancelable(false)
                                    .show();
                        }
                    }
                });


            } else {
                tv_login.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                login_btn.setVisibility(View.VISIBLE);
            }



    }

    void SignInGoogle(){
        progressBar.setVisibility(View.VISIBLE);
        Intent signinIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signinIntent, GOOGLE_SIGN);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GOOGLE_SIGN){
            Task<GoogleSignInAccount> task = GoogleSignIn
                    .getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if(account != null){
                    firebaseAuthwithGoogle(account);
                }

            }catch (ApiException e){
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthwithGoogle(GoogleSignInAccount account){
        Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.d("TAG", "signInWithCredential:success");
                            Toast toast = Toast.makeText(LoginScreen.this, "Signin Success", Toast.LENGTH_SHORT);
                            toast.getView().setBackground(ContextCompat.getDrawable(LoginScreen.this, R.drawable.dialog_bg_toast_colored));
                            TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                            toastmsg.setTextColor(Color.WHITE);
                            // toast.show();

                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            LoginScreen.this.writeFirebase(user);


                            Intent myIntent = new Intent(LoginScreen.this, WalkThroughActivity.class);
                            // myIntent.putExtra("key", value); //Optional parameters
                            LoginScreen.this.startActivity(myIntent);
                            LoginScreen.this.finish();

                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.d("TAG", "signInWithCredential:Failure", task.getException());


                            Toast toast = Toast.makeText(LoginScreen.this, "Signin Failed", Toast.LENGTH_SHORT);
                            toast.getView().setBackground(ContextCompat.getDrawable(LoginScreen.this, R.drawable.dialog_bg_toast_colored));
                            TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                            toastmsg.setTextColor(Color.WHITE);
                            toast.show();


                        }
                    }
                });

    }

    private void writeFirebase(FirebaseUser user) {
        if(user != null) {
            String email = user.getEmail();
            assert email != null;
            String name = email.substring(0, email.indexOf("@"));
            String photo = String.valueOf(user.getPhotoUrl());
            String uid = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            User user_ = new User();
            user_.setUsername(name);
            user_.setEmail(email);
            user_.setPhoto(photo);
            user_.setUser_id(uid);
            user_.setAvatar(getString(R.string.avatar_0));
            user_.setDateofjoin(null);
            user_.setLastlogin(null);

            DocumentReference newUserRef = db
                    .collection(getString(R.string.collection_users))
                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

            newUserRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        //do nothing
                        User user1 = documentSnapshot.toObject(User.class);
                        assert user1 != null;
                        user1.setLastlogin(null);
                        DocumentReference docRef = db
                                .collection(getString(R.string.collection_users))
                                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                        docRef.set(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // do nothing
                            }
                        });
                    } else {
                        newUserRef.set(user_);
                    }
                }
            });


            DocumentReference userRef = db.collection(getString(R.string.collection_users))
                    .document(user.getUid());
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: successfully set the user client.");
                        User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                        ((UserClient)(getApplicationContext())).setUser(user);
                    }
                }
            });


            //to load image into an image view
            //Picasso.with(LoginActivity.this).load(photo).into(image);
        }
    }

    private void Logout() {
        FirebaseAuth.getInstance().signOut();
        googleSignInClient.signOut();
    }
}
