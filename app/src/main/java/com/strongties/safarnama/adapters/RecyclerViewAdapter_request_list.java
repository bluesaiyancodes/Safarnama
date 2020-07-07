package com.strongties.safarnama.adapters;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.strongties.safarnama.R;
import com.strongties.safarnama.UserClient;
import com.strongties.safarnama.user_classes.RV_friend;
import com.strongties.safarnama.user_classes.RV_friendrequest;
import com.strongties.safarnama.user_classes.User;
import com.strongties.safarnama.user_classes.UserRelation;

import java.util.List;
import java.util.Objects;

public class RecyclerViewAdapter_request_list extends RecyclerView.Adapter<RecyclerViewAdapter_request_list.MyViewHolder> {


    Context mContext;
    List<RV_friendrequest> mData;
    Dialog myDialog;
    private FirebaseFirestore mDb;
    private ListenerRegistration mUserListenerRegistration;
    private static final String TAG = "Menu 3, ReqAdapter";
    User currentuser;

    public RecyclerViewAdapter_request_list(Context mContext, List<RV_friendrequest> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.rv_menu3_request,parent,false);
        final MyViewHolder vHolder = new MyViewHolder(v);


        //Dialog Initiation
       // myDialog = new Dialog(mContext);
        //myDialog.setContentView(R.layout.rv_dialog_accomplished);
        //myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));




        vHolder.item_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {

            }
        });


        mDb = FirebaseFirestore.getInstance();
        currentuser = ((UserClient)(mContext.getApplicationContext())).getUser();



        vHolder.img_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add to friend list for current User
               // Toast.makeText(mContext, mData.get(vHolder.getAdapterPosition()).getUserRelation().getUser().getUser_id(), Toast.LENGTH_SHORT).show();
                DocumentReference usercollectionRef = mDb
                        .collection(mContext.getString(R.string.collection_relations))
                        .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                        .collection(mContext.getString(R.string.collection_friendlist))
                        .document(mData.get(vHolder.getAdapterPosition()).getUserRelation().getUser().getUser_id());
                UserRelation userRelation1 = mData.get(vHolder.getAdapterPosition()).getUserRelation();
                userRelation1.setRelation(mContext.getString(R.string.friend));

               //Toast.makeText(mContext, mData.get(vHolder.getAdapterPosition()).getUserRelation().getUser().getUser_id(), Toast.LENGTH_SHORT).show();

                usercollectionRef.set(userRelation1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            //   Toast.makeText(mcontext, getString(R.string.friend_request_success), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Successfull added to friend list for user");
                        }else{
                            //  Toast.makeText(mcontext, getString(R.string.friend_request_fail), Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

                //Add to friend list for requested user
                usercollectionRef = mDb.collection(mContext.getString(R.string.collection_relations))
                        .document(mData.get(vHolder.getAdapterPosition()).getUserRelation().getUser().getUser_id())
                        .collection(mContext.getString(R.string.collection_friendlist))
                        .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                UserRelation userRelation2 = new UserRelation();
                userRelation2.setRelation(mContext.getString(R.string.friend));
                userRelation2.setUser(currentuser);
                usercollectionRef.set(userRelation2).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(mContext, mContext.getString(R.string.friend_accepted), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Successfull added to friend list for requested user");
                        }else{
                            Toast.makeText(mContext, mContext.getString(R.string.friend_failed), Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


                DocumentReference removerequest = mDb.collection(mContext.getString(R.string.collection_relations))
                        .document(mData.get(vHolder.getAdapterPosition()).getUserRelation().getUser().getUser_id())
                        .collection(mContext.getString(R.string.collection_requestedlist))
                        .document(currentuser.getUser_id());

                removerequest.delete();

                removerequest = mDb.collection(mContext.getString(R.string.collection_relations))
                        .document(currentuser.getUser_id())
                        .collection(mContext.getString(R.string.collection_requestlist))
                        .document(mData.get(vHolder.getAdapterPosition()).getUserRelation().getUser().getUser_id());

                removerequest.delete();



            }
        });

        vHolder.img_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference removerequest = mDb.collection(mContext.getString(R.string.collection_relations))
                        .document(mData.get(vHolder.getAdapterPosition()).getUserRelation().getUser().getUser_id())
                        .collection(mContext.getString(R.string.collection_requestedlist))
                        .document(currentuser.getUser_id());

                removerequest.delete();

                removerequest = mDb.collection(mContext.getString(R.string.collection_relations))
                        .document(currentuser.getUser_id())
                        .collection(mContext.getString(R.string.collection_requestlist))
                        .document(mData.get(vHolder.getAdapterPosition()).getUserRelation().getUser().getUser_id());

                removerequest.delete();
                Toast.makeText(mContext, mContext.getString(R.string.friend_rejected), Toast.LENGTH_SHORT).show();

            }
        });



        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

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


        private LinearLayout item_request;
        private TextView tv_name;
        private TextView tv_email;
        private TextView tv_added_on;
        private ImageView img;
        private ImageView img_accept;
        private ImageView img_reject;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            item_request = itemView.findViewById(R.id.menu3_request_item_id);
            tv_name = itemView.findViewById(R.id.menu3_request_name);
            tv_email = itemView.findViewById(R.id.menu3_request_email);
            tv_added_on = itemView.findViewById(R.id.menu3_request_added_on);
            img = itemView.findViewById(R.id.menu3_request_img);
            img_accept = itemView.findViewById(R.id.menu_3_img_accept);
            img_reject = itemView.findViewById(R.id.menu3_img_reject);
        }
    }


}
