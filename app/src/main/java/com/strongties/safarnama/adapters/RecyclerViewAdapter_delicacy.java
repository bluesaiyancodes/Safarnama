package com.strongties.safarnama.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.strongties.safarnama.DelicacyActivity;
import com.strongties.safarnama.R;
import com.strongties.safarnama.user_classes.RV_Delicacy;

import org.imaginativeworld.whynotimagecarousel.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.ImageCarousel;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter_delicacy extends FirestoreRecyclerAdapter<RV_Delicacy, RecyclerViewAdapter_delicacy.MyViewHolder> {


    private static final String TAG = "Adapter Delicacy";
    Context mContext;
    Dialog myDialog;
    private FirebaseFirestore mDb;

    public RecyclerViewAdapter_delicacy(@NonNull FirestoreRecyclerOptions<RV_Delicacy> options) {
        super(options);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        final View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_delicacy, parent, false);
        final MyViewHolder vHolder = new MyViewHolder(v);

        mContext = parent.getContext();


        return vHolder;
    }


    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull RV_Delicacy model) {

        holder.imageCarousel.setAutoPlay(true);
        List<CarouselItem> carousellist = new ArrayList<>();
        String[] imgslist = model.getImg_carousel().split(" ");
        for (String img : imgslist) {
            carousellist.add(new CarouselItem(img));
        }
        switch (model.getCategory()) {
            default:
                break;
            case "Spicy":
                Glide.with(mContext)
                        .load(R.drawable.item_bg_green)
                        .placeholder(R.drawable.loading_image)
                        .centerCrop()
                        .into(holder.background_img);
                break;
            case "Fries":
                Glide.with(mContext)
                        .load(R.drawable.item_bg_blue)
                        .placeholder(R.drawable.loading_image)
                        .centerCrop()
                        .into(holder.background_img);
                break;
            case "Sweets":
                Glide.with(mContext)
                        .load(R.drawable.item_bg_pink)
                        .placeholder(R.drawable.loading_image)
                        .centerCrop()
                        .into(holder.background_img);
                break;
            case "Crackers":
                Glide.with(mContext)
                        .load(R.drawable.item_bg_yellow)
                        .placeholder(R.drawable.loading_image)
                        .centerCrop()
                        .into(holder.background_img);
                break;
        }


        Glide.with(mContext)
                .load(model.getImg_header())
                .placeholder(R.drawable.loading_image)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(holder.header_img);

        holder.tv_name.setText(model.getName());

        holder.tv_placename.setText(model.getFamous_in().replace("\"", ""));

        holder.tv_price.setText(model.getPrice());

        holder.tv_time.setText(model.getFoodtype().replace("\"", ""));

        holder.imageCarousel.addData(carousellist);

        holder.button_details.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, DelicacyActivity.class);
            Bundle args = new Bundle();
            args.putString("id", model.getId());
            intent.putExtras(args);
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_top);
        });


    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView background_img;

        private final ImageView header_img;
        private final TextView tv_name;
        private final TextView tv_placename;
        private final TextView tv_price;
        private final TextView tv_time;
        private final AppCompatButton button_details;
        private final ImageCarousel imageCarousel;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            header_img = itemView.findViewById(R.id.rv_delicacy_img_header);
            tv_name = itemView.findViewById(R.id.rv_delicacy_name);
            tv_placename = itemView.findViewById(R.id.rv_delicacy_place_name);
            tv_price = itemView.findViewById(R.id.rv_delicacy_place_price);
            tv_time = itemView.findViewById(R.id.rv_delicacy_time);
            button_details = itemView.findViewById(R.id.rv_delicacy_button);
            imageCarousel = itemView.findViewById(R.id.rv_delicacy_carousel);

            background_img = itemView.findViewById(R.id.rv_delicacy_background);
        }
    }


}
