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
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.strongties.safarnama.LandmarkActivity;
import com.strongties.safarnama.R;
import com.strongties.safarnama.user_classes.LandmarkMeta;
import com.strongties.safarnama.user_classes.RV_DistanceSearch;

import java.util.List;
import java.util.Objects;

public class RecyclerViewAdaptor_distance_search extends RecyclerView.Adapter<RecyclerViewAdaptor_distance_search.MyViewHolder> {

    Context mContext;
    List<RV_DistanceSearch> mData;
    Dialog myDialog;

    public RecyclerViewAdaptor_distance_search(Context mContext, List<RV_DistanceSearch> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.rv_item_distancesearch, parent, false);
        final MyViewHolder vHolder = new MyViewHolder(v);


        //Dialog Initiation
        myDialog = new Dialog(mContext);
        myDialog.setContentView(R.layout.rv_dialog_bucketlist);
        Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tv_placename.setText(mData.get(position).getPlacename());
        holder.tv_location.setText(mData.get(position).getLocation());
        holder.tv_district.setText(mData.get(position).getDistrict());
        holder.tv_distance.setText(mData.get(position).getDistanceText());

        Glide.with(mContext)
                .load(mData.get(holder.getAdapterPosition()).getPhotoUrl())
                .placeholder(R.drawable.loading_image)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(holder.img);

        holder.item_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(new AlphaAnimation(1F, 0.7F));

                DocumentReference documentReference = FirebaseFirestore.getInstance()
                        .collection(mContext.getString(R.string.collection_landmarks))
                        .document(mContext.getString(R.string.document_meta))
                        .collection(mContext.getString(R.string.collection_all))
                        .document(mData.get(position).getId());

                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            assert doc != null;
                            LandmarkMeta landmarkMeta = doc.toObject(LandmarkMeta.class);


                            Intent intent = new Intent(mContext, LandmarkActivity.class);
                            Bundle args = new Bundle();
                            args.putString("id", mData.get(position).getId());
                            assert landmarkMeta != null;
                            args.putString("state", landmarkMeta.getState());
                            args.putString("city", landmarkMeta.getCity());
                            intent.putExtras(args);
                            mContext.startActivity(intent);
                            ((Activity) mContext).overridePendingTransition(R.anim.enter_from_top, R.anim.exit_to_bottom);


                        }
                    }
                });

            }
        });


        // Category Image
        switch (mData.get(holder.getAdapterPosition()).getType()) {
            case "Dams & Water Reservoirs":
                holder.img_type.setImageResource(R.drawable.category_dams);
                break;
            case "Education & History":
                holder.img_type.setImageResource(R.drawable.category_education_and_history);
                break;
            case "Garden & Parks":
                holder.img_type.setImageResource(R.drawable.category_garden_and_parks);
                break;
            case "Hills & Caves":
                holder.img_type.setImageResource(R.drawable.category_hills_and_caves);
                break;
            case "Historical Monuments":
                holder.img_type.setImageResource(R.drawable.category_historical_monuments);
                break;
            case "Nature & Wildlife":
                holder.img_type.setImageResource(R.drawable.category_nature_and_wildlife);
                break;
            case "Port & Sea Beach":
                holder.img_type.setImageResource(R.drawable.category_port_and_sea_beach);
                break;
            case "Religious Sites":
                holder.img_type.setImageResource(R.drawable.category_religious);
                break;
            case "Waterfalls":
                holder.img_type.setImageResource(R.drawable.category_waterfalls);
                break;
            case "Zoos & Reserves":
                holder.img_type.setImageResource(R.drawable.category_zoo);
                break;
            default:
                holder.img_type.setImageResource(R.drawable.add_icon);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {


        private LinearLayout item_card;
        private TextView tv_placename;
        private TextView tv_location;
        private TextView tv_district;
        private TextView tv_distance;
        private ImageView img;
        private ImageView img_type;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            item_card = itemView.findViewById(R.id.distancesearch_item);
            tv_placename = itemView.findViewById(R.id.placename_distancesearch);
            tv_location = itemView.findViewById(R.id.location_distancesearch);
            tv_district = itemView.findViewById(R.id.district_distancesearch);
            tv_distance = itemView.findViewById(R.id.distance_distancesearch);
            img = itemView.findViewById(R.id.img_distancesearch);
            img_type = itemView.findViewById(R.id.distacesearch_type);
        }
    }
}
