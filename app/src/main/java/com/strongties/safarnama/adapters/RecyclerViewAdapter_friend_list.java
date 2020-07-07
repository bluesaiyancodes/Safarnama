package com.strongties.safarnama.adapters;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.strongties.safarnama.DatabaseHelper;
import com.strongties.safarnama.R;
import com.strongties.safarnama.UserClient;
import com.strongties.safarnama.user_classes.RV_Accomplished;
import com.strongties.safarnama.user_classes.RV_friend;
import com.strongties.safarnama.user_classes.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class RecyclerViewAdapter_friend_list extends RecyclerView.Adapter<RecyclerViewAdapter_friend_list.MyViewHolder> {


    Context mContext;
    List<RV_friend> mData;
    Dialog myDialog;
    User currentuser;

    private static final String TAG = "Menu 3, FriendList";

    private FirebaseFirestore mDb;

    public RecyclerViewAdapter_friend_list(Context mContext, List<RV_friend> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        final View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.rv_menu3_friend,parent,false);
        final MyViewHolder vHolder = new MyViewHolder(v);


        //Firebase Initiation
        mDb = FirebaseFirestore.getInstance();

        currentuser = ((UserClient)(mContext.getApplicationContext())).getUser();



        vHolder.item_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {

                myDialog = new Dialog(mContext);
                myDialog.setContentView(R.layout.menu3_dialogue_buddy);
                Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView tv_name = myDialog.findViewById(R.id.menu3_buddy_name);
                TextView tv_email = myDialog.findViewById(R.id.menu3_buddy_email);
                TextView tv_date_added = myDialog.findViewById(R.id.menu3_buddy_date_added);
                ImageView iv_img = myDialog.findViewById(R.id.menu3_buddy_img);
                ImageView iv_avatar = myDialog.findViewById(R.id.menu3_buddy_avatar);
                Button buddy_remove = myDialog.findViewById(R.id.menu3_buddy_btn);

                tv_name.setText(mData.get(vHolder.getAdapterPosition()).getName());
                tv_email.setText(mData.get(vHolder.getAdapterPosition()).getEmail());
                tv_date_added.setText(mData.get(vHolder.getAdapterPosition()).getAdded_on());

                Glide.with(mContext)
                        .load(mData.get(vHolder.getAdapterPosition()).getPhotoUrl())
                        .placeholder(R.drawable.loading_image)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                        .into(iv_img);

                buddy_remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // remove from current user friend list

                        DocumentReference docRef1 = mDb
                                .collection(mContext.getString(R.string.collection_relations))
                                .document(currentuser.getUser_id())
                                .collection(mContext.getString(R.string.collection_friendlist))
                                .document(mData.get(vHolder.getAdapterPosition()).getUserRelation().getUser().getUser_id());
                        //Toast.makeText(mContext, mData.get(vHolder.getAdapterPosition()).getUserRelation().getUser().getUser_id(), Toast.LENGTH_SHORT).show();
                        docRef1.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    //   Toast.makeText(mcontext, getString(R.string.friend_request_success), Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Successfull removed from friend list for user");
                                }else{
                                    //  Toast.makeText(mcontext, getString(R.string.friend_request_fail), Toast.LENGTH_SHORT).show();
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });

                        // remove from buddy's friend list

                        DocumentReference docRef2 = mDb
                                .collection(mContext.getString(R.string.collection_relations))
                                .document(mData.get(vHolder.getAdapterPosition()).getUserRelation().getUser().getUser_id())
                                .collection(mContext.getString(R.string.collection_friendlist))
                                .document(currentuser.getUser_id());
                        docRef2.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    //   Toast.makeText(mcontext, getString(R.string.friend_request_success), Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Successfull removed from friend list for friend");
                                    Toast.makeText(mContext, mContext.getString(R.string.friend_unfriend_msg), Toast.LENGTH_SHORT).show();
                                }else{
                                    //  Toast.makeText(mcontext, getString(R.string.friend_request_fail), Toast.LENGTH_SHORT).show();
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                    Toast.makeText(mContext, mContext.getString(R.string.friend_reunfriend_msg), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    }
                });

                myDialog.show();

            }
        });



        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tv_name.setText(mData.get(position).getName());
        holder.tv_email.setText(mData.get(position).getEmail());
        holder.tv_added_on.setText(mData.get(position).getAdded_on());
       // holder.img.setImageResource(mData.get(position).getPhoto());

        Glide.with(mContext)
                .load(mData.get(holder.getAdapterPosition()).getPhotoUrl())
                .placeholder(R.drawable.loading_image)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(holder.img);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{


        private LinearLayout item_friend;
        private TextView tv_name;
        private TextView tv_email;
        private TextView tv_added_on;
        private ImageView img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            item_friend = itemView.findViewById(R.id.menu3_friend_item_id);
            tv_name = itemView.findViewById(R.id.menu3_friend_name);
            tv_email = itemView.findViewById(R.id.menu3_friend_email);
            tv_added_on = itemView.findViewById(R.id.menu3_friend_added_on);
            img = itemView.findViewById(R.id.menu3_friend_img);
        }
    }


}
