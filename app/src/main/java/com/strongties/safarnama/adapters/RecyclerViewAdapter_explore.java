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
import com.strongties.safarnama.user_classes.LandmarkMeta;
import com.strongties.safarnama.user_classes.UserFeed;

import java.util.Objects;

public class RecyclerViewAdapter_explore extends FirestoreRecyclerAdapter<LandmarkMeta, RecyclerViewAdapter_explore.MyViewHolder> {

    public static final String TAG = "ExploreFragment";

    Context mContext;
    Dialog myDialog;

    public RecyclerViewAdapter_explore(@NonNull FirestoreRecyclerOptions<LandmarkMeta> options) {
        super(options);
      //  this.mContext = mContext;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_explore,parent,false);
        final MyViewHolder vHolder = new MyViewHolder(v);

        mContext = parent.getContext();

        return vHolder;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull LandmarkMeta model) {

        Log.d(TAG, "PlaceName ->" + model.getLandmark().getName());
        Log.d(TAG, "PlaceName ->" + model.getLandmark().getCity());

        holder.tv_placename.setText(model.getLandmark().getName());
        holder.tv_location.setText(model.getCity());
        holder.tv_district.setText(model.getState());
        // holder.img.setImageResource(mData.get(position).getPhoto());

        Glide.with(mContext)
                .load(model.getLandmark().getImg_url())
                .placeholder(R.drawable.loading_image)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(holder.img);

        /*
        *
        * Todo -> Change the list type dynamically
        *

        if (mData.get(position).getVisit().equals("tovisit")){
            holder.img_heart.setImageResource(R.drawable.bucketlist);
        }else if(mData.get(position).getVisit().equals("visited")){
            holder.img_heart.setImageResource(R.drawable.accomplished);
        }else{
            holder.img_heart.setImageResource(R.drawable.add_icon);
        }

         */

        switch (model.getLandmark().getCategory()) {
            case "Dams & Water Reservoirs":
                holder.img_heart.setImageResource(R.drawable.category_dams);
                break;
            case "Education & History":
                holder.img_heart.setImageResource(R.drawable.category_education_and_history);
                break;
            case "Garden & Parks":
                holder.img_heart.setImageResource(R.drawable.category_garden_and_parks);
                break;
            case "Hills & Caves":
                holder.img_heart.setImageResource(R.drawable.category_hills_and_caves);
                break;
            case "Historical Monuments":
                holder.img_heart.setImageResource(R.drawable.category_historical_monuments);
                break;
            case "Nature & Wildlife":
                holder.img_heart.setImageResource(R.drawable.category_nature_and_wildlife);
                break;
            case "Port & Sea Beach":
                holder.img_heart.setImageResource(R.drawable.category_port_and_sea_beach);
                break;
            case "Religious Sites":
                holder.img_heart.setImageResource(R.drawable.category_religious);
                break;
            case "Waterfalls":
                holder.img_heart.setImageResource(R.drawable.category_waterfalls);
                break;
            case "Zoos & Reserves":
                holder.img_heart.setImageResource(R.drawable.category_zoo);
                break;
            default:
                holder.img_heart.setImageResource(R.drawable.add_icon);
                break;
        }


        //Dialog Initiation
        myDialog = new Dialog(mContext);
        myDialog.setContentView(R.layout.rv_dialog_explore);
        Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;



        holder.item_explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                v1.startAnimation(new AlphaAnimation(1F, 0.7F));
                TextView dialog_placename_tv = myDialog.findViewById(R.id.dialog_explore_place_name);
                TextView dialog_description_tv = myDialog.findViewById(R.id.dialog_explore_description);
                TextView dialog_type_tv = myDialog.findViewById(R.id.dialog_explore_type);
                ImageView dialog_img = myDialog.findViewById(R.id.dialog_explore_img);

                Button btn_wish = myDialog.findViewById(R.id.dialog_explore_addtowishlist);
                Button btn_cmpl = myDialog.findViewById(R.id.dialog_explore_addtocompletelist);
                Button btn_details = myDialog.findViewById(R.id.dialog_explore_btn_details);

                dialog_placename_tv.setText(model.getLandmark().getName());
                dialog_description_tv.setText(model.getLandmark().getLong_desc());
                dialog_type_tv.setText(model.getLandmark().getCategory());


                Glide.with(mContext)
                        .load(model.getLandmark().getImg_url())
                        .placeholder(R.drawable.loading_image)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                        .into(dialog_img);

                //dialog_img.setImageResource(mData.get(vHolder.getAdapterPosition()).getPhoto());


                btn_wish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(new AlphaAnimation(1F, 0.7F));
                        DocumentReference bucketRef = FirebaseFirestore.getInstance()
                                    .collection(mContext.getString(R.string.collection_users))
                                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                .collection(mContext.getString(R.string.collection_bucket_list))
                                .document(model.getId());

                        LandmarkList landmarkList = new LandmarkList();
                        landmarkList.setLandmarkMeta(model);

                        bucketRef.set(landmarkList).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast toast = Toast.makeText(mContext, mContext.getString(R.string.wishlistadd), Toast.LENGTH_SHORT);
                                    toast.getView().setBackground(ContextCompat.getDrawable(mContext, R.drawable.dialog_bg_toast_colored));
                                    TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                                    toastmsg.setTextColor(Color.WHITE);
                                    toast.show();
                                    myDialog.cancel();
                                }
                            }
                        });

                        bucketRef = FirebaseFirestore.getInstance()
                                .collection(mContext.getString(R.string.collection_users))
                                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                .collection(mContext.getString(R.string.collection_accomplished_list))
                                .document(model.getId());

                        bucketRef.delete();

                    }

                });

                btn_cmpl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(new AlphaAnimation(1F, 0.7F));

                        DocumentReference bucketRef = FirebaseFirestore.getInstance()
                                .collection(mContext.getString(R.string.collection_users))
                                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                .collection(mContext.getString(R.string.collection_accomplished_list))
                                .document(model.getId());

                        LandmarkList landmarkList = new LandmarkList();
                        landmarkList.setLandmarkMeta(model);

                        bucketRef.set(landmarkList).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast toast = Toast.makeText(mContext, mContext.getString(R.string.accomplishlistadd), Toast.LENGTH_SHORT);
                                    toast.getView().setBackground(ContextCompat.getDrawable(mContext, R.drawable.dialog_bg_toast_colored));
                                    TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                                    toastmsg.setTextColor(Color.WHITE);
                                    toast.show();
                                    myDialog.cancel();
                                }
                            }
                        });

                        bucketRef = FirebaseFirestore.getInstance()
                                .collection(mContext.getString(R.string.collection_users))
                                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                .collection(mContext.getString(R.string.collection_bucket_list))
                                .document(model.getId());

                        bucketRef.delete();


                        UserFeed userFeed = new UserFeed();
                        userFeed.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                        String msgBuilder = mContext.getString(R.string.feed_msg_accomplished) + model.getLandmark().getName();
                        msgBuilder += "\n \n" + model.getLandmark().getLong_desc();

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

                        /*
                        DatabaseHelper dbhelper = new DatabaseHelper(mContext);
                        SQLiteDatabase database = dbhelper.getWritableDatabase();

                        ContentValues values = new ContentValues();
                        values.put("visit", "visited");
                        values.put("date", currentDate);

                        Cursor cursor = database.rawQuery("SELECT visit, description FROM LANDMARKS WHERE id = ?", new String[]{Integer.toString(mData.get(vHolder.getAdapterPosition()).getId())});

                        if (cursor != null) {
                            cursor.moveToFirst();
                        }
                        if (!cursor.getString(0).equals("visited")) {
                            database.update("LANDMARKS", values, " id = ?", new String[]{Integer.toString(mData.get(vHolder.getAdapterPosition()).getId())});
                            vHolder.img_heart.setImageResource(R.drawable.accomplished_add);









                            Toast.makeText(mContext, mContext.getResources().getString(R.string.accomplishlistadd), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.accomplishlistreadd), Toast.LENGTH_SHORT).show();
                        }

                         */

                    }
                });

                btn_details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(new AlphaAnimation(1F, 0.7F));
                        Intent intent = new Intent(mContext, LandmarkActivity.class);
                        Bundle args = new Bundle();
                        args.putString("state", model.getState());
                        args.putString("city", model.getCity());
                        args.putString("id", model.getId());
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
        private  ImageView img_heart;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            item_explore = itemView.findViewById(R.id.explore_item_id);
            tv_placename = itemView.findViewById(R.id.placename_explore);
            tv_location = itemView.findViewById(R.id.location_explore);
            tv_district = itemView.findViewById(R.id.state_explore);
            img = itemView.findViewById(R.id.img_explore);
            img_heart = itemView.findViewById(R.id.explore_heart);
        }
    }


}
