package com.strongties.safarnama.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
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
import com.strongties.safarnama.LandmarkActivity;
import com.strongties.safarnama.R;
import com.strongties.safarnama.imageViewActivity;
import com.strongties.safarnama.user_classes.LandmarkMeta;
import com.strongties.safarnama.user_classes.User;
import com.strongties.safarnama.user_classes.UserFeed;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class RecyclerViewAdapter_feed extends FirestoreRecyclerAdapter<UserFeed, RecyclerViewAdapter_feed.MyViewHolder> {

    public static final String TAG = "FeedFragAdap";

    Context mContext;
    Dialog myDialog;
    String mode;


    public RecyclerViewAdapter_feed(@NonNull FirestoreRecyclerOptions<UserFeed> options, String mode) {
        super(options);
        this.mode = mode;
    }

    public RecyclerViewAdapter_feed(@NonNull FirestoreRecyclerOptions<UserFeed> options) {
        super(options);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_feed, parent, false);
        final MyViewHolder vHolder = new MyViewHolder(v);

        mContext = parent.getContext();


        return vHolder;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull UserFeed model) {

        if (position == 0) {
            holder.vspace.setVisibility(View.VISIBLE);
        } else {
            holder.vspace.setVisibility(View.GONE);
        }

        holder.tv_feed_body.setText(model.getDatacontent());

        String pattern = "dd-MM-YYYY";
        DateFormat df = new SimpleDateFormat(pattern);
        String dateasstring;
        try {
            dateasstring = df.format(model.getTimestamp());
        } catch (NullPointerException e) {
            dateasstring = "";
        }
        holder.tv_date.setText(dateasstring);


        if (model.getImgIncluded()) {
            holder.feed_img.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(model.getImgUrl())
                    .placeholder(R.drawable.loading_image)
                    .centerCrop()
                    .into(holder.feed_img);
            if (model.getLandmarkIncluded()) {
                holder.feed_img.setOnClickListener(v -> {
                    //Dialog Initiation
                    Dialog myDialog = new Dialog(mContext);
                    myDialog.setContentView(R.layout.dialog_main_search);
                    Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


                    TextView dialog_placename_tv = myDialog.findViewById(R.id.dialog_main_place_name);
                    TextView dialog_description_tv = myDialog.findViewById(R.id.dialog_main_description);
                    TextView dialog_type_tv = myDialog.findViewById(R.id.dialog_main_type);
                    ImageView dialog_img = myDialog.findViewById(R.id.dialog_main_img);
                    ImageView dialog_type_iv = myDialog.findViewById(R.id.dialog_category_img);
                    AppCompatButton btn_details = myDialog.findViewById(R.id.dialog_main_btn_details);


                    DocumentReference docRef = FirebaseFirestore.getInstance()
                            .collection(mContext.getString(R.string.collection_landmarks))
                            .document(mContext.getString(R.string.document_meta))
                            .collection(mContext.getString(R.string.collection_all))
                            .document(model.getLandmarkId());

                    docRef.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            LandmarkMeta landmarkMeta = documentSnapshot.toObject(LandmarkMeta.class);
                            dialog_placename_tv.setText(landmarkMeta.getLandmark().getName());
                            dialog_description_tv.setText(landmarkMeta.getLandmark().getLong_desc());
                            dialog_type_tv.setText(landmarkMeta.getLandmark().getCategory());


                            Glide.with(mContext)
                                    .load(landmarkMeta.getLandmark().getImg_url())
                                    .placeholder(R.drawable.loading_image)
                                    .transform(new CenterCrop(), new RoundedCorners(20))
                                    .into(dialog_img);

                            switch (landmarkMeta.getLandmark().getCategory()) {
                                case "Dams & Water Reservoirs":
                                    dialog_type_iv.setImageResource(R.drawable.category_dams);
                                    break;
                                case "Education & History":
                                    dialog_type_iv.setImageResource(R.drawable.category_education_and_history);
                                    break;
                                case "Garden & Parks":
                                    dialog_type_iv.setImageResource(R.drawable.category_garden_and_parks);
                                    break;
                                case "Hills & Caves":
                                    dialog_type_iv.setImageResource(R.drawable.category_hills_and_caves);
                                    break;
                                case "Iconic Places":
                                    dialog_type_iv.setImageResource(R.drawable.category_historical_monuments);
                                    break;
                                case "Nature & Wildlife":
                                    dialog_type_iv.setImageResource(R.drawable.category_nature_and_wildlife);
                                    break;
                                case "Port & Sea Beach":
                                    dialog_type_iv.setImageResource(R.drawable.category_port_and_sea_beach);
                                    break;
                                case "Religious Sites":
                                    dialog_type_iv.setImageResource(R.drawable.category_religious);
                                    break;
                                case "Waterbodies":
                                    dialog_type_iv.setImageResource(R.drawable.category_waterfalls);
                                    break;
                                case "Zoos & Reserves":
                                    dialog_type_iv.setImageResource(R.drawable.category_zoo);
                                    break;
                                default:
                                    dialog_type_iv.setImageResource(R.drawable.loading_image);
                                    break;
                            }


                            btn_details.setOnClickListener(view -> {
                                Intent intent = new Intent(mContext, LandmarkActivity.class);
                                Bundle args = new Bundle();
                                args.putString("state", landmarkMeta.getState());
                                args.putString("district", landmarkMeta.getdistrict());
                                args.putString("id", landmarkMeta.getId());
                                intent.putExtras(args);
                                mContext.startActivity(intent);
                                ((Activity) mContext).overridePendingTransition(R.anim.enter_from_top, R.anim.exit_to_bottom);
                            });
                        }
                    });

                    myDialog.show();


                });
            } else {
                holder.feed_img.setOnClickListener(view -> {


                    view.startAnimation(new AlphaAnimation(1F, 0.7F));
                    Intent intent = new Intent(mContext, imageViewActivity.class);
                    intent.putExtra("imageUrl", model.getImgUrl());
                    intent.putExtra("name", "View Image");
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);


                });
            }
        }


        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection(mContext.getString(R.string.collection_users))
                .document(model.getUser_id());


        final User[] user = new User[1];
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    user[0] = document.toObject(User.class);
                    if (document.exists()) {
                        Log.d(TAG, "User Info Retrieved, Name: " + user[0].getUsername());

                        Glide.with(mContext)
                                .load(user[0].getPhoto())
                                .placeholder(R.drawable.loading_image)
                                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                .into(holder.profile_img);
                        holder.tv_name.setText(user[0].getUsername());
                        switch (user[0].getAvatar()) {
                            case "0 Star":
                                holder.tv_avatar_lvl.setText(mContext.getString(R.string.avatar_0_lvl));
                                break;
                            case "1 Star":
                                holder.tv_avatar_lvl.setText(mContext.getString(R.string.avatar_1_lvl));
                                break;
                            case "2 Star":
                                holder.tv_avatar_lvl.setText(mContext.getString(R.string.avatar_2_lvl));
                                break;
                            case "3 Star":
                                holder.tv_avatar_lvl.setText(mContext.getString(R.string.avatar_3_lvl));
                                break;
                            case "4 Star":
                                holder.tv_avatar_lvl.setText(mContext.getString(R.string.avatar_4_lvl));
                                break;
                            case "5 Star":
                                holder.tv_avatar_lvl.setText(mContext.getString(R.string.avatar_5_lvl));
                                break;
                            case "Developer":
                                holder.tv_avatar_lvl.setText(mContext.getString(R.string.avatar_dev_lvl));
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


    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_feed_body;
        private final TextView tv_date;
        private final TextView tv_name;
        private final TextView tv_avatar_lvl;
        private final ImageView profile_img;
        private final ImageView feed_img;

        private final LinearLayout itemlayout;
        private final RelativeLayout vspace;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_feed_body = itemView.findViewById(R.id.menu_feed_textbody);
            tv_date = itemView.findViewById(R.id.menu_feed_date);
            tv_name = itemView.findViewById(R.id.menu_feed_name);
            tv_avatar_lvl = itemView.findViewById(R.id.menu_feed_avatar_lvl);
            profile_img = itemView.findViewById(R.id.menu_feed_profile);
            feed_img = itemView.findViewById(R.id.menu_feed_img);

            itemlayout = itemView.findViewById(R.id.feed_layout);
            vspace = itemView.findViewById(R.id.feed_vspace);
        }
    }


}
