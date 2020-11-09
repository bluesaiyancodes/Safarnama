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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.strongties.safarnama.JourneyActivity;
import com.strongties.safarnama.R;
import com.strongties.safarnama.user_classes.RV_Journey_Tour;

import java.util.ArrayList;

public class RecyclerViewAdapter_Journey_tour extends FirestoreRecyclerAdapter<RV_Journey_Tour, RecyclerViewAdapter_Journey_tour.MyViewHolder> {


    private static final String TAG = "Adapter JourneyTour";
    Context mContext;
    Dialog myDialog;
    ArrayList<Integer> img_urls;
    private FirebaseFirestore mDb;

    public RecyclerViewAdapter_Journey_tour(@NonNull FirestoreRecyclerOptions<RV_Journey_Tour> options) {
        super(options);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        final View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_journey_tour, parent, false);
        final MyViewHolder vHolder = new MyViewHolder(v);

        mContext = parent.getContext();

        img_urls = new ArrayList<>();
        img_urls.add(R.drawable.background_1);
        img_urls.add(R.drawable.background_2);
        img_urls.add(R.drawable.background_3);
        img_urls.add(R.drawable.background_4);


        return vHolder;
    }


    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull RV_Journey_Tour model) {


        holder.journey_ll.setBackgroundResource(img_urls.get(position % 4));

        Glide.with(mContext)
                .load(model.getImg_url())
                .placeholder(R.drawable.loading_image)
                .centerCrop()
                .into(holder.main_img);

        //Log.d(TAG, "img_url -> " + model.getImg_url());

        holder.tv_header.setText(model.getName());

        holder.tv_description.setText(model.getDescription().replace("\"", ""));

        holder.tv_duration.setText(model.getDuration());

        holder.btn_view_more.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, JourneyActivity.class);
            Bundle args = new Bundle();
            args.putString("id", model.getId());
            args.putInt("number", position % 4);
            intent.putExtras(args);
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_top);

        });


    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {


        private final ImageView main_img;
        private final TextView tv_header;
        private final TextView tv_duration;
        private final TextView tv_description;
        private final AppCompatButton btn_view_more;

        private final LinearLayout journey_ll;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            main_img = itemView.findViewById(R.id.journey_rv_card_image);
            tv_header = itemView.findViewById(R.id.journey_rv_title);
            tv_duration = itemView.findViewById(R.id.journey_rv_card_duration);
            tv_description = itemView.findViewById(R.id.journey_rv_card_desciption);

            journey_ll = itemView.findViewById(R.id.journey_rv_ll);

            btn_view_more = itemView.findViewById(R.id.journey_rv_card_btn);

        }
    }


}
