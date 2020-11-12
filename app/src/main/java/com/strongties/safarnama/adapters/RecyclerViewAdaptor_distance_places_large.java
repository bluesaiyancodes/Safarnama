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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.strongties.safarnama.LandmarkActivity;
import com.strongties.safarnama.R;
import com.strongties.safarnama.user_classes.RV_Distance;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class RecyclerViewAdaptor_distance_places_large extends RecyclerView.Adapter<RecyclerViewAdaptor_distance_places_large.ViewHolder> {

    public static final String TAG = "RecyclerViewAdaptor";

    private final List<RV_Distance> mData;
    private final Context mcontext;

    private final int max_items = 5;

    public RecyclerViewAdaptor_distance_places_large(Context context, List<RV_Distance> mData) {
        this.mcontext = context;
        this.mData = mData;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called.");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_distance_large, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        //View More
        /*
        if (position == getItemCount() - 1) {
            holder.layout_viewmore.setVisibility(View.VISIBLE);
            holder.layout_viewmore.setOnClickListener(view -> {

                Intent intent = new Intent(mcontext, distanceViewMore.class);
                Bundle args = new Bundle();
                args.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) mData);
                intent.putExtras(args);
                mcontext.startActivity(intent);
                ((Activity) mcontext).overridePendingTransition(R.anim.enter_from_top, R.anim.exit_to_bottom);

            });
        }

         */

        Glide.with(mcontext)
                .asBitmap()
                .placeholder(R.drawable.loading_image)
                .load(mData.get(position).getImg_url())
                .into(holder.image);
        holder.name.setText(mData.get(position).getName());
        holder.type.setText(mData.get(position).getCategory());
        DecimalFormat df = new DecimalFormat("0.00");
        String dist_text = "0";
        if (mData.get(position).getDistance() != null) {
            dist_text = df.format(mData.get(position).getDistance() / 1000.0);
        }

        holder.dist.setText(String.format("%s KMs Away", dist_text));

        AtomicReference<Boolean> isFlipped = new AtomicReference<>(Boolean.FALSE);

        holder.layout_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(new AlphaAnimation(1F, 0.7F));
/*
                if(isFlipped.get()){
                    ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(mcontext, R.animator.flipping);
                    anim.setTarget(holder.image);
                    anim.setDuration(400);
                    ObjectAnimator anim2 = (ObjectAnimator) AnimatorInflater.loadAnimator(mcontext, R.animator.flipping);
                    anim2.setTarget(holder.layout_item_flipped);
                    anim2.setDuration(400);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            holder.type.setVisibility(View.INVISIBLE);
                            holder.dist.setVisibility(View.INVISIBLE);
                            holder.next.setVisibility(View.GONE);

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            holder.layout_item_flipped.setVisibility(View.GONE);
                            holder.layout_text_back.setVisibility(View.VISIBLE);
                            holder.name.setVisibility(View.VISIBLE);
                        }
                    });
                    anim.start();
                    anim2.start();
                    isFlipped.set(Boolean.FALSE);

                }else{
                    ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(mcontext, R.animator.flipping);
                    anim.setTarget(holder.image);
                    anim.setDuration(400);
                    ObjectAnimator anim2 = (ObjectAnimator) AnimatorInflater.loadAnimator(mcontext, R.animator.flipping);
                    anim2.setTarget(holder.layout_item_flipped);
                    anim2.setDuration(400);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            holder.layout_item_flipped.setVisibility(View.VISIBLE);
                            holder.type.setVisibility(View.INVISIBLE);
                            holder.dist.setVisibility(View.INVISIBLE);
                            holder.next.setVisibility(View.GONE);
                            holder.layout_text_back.setVisibility(View.GONE);
                            holder.name.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            holder.type.setVisibility(View.VISIBLE);
                            holder.dist.setVisibility(View.VISIBLE);
                            holder.next.setVisibility(View.VISIBLE);
                        }
                    });
                    anim.start();
                    anim2.start();
                    isFlipped.set(Boolean.TRUE);
                }

 */

                holder.next.setOnClickListener(view -> {
                    Intent intent = new Intent(mcontext, LandmarkActivity.class);
                    Bundle args = new Bundle();
                    args.putString("id", mData.get(position).getId());
                    args.putString("state", mData.get(position).getState());
                    args.putString("district", mData.get(position).getDistrict());
                    args.putDouble("distance", mData.get(position).getDistance());
                    intent.putExtras(args);
                    mcontext.startActivity(intent);
                    ((Activity) mcontext).overridePendingTransition(R.anim.enter_from_top, R.anim.exit_to_bottom);
                });


            }
        });
    }

    @Override
    public int getItemCount() {
        if (mData.size() < max_items) {
            return mData.size();
        }
        return max_items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name;
        TextView type;
        TextView dist;
        ImageView next;
        RelativeLayout layout_item;
        RelativeLayout layout_text_back;
        RelativeLayout layout_item_flipped;


        public ViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.distance_menu_image);
            name = itemView.findViewById(R.id.distance_menu_name);
            type = itemView.findViewById(R.id.distance_menu_type);
            dist = itemView.findViewById(R.id.distance_menu_dist);
            next = itemView.findViewById(R.id.distance_menu_next);
            layout_item = itemView.findViewById(R.id.distance_item);
            layout_text_back = itemView.findViewById(R.id.distance_menu_text_back);
            layout_item_flipped = itemView.findViewById(R.id.distance_item_flipped);

        }


    }


}
