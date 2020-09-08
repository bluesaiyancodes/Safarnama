package com.strongties.safarnama.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.strongties.safarnama.R;
import com.strongties.safarnama.UserClient;
import com.strongties.safarnama.user_classes.User;
import com.strongties.safarnama.user_classes.UserRelation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class RecyclerViewAdapter_friend_list extends FirestoreRecyclerAdapter<UserRelation, RecyclerViewAdapter_friend_list.MyViewHolder> {


    Context mContext;
    Dialog myDialog;
    User currentuser;

    private static final String TAG = "Menu 3, FriendList";

    private FirebaseFirestore mDb;

    public RecyclerViewAdapter_friend_list(@NonNull FirestoreRecyclerOptions<UserRelation> options) {
        super(options);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        final View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_menu3_friend,parent,false);
        final MyViewHolder vHolder = new MyViewHolder(v);

        mContext = parent.getContext();

        //Firebase Initiation
        mDb = FirebaseFirestore.getInstance();

        currentuser = ((UserClient)(mContext.getApplicationContext())).getUser();






        return vHolder;
    }


    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull UserRelation model) {
        String user_id = model.getUser_id();

        DocumentReference documentReference = FirebaseFirestore.getInstance()
                .collection(mContext.getString(R.string.collection_users))
                .document(user_id);

        final User[] user = new User[1];
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    user[0] = document.toObject(User.class);
                    if (document.exists()) {
                        Log.d(TAG, "User Info Retrieved, Name: " + user[0].getUsername());

                        holder.tv_name.setText(user[0].getUsername());
                        holder.tv_email.setText(user[0].getEmail());
                        String pattern = "dd-MM-YYYY";
                        DateFormat df = new SimpleDateFormat(pattern);
                        String dateasstring;
                        try {
                            dateasstring = df.format(model.getTimestamp());
                        } catch (NullPointerException e) {
                            dateasstring = "";
                        }
                        holder.tv_added_on.setText(dateasstring);
                        // holder.img.setImageResource(mData.get(position).getPhoto());

                        if (!user[0].getPhoto().equals("null")) {
                            Glide.with(mContext)
                                    .load(user[0].getPhoto())
                                    .placeholder(R.drawable.loading_image)
                                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                    .into(holder.img);

                        } else {
                            Glide.with(mContext)
                                    .load(R.drawable.profile_pic_placeholder)
                                    .placeholder(R.drawable.loading_image)
                                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                    .into(holder.img);

                        }


                        switch (user[0].getAvatar()) {
                            case "0 Star":
                                Glide.with(mContext)
                                        .load(R.drawable.avatar_0_star)
                                        .placeholder(R.drawable.loading_image)
                                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                                        .into(holder.badge);
                                break;
                            case "1 Star":
                                Glide.with(mContext)
                                        .load(R.drawable.avatar_1_star)
                                        .placeholder(R.drawable.loading_image)
                                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                                        .into(holder.badge);
                                break;
                            case "2 Star":
                                Glide.with(mContext)
                                        .load(R.drawable.avatar_2_star)
                                        .placeholder(R.drawable.loading_image)
                                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                                        .into(holder.badge);
                                break;
                            case "3 Star":
                                Glide.with(mContext)
                                        .load(R.drawable.avatar_3_star)
                                        .placeholder(R.drawable.loading_image)
                                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                                        .into(holder.badge);
                                break;
                            case "4 Star":
                                Glide.with(mContext)
                                        .load(R.drawable.avatar_4_star)
                                        .placeholder(R.drawable.loading_image)
                                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                                        .into(holder.badge);
                                break;
                            case "5 Star":
                                Glide.with(mContext)
                                        .load(R.drawable.avatar_5_star)
                                        .placeholder(R.drawable.loading_image)
                                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                                        .into(holder.badge);
                                break;
                            case "Developer":
                                Glide.with(mContext)
                                        .load(R.drawable.avatar_6_star)
                                        .placeholder(R.drawable.loading_image)
                                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                                        .into(holder.badge);
                                break;
                            default:
                                break;
                        }


                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });






        holder.item_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                v1.startAnimation(new AlphaAnimation(1F, 0.7F));

                myDialog = new Dialog(mContext);
                myDialog.setContentView(R.layout.menu3_dialogue_buddy);
                Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                TextView tv_name = myDialog.findViewById(R.id.menu3_buddy_name);
                TextView tv_email = myDialog.findViewById(R.id.menu3_buddy_email);
                TextView tv_date_added = myDialog.findViewById(R.id.menu3_buddy_date_added);
                ImageView iv_img = myDialog.findViewById(R.id.menu3_buddy_img);
                ImageView iv_avatar = myDialog.findViewById(R.id.menu3_buddy_avatar);
                Button buddy_remove = myDialog.findViewById(R.id.menu3_buddy_btn);




                DocumentReference documentReference = FirebaseFirestore.getInstance()
                        .collection(mContext.getString(R.string.collection_users))
                        .document(user_id);

                final User[] user = new User[1];
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            assert document != null;
                            user[0] = document.toObject(User.class);
                            if (document.exists()) {
                                Log.d(TAG, "User Info Retrieved, Name: " + user[0].getUsername());


                                tv_name.setText(user[0].getUsername());
                                tv_email.setText(user[0].getEmail());
                                String pattern = "dd-MM-YYYY";
                                DateFormat df = new SimpleDateFormat(pattern);
                                String dateasstring = df.format(model.getTimestamp());
                                tv_date_added.setText(dateasstring);

                                Glide.with(mContext)
                                        .load(user[0].getPhoto())
                                        .placeholder(R.drawable.loading_image)
                                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                                        .into(iv_img);

                                switch (user[0].getAvatar()){
                                    case "0 Star":
                                        Glide.with(mContext)
                                                .load(R.drawable.avatar_0_star)
                                                .placeholder(R.drawable.loading_image)
                                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                                                .into(iv_avatar);
                                        break;
                                    case "1 Star":
                                        Glide.with(mContext)
                                                .load(R.drawable.avatar_1_star)
                                                .placeholder(R.drawable.loading_image)
                                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                                                .into(iv_avatar);
                                        break;
                                    case "2 Star":
                                        Glide.with(mContext)
                                                .load(R.drawable.avatar_2_star)
                                                .placeholder(R.drawable.loading_image)
                                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                                                .into(iv_avatar);
                                        break;
                                    case "3 Star":
                                        Glide.with(mContext)
                                                .load(R.drawable.avatar_3_star)
                                                .placeholder(R.drawable.loading_image)
                                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                                                .into(iv_avatar);
                                        break;
                                    case "4 Star":
                                        Glide.with(mContext)
                                                .load(R.drawable.avatar_4_star)
                                                .placeholder(R.drawable.loading_image)
                                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                                                .into(iv_avatar);
                                        break;
                                    case "5 Star":
                                        Glide.with(mContext)
                                                .load(R.drawable.avatar_5_star)
                                                .placeholder(R.drawable.loading_image)
                                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                                                .into(iv_avatar);
                                        break;
                                    case "Developer":
                                        Glide.with(mContext)
                                                .load(R.drawable.avatar_6_star)
                                                .placeholder(R.drawable.loading_image)
                                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                                                .into(iv_avatar);
                                        break;
                                    default:
                                        Glide.with(mContext)
                                                .load(R.drawable.loading_image)
                                                .placeholder(R.drawable.loading_image)
                                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                                                .into(iv_avatar);
                                        break;
                                }

                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });






                buddy_remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // remove from current user friend list
                        v.startAnimation(new AlphaAnimation(1F, 0.7F));

                        DocumentReference documentReference = FirebaseFirestore.getInstance()
                                .collection(mContext.getString(R.string.collection_users))
                                .document(user_id);

                        final User[] user = new User[1];
                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    assert document != null;
                                    user[0] = document.toObject(User.class);
                                    if (document.exists()) {
                                        Log.d(TAG, "User Info Retrieved, Name: " + user[0].getUsername());


                                        DocumentReference docRef1 = mDb
                                                .collection(mContext.getString(R.string.collection_relations))
                                                .document(currentuser.getUser_id())
                                                .collection(mContext.getString(R.string.collection_friendlist))
                                                .document(user[0].getUser_id());
                                        //Toast.makeText(mContext, mData.get(vHolder.getAdapterPosition()).getUserRelation().getUser().getUser_id(), Toast.LENGTH_SHORT).show();
                                        docRef1.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    //   Toast.makeText(mcontext, getString(R.string.friend_request_success), Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, "Successfull removed from friend list for user");
                                                }else{
                                                    //  Toast.makeText(mcontext, getString(R.string.friend_request_fail), Toast.LENGTH_SHORT).show();
                                                    Log.w(TAG, "Error getting documents.", task.getException());
                                                }
                                            }
                                        });


                                    } else {
                                        Log.d(TAG, "No such document");
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });




                        // remove from buddy's friend list


                        documentReference = FirebaseFirestore.getInstance()
                                .collection(mContext.getString(R.string.collection_users))
                                .document(user_id);

                        final User[] user1 = new User[1];
                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    assert document != null;
                                    user1[0] = document.toObject(User.class);
                                    if (document.exists()) {
                                        Log.d(TAG, "User Info Retrieved, Name: " + user1[0].getUsername());


                                        DocumentReference docRef2 = mDb
                                                .collection(mContext.getString(R.string.collection_relations))
                                                .document(user1[0].getUser_id())
                                                .collection(mContext.getString(R.string.collection_friendlist))
                                                .document(currentuser.getUser_id());
                                        docRef2.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()) {
                                                    //   Toast.makeText(mcontext, getString(R.string.friend_request_success), Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, "Successfull removed from friend list for friend");
                                                    Toast toast = Toast.makeText(mContext, mContext.getString(R.string.friend_unfriend_msg), Toast.LENGTH_SHORT);
                                                    toast.getView().setBackground(ContextCompat.getDrawable(mContext, R.drawable.dialog_bg_toast_colored));
                                                    TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                                                    toastmsg.setTextColor(Color.WHITE);
                                                    toast.show();
                                                }else {
                                                    //  Toast.makeText(mcontext, getString(R.string.friend_request_fail), Toast.LENGTH_SHORT).show();
                                                    Log.w(TAG, "Error getting documents.", task.getException());
                                                    Toast toast = Toast.makeText(mContext, mContext.getString(R.string.friend_reunfriend_msg), Toast.LENGTH_SHORT);
                                                    toast.getView().setBackground(ContextCompat.getDrawable(mContext, R.drawable.dialog_bg_toast_colored));
                                                    TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                                                    toastmsg.setTextColor(Color.WHITE);
                                                    toast.show();
                                                }
                                            }
                                        });



                                    } else {
                                        Log.d(TAG, "No such document");
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });



                    }
                });

                myDialog.show();

            }
        });









    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{


        private LinearLayout item_friend;
        private TextView tv_name;
        private TextView tv_email;
        private TextView tv_added_on;
        private ImageView img;
        private ImageView badge;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            item_friend = itemView.findViewById(R.id.menu3_friend_item_id);
            tv_name = itemView.findViewById(R.id.menu3_friend_name);
            tv_email = itemView.findViewById(R.id.menu3_friend_email);
            tv_added_on = itemView.findViewById(R.id.menu3_friend_added_on);
            img = itemView.findViewById(R.id.menu3_friend_img);
            badge = itemView.findViewById(R.id.menu3_buddy_badge);
        }
    }


}
