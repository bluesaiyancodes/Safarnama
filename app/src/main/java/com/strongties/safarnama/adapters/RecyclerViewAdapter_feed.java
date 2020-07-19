package com.strongties.safarnama.adapters;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.strongties.safarnama.R;
import com.strongties.safarnama.user_classes.User;
import com.strongties.safarnama.user_classes.UserFeed;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class RecyclerViewAdapter_feed extends FirestoreRecyclerAdapter<UserFeed, RecyclerViewAdapter_feed.MyViewHolder>{

    public static final String TAG = "FeedFragAdap";

    Context mContext;
    List<UserFeed> mData;
    Dialog myDialog;
    String mode;


    public RecyclerViewAdapter_feed(@NonNull FirestoreRecyclerOptions<UserFeed> options, String mode) {
        super(options);
        this.mode = mode;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_feed,parent,false);
        final MyViewHolder vHolder = new MyViewHolder(v);

        mContext = parent.getContext();


        return vHolder;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull UserFeed model) {



            holder.tv_feed_body.setText(model.getDatacontent());

            String pattern = "dd-MM-YYYY";
            DateFormat df = new SimpleDateFormat(pattern);
            String dateasstring;
            try {
                dateasstring = df.format(model.getTimestamp());
            }catch (NullPointerException e){
                dateasstring = "";
            }
            holder.tv_date.setText(dateasstring);


            DocumentReference docRef = FirebaseFirestore.getInstance()
                    .collection(mContext.getString(R.string.collection_users))
                    .document(model.getUser_id());


            final User[] user = new User[1];
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assert document != null;
                        user[0] = document.toObject(User.class);
                        if (document.exists()) {
                            Log.d(TAG, "User Info Retrieved, Name: " + user[0].getUsername());
                            Glide.with(mContext)
                                    .load(user[0].getPhoto())
                                    .placeholder(R.drawable.loading_image)
                                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                    .into(holder.profile_img);
                            holder.tv_name.setText(user[0].getUsername());
                            switch (user[0].getAvatar()){
                                case "0 Star":
                                    holder.tv_avatar_lvl.setText(mContext.getString(R.string.avatar_0_lvl));
                                    break;
                                case "1 Star":
                                    holder.tv_avatar_lvl.setText(mContext.getString(R.string.avatar_1_lvl));
                                    break;
                                case "2 Star":
                                    holder.tv_avatar_lvl.setText(mContext.getString(R.string.avatar_2_lvl));
                                    break;
                                case "3 Star":
                                    holder.tv_avatar_lvl.setText(mContext.getString(R.string.avatar_3_lvl));
                                    break;
                                case "4 Star":
                                    holder.tv_avatar_lvl.setText(mContext.getString(R.string.avatar_4_lvl));
                                    break;
                                case "5 Star":
                                    holder.tv_avatar_lvl.setText(mContext.getString(R.string.avatar_5_lvl));
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });




    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_feed_body;
        private TextView tv_date;
        private TextView tv_name;
        private TextView tv_avatar_lvl;
        private ImageView profile_img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_feed_body = itemView.findViewById(R.id.menu_feed_textbody);
            tv_date = itemView.findViewById(R.id.menu_feed_date);
            tv_name = itemView.findViewById(R.id.menu_feed_name);
            tv_avatar_lvl = itemView.findViewById(R.id.menu_feed_avatar_lvl);
            profile_img = itemView.findViewById(R.id.menu_feed_profile);
        }
    }


}
