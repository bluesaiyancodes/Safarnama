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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.strongties.safarnama.LandmarkActivity;
import com.strongties.safarnama.R;
import com.strongties.safarnama.user_classes.LandmarkList;
import com.strongties.safarnama.user_classes.RV_Bucketlist;
import com.strongties.safarnama.user_classes.UserFeed;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

public class RecyclerViewAdapter_bucketlist extends FirestoreRecyclerAdapter<LandmarkList, RecyclerViewAdapter_bucketlist.MyViewHolder> {

    public static final String TAG = "BLFragment";

    Context mContext;
    List<RV_Bucketlist> mData;
    Dialog myDialog;


    public RecyclerViewAdapter_bucketlist(@NonNull FirestoreRecyclerOptions<LandmarkList> options) {
        super(options);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_bucketlist,parent,false);
        final MyViewHolder vHolder = new MyViewHolder(v);

        mContext = parent.getContext();

        return vHolder;

    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull LandmarkList model) {

        holder.tv_placename.setText(model.getLandmarkMeta().getLandmark().getName());
        holder.tv_location.setText(model.getLandmarkMeta().getLandmark().getCity());
        holder.tv_district.setText(model.getLandmarkMeta().getLandmark().getState());
        // holder.img.setImageResource(mData.get(position).getPhoto());

        Glide.with(mContext)
                .load(model.getLandmarkMeta().getLandmark().getImg_url())
                .placeholder(R.drawable.loading_image)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(holder.img);


        switch (model.getLandmarkMeta().getLandmark().getCategory()) {
            case "Dams & Water Reservoirs":
                holder.category.setImageResource(R.drawable.category_dams);
                break;
            case "Education & History":
                holder.category.setImageResource(R.drawable.category_education_and_history);
                break;
            case "Garden & Parks":
                holder.category.setImageResource(R.drawable.category_garden_and_parks);
                break;
            case "Hills & Caves":
                holder.category.setImageResource(R.drawable.category_hills_and_caves);
                break;
            case "Historical Monuments":
                holder.category.setImageResource(R.drawable.category_historical_monuments);
                break;
            case "Nature & Wildlife":
                holder.category.setImageResource(R.drawable.category_nature_and_wildlife);
                break;
            case "Port & Sea Beach":
                holder.category.setImageResource(R.drawable.category_port_and_sea_beach);
                break;
            case "Religious Sites":
                holder.category.setImageResource(R.drawable.category_religious);
                break;
            case "Waterfalls":
                holder.category.setImageResource(R.drawable.category_waterfalls);
                break;
            case "Zoos & Reserves":
                holder.category.setImageResource(R.drawable.category_zoo);
                break;
            default:
                holder.category.setImageResource(R.drawable.add_icon);
                break;
        }


        //Dialog Initiation
        myDialog = new Dialog(mContext);
        myDialog.setContentView(R.layout.rv_dialog_bucketlist);
        Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;




        holder.item_explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                TextView dialog_placename_tv = myDialog.findViewById(R.id.dialog_bucketlist_place_name);
                TextView dialog_description_tv = myDialog.findViewById(R.id.dialog_bucketlist_description);
                TextView dialog_type_tv = myDialog.findViewById(R.id.dialog_bucketlist_type);
                TextView dialog_date_tv = myDialog.findViewById(R.id.dialog_bucketlist_date);
                ImageView dialog_img = myDialog.findViewById(R.id.dialog_bucketlist_img);

                Button btn_wish = myDialog.findViewById(R.id.dialog_bucketlist_removewishlist);
                Button btn_cmpl = myDialog.findViewById(R.id.dialog_bucketlist_addtocompletelist);
                Button btn_details = myDialog.findViewById(R.id.dialog_bucketlist_btn_details);

                dialog_placename_tv.setText(model.getLandmarkMeta().getLandmark().getName());
                dialog_description_tv.setText(model.getLandmarkMeta().getLandmark().getLong_desc());
                dialog_type_tv.setText(model.getLandmarkMeta().getLandmark().getCategory());

                String pattern = "dd-MM-YYYY";
                DateFormat df = new SimpleDateFormat(pattern);
                String dateasstring;
                try {
                    dateasstring = df.format(model.getTimestamp());
                }catch (NullPointerException e){
                    dateasstring = "";
                }

                dialog_date_tv.setText(dateasstring);
                Glide.with(mContext)
                        .load(model.getLandmarkMeta().getLandmark().getImg_url())
                        .placeholder(R.drawable.loading_image)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                        .into(dialog_img);

                //dialog_img.setImageResource(mData.get(vHolder.getAdapterPosition()).getPhoto());


                btn_wish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DocumentReference bucketRef = FirebaseFirestore.getInstance()
                                .collection(mContext.getString(R.string.collection_users))
                                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                .collection(mContext.getString(R.string.collection_bucket_list))
                                .document(model.getLandmarkMeta().getId());

                        bucketRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    myDialog.cancel();
                                    Toast toast = Toast.makeText(mContext, mContext.getString(R.string.wishlistremove), Toast.LENGTH_SHORT);
                                    toast.getView().setBackground(ContextCompat.getDrawable(mContext, R.drawable.dialog_bg_toast_colored));
                                    TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                                    toastmsg.setTextColor(Color.WHITE);
                                    toast.show();
                                }
                            }
                        });


                    }
                });

                btn_cmpl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        DocumentReference bucketRef = FirebaseFirestore.getInstance()
                                .collection(mContext.getString(R.string.collection_users))
                                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                .collection(mContext.getString(R.string.collection_accomplished_list))
                                .document(model.getLandmarkMeta().getId());

                        LandmarkList landmarkList = new LandmarkList();
                        landmarkList.setLandmarkMeta(model.getLandmarkMeta());

                        bucketRef.set(landmarkList).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast toast = Toast.makeText(mContext, mContext.getString(R.string.accomplishlistadd), Toast.LENGTH_SHORT);
                                    toast.getView().setBackground(ContextCompat.getDrawable(mContext, R.drawable.dialog_bg_toast_colored));
                                    TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                                    toastmsg.setTextColor(Color.WHITE);
                                    toast.show();
                                }
                            }
                        });

                         bucketRef = FirebaseFirestore.getInstance()
                                .collection(mContext.getString(R.string.collection_users))
                                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                .collection(mContext.getString(R.string.collection_bucket_list))
                                .document(model.getLandmarkMeta().getId());

                        bucketRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    myDialog.cancel();
                                }
                            }
                        });




                        UserFeed userFeed = new UserFeed();
                        userFeed.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                        String msgBuilder = mContext.getString(R.string.feed_msg_accomplished) + model.getLandmarkMeta().getLandmark().getName();
                        msgBuilder += "\n \n" + model.getLandmarkMeta().getLandmark().getLong_desc();

                        userFeed.setDatacontent(msgBuilder);

                        Long tsLong = System.currentTimeMillis()/1000;
                        String ts = tsLong.toString();

                        DocumentReference docRef = FirebaseFirestore.getInstance()
                                .collection(mContext.getString(R.string.collection_feed))
                                .document(mContext.getString(R.string.document_global))
                                .collection(mContext.getString(R.string.collection_posts))
                                .document(ts);


                        docRef.set(userFeed).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.d(TAG, "Successfully added to Global feed list ");
                                }else{
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });

                        docRef = FirebaseFirestore.getInstance()
                                .collection(mContext.getString(R.string.collection_feed))
                                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                .collection(mContext.getString(R.string.collection_posts))
                                .document(ts);

                        docRef.set(userFeed).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    //  Toast.makeText(mContext, mContext.getString(R.string.feed_post_success), Toast.LENGTH_SHORT).show();

                                    Log.d(TAG, "Successfully added to User feed list ");
                                }else{
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });


                    }
                });


                btn_details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, LandmarkActivity.class);
                        Bundle args = new Bundle();
                        args.putString("state", model.getLandmarkMeta().getState());
                        args.putString("city", model.getLandmarkMeta().getCity());
                        args.putString("id", model.getLandmarkMeta().getId());
                        intent.putExtras(args);
                        mContext.startActivity(intent);
                        ((Activity) mContext).overridePendingTransition(R.anim.enter_from_top, R.anim.exit_to_bottom);
                    }
                });

                myDialog.show();
            }
        });









    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{


        private LinearLayout item_explore;
        private TextView tv_placename;
        private TextView tv_location;
        private TextView tv_district;
        private ImageView img;
        private ImageView category;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            item_explore = itemView.findViewById(R.id.bucketlist_item_id);
            tv_placename = itemView.findViewById(R.id.placename_bucketlist);
            tv_location = itemView.findViewById(R.id.location_bucketlist);
            tv_district = itemView.findViewById(R.id.state_bucketlist);
            img = itemView.findViewById(R.id.img_bucketlist);
            category = itemView.findViewById(R.id.bucketlist_type);
        }
    }


}
