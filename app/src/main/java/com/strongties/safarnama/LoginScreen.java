package com.strongties.safarnama;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
                    User user1 = Objects.requireNonNull(task.getResult()).toObject(User.class);
                    assert user1 != null;
                    user1.setLastlogin(null);
                    oldUserRef.set(user1);
                }
            });

            Intent myIntent = new Intent(LoginScreen.this, WalkThroughActivity.class);
            // myIntent.putExtra("key", value); //Optional parameters
            LoginScreen.this.startActivity(myIntent);
            LoginScreen.this.finish();
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
        if(user != null){
            String email = user.getEmail();
            //String name = email.substring(0, email.indexOf("@"));
            String name = user.getDisplayName();
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
