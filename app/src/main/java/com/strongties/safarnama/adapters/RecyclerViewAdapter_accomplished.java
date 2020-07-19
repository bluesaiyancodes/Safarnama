package com.strongties.safarnama.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.strongties.safarnama.user_classes.RV_Accomplished;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

public class RecyclerViewAdapter_accomplished extends FirestoreRecyclerAdapter<LandmarkList, RecyclerViewAdapter_accomplished.MyViewHolder> {


    Context mContext;
    List<RV_Accomplished> mData;
    Dialog myDialog;

    public RecyclerViewAdapter_accomplished(@NonNull FirestoreRecyclerOptions<LandmarkList> options) {
        super(options);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_accomplished,parent,false);
        final MyViewHolder vHolder = new MyViewHolder(v);

        mContext = parent.getContext();

        return vHolder;

    }


    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull LandmarkList model) {

        holder.tv_placename.setText(model.getLandmarkMeta().getLandmark().getName());
        holder.tv_location.setText(model.getLandmarkMeta().getLandmark().getCity());
        holder.tv_district.setText(model.getLandmarkMeta().getLandmark().getState());

        Glide.with(mContext)
                .load(model.getLandmarkMeta().getLandmark().getImg_url())
                .placeholder(R.drawable.loading_image)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(holder.img);





         //Dialog Initiation
        myDialog = new Dialog(mContext);
        myDialog.setContentView(R.layout.rv_dialog_accomplished);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;




        holder.item_explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                TextView dialog_placename_tv = myDialog.findViewById(R.id.dialog_accomplished_place_name);
                TextView dialog_description_tv = myDialog.findViewById(R.id.dialog_accomplished_description);
                TextView dialog_type_tv = myDialog.findViewById(R.id.dialog_accomplished_type);
                TextView dialog_date_tv = myDialog.findViewById(R.id.dialog_accomplished_date);
                ImageView dialog_img = myDialog.findViewById(R.id.dialog_accomplished_img);

                Button btn_wish = myDialog.findViewById(R.id.dialog_accomplished_addtowishlist);
                Button btn_cmpl = myDialog.findViewById(R.id.dialog_accomplished_removecompletelist);
                Button btn_details = myDialog.findViewById(R.id.dialog_accomplished_btn_details);

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
                        .placeholder(R.drawable.ic_launcher_background)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                        .into(dialog_img);



                btn_wish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        DocumentReference bucketRef = FirebaseFirestore.getInstance()
                                .collection(mContext.getString(R.string.collection_users))
                                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                .collection(mContext.getString(R.string.collection_bucket_list))
                                .document(model.getLandmarkMeta().getId());

                        LandmarkList landmarkList = new LandmarkList();
                        landmarkList.setLandmarkMeta(model.getLandmarkMeta());

                        bucketRef.set(landmarkList).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(mContext, mContext.getString(R.string.wishlistadd), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                        bucketRef = FirebaseFirestore.getInstance()
                                .collection(mContext.getString(R.string.collection_users))
                                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                .collection(mContext.getString(R.string.collection_accomplished_list))
                                .document(model.getLandmarkMeta().getId());

                        bucketRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    myDialog.cancel();
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

                        bucketRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    myDialog.cancel();
                                    Toast.makeText(mContext, mContext.getString(R.string.accomplishlistremove), Toast.LENGTH_SHORT).show();
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

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            item_explore = itemView.findViewById(R.id.accomplished_item_id);
            tv_placename = itemView.findViewById(R.id.placename_accomplished);
            tv_location = itemView.findViewById(R.id.location_accomplished);
            tv_district = itemView.findViewById(R.id.state_accomplished);
            img = itemView.findViewById(R.id.img_accomplished);
        }
    }


}
