package com.strongties.safarnama.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.strongties.safarnama.LandmarkActivity;
import com.strongties.safarnama.R;
import com.strongties.safarnama.user_classes.RV_Distance;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdaptor_distance_places extends RecyclerView.Adapter<RecyclerViewAdaptor_distance_places.ViewHolder> {

    public static final String TAG = "RecyclerViewAdaptor";

    private List<RV_Distance> mData;
    private Context mcontext;

    public RecyclerViewAdaptor_distance_places(Context context, List<RV_Distance> mData) {
        this.mcontext = context;
        this.mData = mData;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called.");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_distance_1, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        Glide.with(mcontext)
                .asBitmap()
                .placeholder(R.drawable.loading_image)
                .load(mData.get(position).getImg_url())
                .into(holder.image);
        holder.name.setText(mData.get(position).getName());
        holder.type.setText(mData.get(position).getCategory());

        holder.layout_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(new AlphaAnimation(1F, 0.7F));

                Intent intent = new Intent(mcontext, LandmarkActivity.class);
                Bundle args = new Bundle();
                args.putString("id", mData.get(position).getId());
                args.putString("state", mData.get(position).getState());
                args.putString("city", mData.get(position).getCity());
                args.putDouble("distance", mData.get(position).getDistance());
                intent.putExtras(args);
                mcontext.startActivity(intent);
                ((Activity) mcontext).overridePendingTransition(R.anim.enter_from_top, R.anim.exit_to_bottom);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView image;
        TextView name;
        TextView type;
        LinearLayout layout_item;

        public ViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.distance_menu_image);
            name = itemView.findViewById(R.id.distance_menu_name);
            type = itemView.findViewById(R.id.distance_menu_type);
            layout_item = itemView.findViewById(R.id.distance_item);
        }


    }


}
