package com.strongties.safarnama.background_tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.strongties.safarnama.R;
import com.strongties.safarnama.user_classes.User;

import java.util.Objects;

public class AvatarBackgroundTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "Avatar BG";

    private Context mContext;

    public AvatarBackgroundTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // do some background work here

        Log.d(TAG, "Starting Tasks");


        CollectionReference collRef = FirebaseFirestore.getInstance()
                .collection(mContext.getString(R.string.collection_users))
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection(mContext.getString(R.string.collection_accomplished_list));

        collRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                count++;
                            }


                            if (count<10 && count>=1){
                                DocumentReference docRef =FirebaseFirestore.getInstance()
                                        .collection(mContext.getString(R.string.collection_users))
                                        .document(FirebaseAuth.getInstance().getUid());

                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()) {
                                            DocumentSnapshot doc = task.getResult();
                                            assert doc != null;
                                            User user = doc.toObject(User.class);
                                            assert user != null;
                                            user.setAvatar(mContext.getString(R.string.avatar_1));
                                            if (user.getEmail().equals("bluebishal@gmail.com") || user.getEmail().equals("subratkumarpatasani023@gmail.com") || user.getEmail().equals("jeevanjyotisahoo1@gmail.com") || user.getEmail().equals("sudeshkumarsahoo98@gmail.com") || user.getEmail().equals("paltasingh10@gmail.com")) {
                                                Log.d(TAG, "Developer Mode inside If");
                                                user.setAvatar(mContext.getString(R.string.avatar_dev));
                                            }

                                            DocumentReference documentReference = FirebaseFirestore.getInstance()
                                                    .collection(mContext.getString(R.string.collection_users))
                                                    .document(FirebaseAuth.getInstance().getUid());

                                            documentReference.set(user);


                                        }
                                    }
                                });

                            }else if (count <30 && count >= 10){
                                DocumentReference docRef =FirebaseFirestore.getInstance()
                                        .collection(mContext.getString(R.string.collection_users))
                                        .document(FirebaseAuth.getInstance().getUid());

                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()) {
                                            DocumentSnapshot doc = task.getResult();
                                            assert doc != null;
                                            User user = doc.toObject(User.class);
                                            assert user != null;
                                            user.setAvatar(mContext.getString(R.string.avatar_2));
                                            if (user.getEmail().equals("bluebishal@gmail.com") || user.getEmail().equals("subratkumarpatasani0023@gmail.com") || user.getEmail().equals("jeevanjyotisahoo1@gmail.com") || user.getEmail().equals("sudeshkumarsahoo98@gmail.com") || user.getEmail().equals("paltasingh10@gmail.com")) {
                                                Log.d(TAG, "Developer Mode inside If");
                                                user.setAvatar(mContext.getString(R.string.avatar_dev));
                                            }

                                            DocumentReference documentReference = FirebaseFirestore.getInstance()
                                                    .collection(mContext.getString(R.string.collection_users))
                                                    .document(FirebaseAuth.getInstance().getUid());

                                            documentReference.set(user);


                                        }
                                    }
                                });
                            }else if (count <50 && count >= 30){
                                DocumentReference docRef =FirebaseFirestore.getInstance()
                                        .collection(mContext.getString(R.string.collection_users))
                                        .document(FirebaseAuth.getInstance().getUid());

                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()) {
                                            DocumentSnapshot doc = task.getResult();
                                            assert doc != null;
                                            User user = doc.toObject(User.class);
                                            assert user != null;
                                            user.setAvatar(mContext.getString(R.string.avatar_3));
                                            if (user.getEmail().equals("bluebishal@gmail.com") || user.getEmail().equals("subratkumarpatasani0023@gmail.com") || user.getEmail().equals("jeevanjyotisahoo1@gmail.com") || user.getEmail().equals("sudeshkumarsahoo98@gmail.com") || user.getEmail().equals("paltasingh10@gmail.com")) {
                                                Log.d(TAG, "Developer Mode inside If");
                                                user.setAvatar(mContext.getString(R.string.avatar_dev));
                                            }

                                            DocumentReference documentReference = FirebaseFirestore.getInstance()
                                                    .collection(mContext.getString(R.string.collection_users))
                                                    .document(FirebaseAuth.getInstance().getUid());

                                            documentReference.set(user);


                                        }
                                    }
                                });
                            }else if (count <100 && count >= 50){
                                DocumentReference docRef =FirebaseFirestore.getInstance()
                                        .collection(mContext.getString(R.string.collection_users))
                                        .document(FirebaseAuth.getInstance().getUid());

                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()) {
                                            DocumentSnapshot doc = task.getResult();
                                            assert doc != null;
                                            User user = doc.toObject(User.class);
                                            assert user != null;
                                            user.setAvatar(mContext.getString(R.string.avatar_4));
                                            if (user.getEmail().equals("bluebishal@gmail.com") || user.getEmail().equals("subratkumarpatasani0023@gmail.com") || user.getEmail().equals("jeevanjyotisahoo1@gmail.com") || user.getEmail().equals("sudeshkumarsahoo98@gmail.com") || user.getEmail().equals("paltasingh10@gmail.com")) {
                                                Log.d(TAG, "Developer Mode inside If");
                                                user.setAvatar(mContext.getString(R.string.avatar_dev));
                                            }

                                            DocumentReference documentReference = FirebaseFirestore.getInstance()
                                                    .collection(mContext.getString(R.string.collection_users))
                                                    .document(FirebaseAuth.getInstance().getUid());

                                            documentReference.set(user);


                                        }
                                    }
                                });
                            }else if (count >= 100){
                                DocumentReference docRef =FirebaseFirestore.getInstance()
                                        .collection(mContext.getString(R.string.collection_users))
                                        .document(FirebaseAuth.getInstance().getUid());

                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()) {
                                            DocumentSnapshot doc = task.getResult();
                                            assert doc != null;
                                            User user = doc.toObject(User.class);
                                            assert user != null;
                                            user.setAvatar(mContext.getString(R.string.avatar_5));
                                            if (user.getEmail().equals("bluebishal@gmail.com") || user.getEmail().equals("subratkumarpatasani0023@gmail.com") || user.getEmail().equals("jeevanjyotisahoo1@gmail.com") || user.getEmail().equals("sudeshkumarsahoo98@gmail.com") || user.getEmail().equals("paltasingh10@gmail.com")) {
                                                Log.d(TAG, "Developer Mode inside If");
                                                user.setAvatar(mContext.getString(R.string.avatar_dev));
                                            }

                                            DocumentReference documentReference = FirebaseFirestore.getInstance()
                                                    .collection(mContext.getString(R.string.collection_users))
                                                    .document(FirebaseAuth.getInstance().getUid());

                                            documentReference.set(user);


                                        }
                                    }
                                });
                            }


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection(mContext.getString(R.string.collection_users))
                .document(FirebaseAuth.getInstance().getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Developer Mode Detected");
                    DocumentSnapshot doc = task.getResult();
                    assert doc != null;
                    User user = doc.toObject(User.class);
                    assert user != null;
                    Log.d(TAG, "Developer Mode User -> " + user.getEmail());
                    if (user.getEmail().equals("bluebishal@gmail.com") || user.getEmail().equals("subratkumarpatasani0023@gmail.com") || user.getEmail().equals("jeevanjyotisahoo1@gmail.com") || user.getEmail().equals("sudeshkumarsahoo98@gmail.com") || user.getEmail().equals("paltasingh10@gmail.com")) {
                        Log.d(TAG, "Developer Mode inside If");
                        user.setAvatar(mContext.getString(R.string.avatar_dev));
                    }


                    DocumentReference documentReference = FirebaseFirestore.getInstance()
                            .collection(mContext.getString(R.string.collection_users))
                            .document(FirebaseAuth.getInstance().getUid());

                    documentReference.set(user);
                    Log.d(TAG, "Developer Mode Set");


                }
            }
        });


        return true;
    }

    @Override
    protected void onPostExecute(final Boolean result) {
        if (this.isCancelled()) {
            return;
        }
    }
}