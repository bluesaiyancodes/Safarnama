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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.strongties.safarnama.LandmarkActivity;
import com.strongties.safarnama.R;
import com.strongties.safarnama.user_classes.LandmarkMeta;
import com.strongties.safarnama.user_classes.RV_Journey_Location;

import org.imaginativeworld.whynotimagecarousel.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.ImageCarousel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecyclerViewAdapter_Journey_location extends FirestoreRecyclerAdapter<RV_Journey_Location, RecyclerViewAdapter_Journey_location.MyViewHolder> {


    private static final String TAG = "Adapter JourneyLoc";
    Context mContext;
    Dialog myDialog;
    private FirebaseFirestore mDb;

    public RecyclerViewAdapter_Journey_location(@NonNull FirestoreRecyclerOptions<RV_Journey_Location> options) {
        super(options);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        final View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_journey_location, parent, false);
        final MyViewHolder vHolder = new MyViewHolder(v);

        mContext = parent.getContext();


        return vHolder;
    }


    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull RV_Journey_Location model) {


        if (model.getImage().equals("NA")) {
            DocumentReference docRef = FirebaseFirestore.getInstance()
                    .collection(mContext.getString(R.string.collection_landmarks))
                    .document(mContext.getString(R.string.document_meta))
                    .collection(mContext.getString(R.string.collection_all))
                    .document(model.getId());
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        LandmarkMeta landmarkMeta = task.getResult().toObject(LandmarkMeta.class);
                        Glide.with(mContext)
                                .load(landmarkMeta.getLandmark().getImg_url())
                                .placeholder(R.drawable.loading_image)
                                .into(holder.iv_main);

                        holder.iv_main.setOnClickListener(view -> {
                            Intent intent = new Intent(mContext, LandmarkActivity.class);
                            Bundle args = new Bundle();
                            args.putString("id", landmarkMeta.getId());
                            args.putString("state", landmarkMeta.getState());
                            args.putString("district", landmarkMeta.getdistrict());
                            intent.putExtras(args);
                            mContext.startActivity(intent);
                            ((Activity) mContext).overridePendingTransition(R.anim.enter_from_top, R.anim.exit_to_bottom);

                        });
                    }
                }
            });
        } else {
            Glide.with(mContext)
                    .load(model.getImage())
                    .placeholder(R.drawable.loading_image)
                    .into(holder.iv_main);
        }

        // Arrow Flow Management
        {
            if (model.getPlace_pos_type().equals("Beginning")) {
                Glide.with(mContext)
                        .load(R.drawable.journey_asset_round_line)
                        .placeholder(R.drawable.loading_image)
                        .into(holder.iv_head);

                Glide.with(mContext)
                        .load(R.drawable.journey_asset_line_arrow)
                        .placeholder(R.drawable.loading_image)
                        .into(holder.iv_tail);

            } else if (model.getPlace_pos_type().equals("Intermediate")) {
                Glide.with(mContext)
                        .load(R.drawable.journey_asset_line)
                        .placeholder(R.drawable.loading_image)
                        .into(holder.iv_head);

                Glide.with(mContext)
                        .load(R.drawable.journey_asset_line_arrow)
                        .placeholder(R.drawable.loading_image)
                        .into(holder.iv_tail);
            } else if (model.getPlace_pos_type().equals("Ending")) {
                Glide.with(mContext)
                        .load(R.drawable.journey_asset_line)
                        .placeholder(R.drawable.loading_image)
                        .into(holder.iv_head);

                Glide.with(mContext)
                        .load(R.drawable.journey_asset_line_round)
                        .placeholder(R.drawable.loading_image)
                        .into(holder.iv_tail);
            } else {
                Log.d(TAG, "Name ->" + model.getName() + " ; type -> " + model.getPlace_pos_type());
                Glide.with(mContext)
                        .load(R.drawable.journey_asset_line)
                        .placeholder(R.drawable.loading_image)
                        .into(holder.iv_head);

                Glide.with(mContext)
                        .load(R.drawable.journey_asset_line)
                        .placeholder(R.drawable.loading_image)
                        .into(holder.iv_tail);
            }

        }

        holder.tv_name.setText(model.getName());
        holder.tv_name.setSelected(Boolean.TRUE);   //for marquee
        holder.tv_visiting.setText(model.getVisiting_hours());
        holder.tv_visiting.setSelected(Boolean.TRUE);   //for marquee
        holder.tv_fee.setText(model.getEntry_fee());
        holder.tv_fee.setSelected(Boolean.TRUE);    //for marquee


        //Dialog Initiation
        myDialog = new Dialog(mContext);
        myDialog.setContentView(R.layout.rv_dialog_journey);
        Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


        holder.btn_view_more.setOnClickListener(view -> {
            view.startAnimation(new AlphaAnimation(1F, 0.7F));

            TextView tv_placename = myDialog.findViewById(R.id.rv_dialog_journey_name);
            TextView tv_dist_origin = myDialog.findViewById(R.id.rv_dialog_journey_dist_origin);
            TextView tv_dist_previous = myDialog.findViewById(R.id.rv_dialog_journey_dist_previous);
            TextView tv_visiting_hours = myDialog.findViewById(R.id.rv_dialog_journey_dist_visit_hrs);
            TextView tv_entry_fee = myDialog.findViewById(R.id.rv_dialog_journey_dist_entry_fee);
            TextView tv_duration = myDialog.findViewById(R.id.rv_dialog_journey_dist_duration);
            TextView tv_famous_for = myDialog.findViewById(R.id.rv_dialog_journey_famous);
            ImageCarousel carousel = myDialog.findViewById(R.id.rv_dialog_journey_carousel);

            tv_placename.setText(model.getName());
            tv_dist_origin.setText(model.getDistance_origin());
            tv_dist_previous.setText(model.getDistance_previous());
            tv_visiting_hours.setText(model.getVisiting_hours());
            tv_entry_fee.setText(model.getEntry_fee());
            tv_duration.setText(model.getDuration());
            tv_famous_for.setText(model.getFamous_for());

            carousel.setAutoPlay(false);
            List<CarouselItem> carousellist = new ArrayList<>();
            if (model.getImage().equals("NA")) {
                DocumentReference docRef = FirebaseFirestore.getInstance()
                        .collection(mContext.getString(R.string.collection_landmarks))
                        .document(mContext.getString(R.string.document_meta))
                        .collection(mContext.getString(R.string.collection_all))
                        .document(model.getId());
                docRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            LandmarkMeta landmarkMeta = task.getResult().toObject(LandmarkMeta.class);
                            String[] imgslist = landmarkMeta.getLandmark().getImg_all_url().split(" ");
                            Log.d(TAG, "imgslist -> " + imgslist[0]);
                            for (String img : imgslist) {
                                carousellist.add(new CarouselItem(img));
                            }
                            carousel.addData(carousellist);
                        }
                    }
                });
            } else {
                carousellist.add(new CarouselItem(model.getImage()));
                carousel.addData(carousellist);
            }


            myDialog.show();

        });


    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {


        private final ImageView iv_head;
        private final ImageView iv_main;
        private final ImageView iv_tail;
        private final TextView tv_name;
        private final TextView tv_visiting;
        private final TextView tv_fee;
        private final AppCompatTextView btn_view_more;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_head = itemView.findViewById(R.id.act_journey_rv_card_layout_img1);
            iv_main = itemView.findViewById(R.id.act_journey_rv_card_layout_img2);
            iv_tail = itemView.findViewById(R.id.act_journey_rv_card_layout_img3);
            tv_name = itemView.findViewById(R.id.act_journey_rv_card_place);
            tv_visiting = itemView.findViewById(R.id.act_journey_rv_card_visiting);
            tv_fee = itemView.findViewById(R.id.act_journey_rv_card_fee);

            btn_view_more = itemView.findViewById(R.id.act_journey_rv_card_btn);

        }
    }


}
