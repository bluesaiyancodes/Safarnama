package com.strongties.safarnama.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.strongties.safarnama.R;
import com.strongties.safarnama.imageViewActivity;

public class RecylerViewAdapter_place_images extends RecyclerView.Adapter<RecylerViewAdapter_place_images.ViewHolder> {

    Context mContext;
    private String[] mData;
    private String name;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public RecylerViewAdapter_place_images(Context context, String[] data, String name) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.name = name;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rv_place_images, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(view);
    }


    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(mContext)
                .load(mData[position])
                .centerCrop()
                .placeholder(R.drawable.loading_image)
               // .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(holder.imview);

        holder.imview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(new AlphaAnimation(1F, 0.7F));
                Intent intent = new Intent(mContext, imageViewActivity.class);
                intent.putExtra("imageUrl", mData[position]);
                intent.putExtra("name", name);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        });
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData.length;
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return mData[id];
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imview;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.startAnimation(new AlphaAnimation(1F, 0.7F));
            imview = itemView.findViewById(R.id.dialog_place_img);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}