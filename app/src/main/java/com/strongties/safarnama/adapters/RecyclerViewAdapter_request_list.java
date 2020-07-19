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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.strongties.safarnama.R;
import com.strongties.safarnama.UserClient;
import com.strongties.safarnama.user_classes.RV_friendrequest;
import com.strongties.safarnama.user_classes.User;
import com.strongties.safarnama.user_classes.UserRelation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

public class RecyclerViewAdapter_request_list extends FirestoreRecyclerAdapter<UserRelation, RecyclerViewAdapter_request_list.MyViewHolder> {


    Context mContext;
    List<RV_friendrequest> mData;
    Dialog myDialog;
    private FirebaseFirestore mDb;
    private ListenerRegistration mUserListenerRegistration;
    private static final String TAG = "Menu 3, ReqAdapter";
    User currentuser;


    public RecyclerViewAdapter_request_list(@NonNull FirestoreRecyclerOptions<UserRelation> options) {
        super(options);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_menu3_request,parent,false);
        final MyViewHolder vHolder = new MyViewHolder(v);

        mContext = parent.getContext();
        //Dialog Initiation
       // myDialog = new Dialog(mContext);
        //myDialog.setContentView(R.layout.rv_dialog_accomplished);
        //myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        mDb = FirebaseFirestore.getInstance();
        currentuser = ((UserClient)(mContext.getApplicationContext())).getUser();


        return vHolder;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull UserRelation model) {

        String user_id = model.getUser_id();
        Log.d(TAG, "Model userRel-> " + model.getRelation());
        Log.d(TAG, "Model userid-> " + model.getUser_id());
        DocumentReference documentReference = FirebaseFirestore.getInstance()
                .collection(mContext.getString(R.string.collection_users))
                .document(user_id);

        final User[] user = new User[1];
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    user[0] = document.toObject(User.class);
                    if (document.exists()) {
                        Log.d(TAG, "User Info Retrieved, Name: " + user[0].getUsername());


                        holder.tv_name.setText(user[0].getUsername());
                        holder.tv_email.setText(user[0].getEmail());

                        String pattern = "dd-MM-YYYY";
                        DateFormat df = new SimpleDateFormat(pattern);
                        String dateasstring = df.format(model.getTimestamp());
                        holder.tv_added_on.setText(dateasstring);
                        // holder.img.setImageResource(mData.get(position).getPhoto());

                        Glide.with(mContext)
                                .load(user[0].getPhoto())
                                .placeholder(R.drawable.loading_image)
                                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                .into(holder.img);


                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });




        holder.item_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {

            }
        });


        holder.img_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add to friend list for current User
                // Toast.makeText(mContext, mData.get(vHolder.getAdapterPosition()).getUserRelation().getUser().getUser_id(), Toast.LENGTH_SHORT).show();

                DocumentReference usercollectionRef = mDb
                        .collection(mContext.getString(R.string.collection_relations))
                        .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                        .collection(mContext.getString(R.string.collection_friendlist))
                        .document(model.getUser_id());
                UserRelation userRelation1 = new UserRelation();
                userRelation1.setUser_id(model.getUser_id());
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
                        .document(model.getUser_id())
                        .collection(mContext.getString(R.string.collection_friendlist))
                        .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                UserRelation userRelation2 = new UserRelation();
                userRelation2.setRelation(mContext.getString(R.string.friend));
                userRelation2.setUser_id(currentuser.getUser_id());
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
                        .document(model.getUser_id())
                        .collection(mContext.getString(R.string.collection_requestedlist))
                        .document(currentuser.getUser_id());

                removerequest.delete();

                removerequest = mDb.collection(mContext.getString(R.string.collection_relations))
                        .document(currentuser.getUser_id())
                        .collection(mContext.getString(R.string.collection_requestlist))
                        .document(model.getUser_id());

                removerequest.delete();



            }
        });

        holder.img_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference removerequest = mDb.collection(mContext.getString(R.string.collection_relations))
                        .document(model.getUser_id())
                        .collection(mContext.getString(R.string.collection_requestedlist))
                        .document(currentuser.getUser_id());

                removerequest.delete();

                removerequest = mDb.collection(mContext.getString(R.string.collection_relations))
                        .document(currentuser.getUser_id())
                        .collection(mContext.getString(R.string.collection_requestlist))
                        .document(model.getUser_id());

                removerequest.delete();
                Toast.makeText(mContext, mContext.getString(R.string.friend_rejected), Toast.LENGTH_SHORT).show();

            }
        });






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
