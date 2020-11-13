package com.strongties.safarnama.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.strongties.safarnama.LandmarkActivity;
import com.strongties.safarnama.R;
import com.strongties.safarnama.user_classes.LandmarkList;
import com.strongties.safarnama.user_classes.LandmarkMeta;
import com.strongties.safarnama.user_classes.LandmarkStat;
import com.strongties.safarnama.user_classes.UserFeed;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.util.Objects;

import static com.strongties.safarnama.MainActivity.accomplished_id_list;
import static com.strongties.safarnama.MainActivity.accomplished_list;
import static com.strongties.safarnama.MainActivity.bucket_id_list;
import static com.strongties.safarnama.MainActivity.bucket_list;

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
        //Log.d(TAG, "Selected CHips -> "+selectedChips.toString());
/*

        if(selectedChips.size()!=0){
            if(!selectedChips.contains(model.getCategory())){
                holder.item_explore.setVisibility(View.GONE);
            }else{
                holder.item_explore.setVisibility(View.GONE);
            }
        }


 */


        Log.d(TAG, "PlaceName ->" + model.getLandmark().getName());
        Log.d(TAG, "PlaceName ->" + model.getLandmark().getCity());

        holder.tv_placename.setText(model.getLandmark().getName());
        holder.tv_location.setText(model.getLandmark().getDistrict());
        holder.tv_district.setText(model.getState());
        // holder.img.setImageResource(mData.get(position).getPhoto());

        Glide.with(mContext)
                .load(model.getLandmark().getImg_url())
                .placeholder(R.drawable.loading_image)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(holder.img);



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
            case "Iconic Places":
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
            case "Waterbodies":
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
        myDialog.setContentView(R.layout.rv_dialog_place);
        Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;



        holder.item_explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                v1.startAnimation(new AlphaAnimation(1F, 0.7F));


                TextView dialog_placename_tv = myDialog.findViewById(R.id.dialog_main_place_name);
                TextView dialog_description_tv = myDialog.findViewById(R.id.dialog_main_description);
                TextView dialog_type_tv = myDialog.findViewById(R.id.dialog_main_type);
                ImageView dialog_img = myDialog.findViewById(R.id.dialog_main_img);
                ImageView dialog_img_type = myDialog.findViewById(R.id.dialog_category_img);

                SparkButton btn_wish = myDialog.findViewById(R.id.dialog_landmark_bucket);
                SparkButton btn_cmpl = myDialog.findViewById(R.id.dialog_landmark_accomplish);
                Button btn_details = myDialog.findViewById(R.id.dialog_main_btn_details);

                dialog_placename_tv.setText(model.getLandmark().getName());
                dialog_description_tv.setText(model.getLandmark().getLong_desc());
                dialog_type_tv.setText(model.getLandmark().getCategory());


                Glide.with(mContext)
                        .load(model.getLandmark().getImg_url())
                        .placeholder(R.drawable.loading_image)
                        .transform(new CenterCrop(), new RoundedCorners(20))
                        .into(dialog_img);


                switch (model.getLandmark().getCategory()) {
                    case "Dams & Water Reservoirs":
                        dialog_img_type.setImageResource(R.drawable.category_dams);
                        break;
                    case "Education & History":
                        dialog_img_type.setImageResource(R.drawable.category_education_and_history);
                        break;
                    case "Garden & Parks":
                        dialog_img_type.setImageResource(R.drawable.category_garden_and_parks);
                        break;
                    case "Hills & Caves":
                        dialog_img_type.setImageResource(R.drawable.category_hills_and_caves);
                        break;
                    case "Iconic Places":
                        dialog_img_type.setImageResource(R.drawable.category_historical_monuments);
                        break;
                    case "Nature & Wildlife":
                        dialog_img_type.setImageResource(R.drawable.category_nature_and_wildlife);
                        break;
                    case "Port & Sea Beach":
                        dialog_img_type.setImageResource(R.drawable.category_port_and_sea_beach);
                        break;
                    case "Religious Sites":
                        dialog_img_type.setImageResource(R.drawable.category_religious);
                        break;
                    case "Waterbodies":
                        dialog_img_type.setImageResource(R.drawable.category_waterfalls);
                        break;
                    case "Zoos & Reserves":
                        dialog_img_type.setImageResource(R.drawable.category_zoo);
                        break;
                    default:
                        dialog_img_type.setImageResource(R.drawable.loading_image);
                        break;
                }


                if (bucket_id_list.contains(model.getId())) {
                    btn_wish.setChecked(true);
                }


                btn_wish.setEventListener(new SparkEventListener() {
                    @Override
                    public void onEvent(ImageView button, boolean buttonState) {
                        if (buttonState) {
                            bucket_id_list.add(model.getId());
                            bucket_list.add(model.getLandmark().getName());
                            btn_cmpl.setChecked(false);

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
                                    if (task.isSuccessful()) {

                                        Toast toast = Toast.makeText(mContext, mContext.getString(R.string.wishlistadd), Toast.LENGTH_SHORT);
                                        toast.getView().setBackground(ContextCompat.getDrawable(mContext, R.drawable.dialog_bg_toast_colored));
                                        TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                                        toastmsg.setTextColor(Color.WHITE);
                                        toast.show();

                                    }
                                }
                            });

                            bucketRef = FirebaseFirestore.getInstance()
                                    .collection(mContext.getString(R.string.collection_users))
                                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                    .collection(mContext.getString(R.string.collection_accomplished_list))
                                    .document(model.getId());

                            bucketRef.delete();


                            DocumentReference docRef = FirebaseFirestore.getInstance()
                                    .collection(mContext.getString(R.string.collection_stats))
                                    .document(mContext.getString(R.string.collection_landmarks))
                                    .collection(mContext.getString(R.string.collection_lists))
                                    .document(mContext.getString(R.string.document_bucketlist))
                                    .collection(mContext.getString(R.string.document_meta))
                                    .document(model.getId());

                            DocumentReference docRef1 = FirebaseFirestore.getInstance()
                                    .collection(mContext.getString(R.string.collection_stats))
                                    .document(mContext.getString(R.string.collection_landmarks))
                                    .collection(mContext.getString(R.string.collection_lists))
                                    .document(mContext.getString(R.string.document_bucketlist))
                                    .collection(model.getState())
                                    .document(model.getId());


                            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        LandmarkStat landmarkStat = documentSnapshot.toObject(LandmarkStat.class);
                                        assert landmarkStat != null;
                                        landmarkStat.setLandmarkCounter(landmarkStat.getLandmarkCounter() + 1);

                                        DocumentReference documentReference = FirebaseFirestore.getInstance()
                                                .collection(mContext.getString(R.string.collection_stats))
                                                .document(mContext.getString(R.string.collection_landmarks))
                                                .collection(mContext.getString(R.string.collection_lists))
                                                .document(mContext.getString(R.string.document_bucketlist))
                                                .collection(mContext.getString(R.string.document_meta))
                                                .document(model.getId());

                                        documentReference.set(landmarkStat).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //do nothing
                                            }
                                        });

                                        docRef1.set(landmarkStat).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //do nothing
                                            }
                                        });

                                    } else {
                                        LandmarkStat landmarkStat = new LandmarkStat();
                                        landmarkStat.setLandmark(model.getLandmark());
                                        landmarkStat.setLandmarkCounter(1);
                                        DocumentReference documentReference = FirebaseFirestore.getInstance()
                                                .collection(mContext.getString(R.string.collection_stats))
                                                .document(mContext.getString(R.string.collection_landmarks))
                                                .collection(mContext.getString(R.string.collection_lists))
                                                .document(mContext.getString(R.string.document_bucketlist))
                                                .collection(mContext.getString(R.string.document_meta))
                                                .document(model.getId());

                                        documentReference.set(landmarkStat).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //do nothing
                                            }
                                        });
                                        docRef1.set(landmarkStat).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //do nothing
                                            }
                                        });
                                    }
                                }
                            });


                        } else {

                            new AlertDialog.Builder(mContext)
                                    .setTitle(mContext.getString(R.string.confirm))
                                    .setMessage(mContext.getString(R.string.wishlistremove_confirmation_msg))
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            bucket_id_list.remove(model.getId());
                                            bucket_list.remove(model.getLandmark().getName());

                                            DocumentReference bucketRef = FirebaseFirestore.getInstance()
                                                    .collection(mContext.getString(R.string.collection_users))
                                                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                                    .collection(mContext.getString(R.string.collection_bucket_list))
                                                    .document(model.getId());

                                            bucketRef.delete();
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            btn_wish.setChecked(true);
                                        }
                                    })
                                    .setIcon(R.drawable.app_main_icon)
                                    .show();

                        }
                    }

                    @Override
                    public void onEventAnimationEnd(ImageView button, boolean buttonState) {

                    }

                    @Override
                    public void onEventAnimationStart(ImageView button, boolean buttonState) {

                    }
                });


                if (accomplished_id_list.contains(model.getId())) {
                    btn_cmpl.setChecked(true);
                }

                btn_cmpl.setEventListener(new SparkEventListener() {
                    @Override
                    public void onEvent(ImageView button, boolean buttonState) {
                        if (buttonState) {

                            accomplished_id_list.add(model.getId());
                            accomplished_list.add(model.getLandmark().getName());
                            btn_wish.setChecked(false);

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
                                    if (task.isSuccessful()) {
                                        Toast toast = Toast.makeText(mContext, mContext.getString(R.string.accomplishlistadd), Toast.LENGTH_SHORT);
                                        toast.getView().setBackground(ContextCompat.getDrawable(mContext, R.drawable.dialog_bg_toast_colored));
                                        TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                                        toastmsg.setTextColor(Color.WHITE);
                                        toast.show();

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
                            msgBuilder += "\n \n" + model.getLandmark().getShort_desc();

                            userFeed.setDatacontent(msgBuilder);

                            userFeed.setType(mContext.getString(R.string.public_feed));
                            userFeed.setImgIncluded(Boolean.TRUE);
                            userFeed.setImgUrl(model.getLandmark().getImg_url());
                            userFeed.setLandmarkIncluded(Boolean.TRUE);
                            userFeed.setLandmarkId(model.getId());

                            Long tsLong = System.currentTimeMillis() / 1000;
                            String ts = tsLong.toString();

                            DocumentReference docRef = FirebaseFirestore.getInstance()
                                    .collection(mContext.getString(R.string.collection_feed))
                                    .document(mContext.getString(R.string.document_global))
                                    .collection(mContext.getString(R.string.collection_posts))
                                    .document(ts);


                            docRef.set(userFeed).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Successfully added to Global feed list ");
                                    } else {
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
                                    if (task.isSuccessful()) {
                                        //  Toast.makeText(mContext, mContext.getString(R.string.feed_post_success), Toast.LENGTH_SHORT).show();

                                        Log.d(TAG, "Successfully added to User feed list ");
                                    } else {
                                        Log.w(TAG, "Error getting documents.", task.getException());
                                    }
                                }
                            });


                            docRef = FirebaseFirestore.getInstance()
                                    .collection(mContext.getString(R.string.collection_stats))
                                    .document(mContext.getString(R.string.collection_landmarks))
                                    .collection(mContext.getString(R.string.collection_lists))
                                    .document(mContext.getString(R.string.document_accomplished))
                                    .collection(mContext.getString(R.string.document_meta))
                                    .document(model.getId());

                            DocumentReference docRef1 = FirebaseFirestore.getInstance()
                                    .collection(mContext.getString(R.string.collection_stats))
                                    .document(mContext.getString(R.string.collection_landmarks))
                                    .collection(mContext.getString(R.string.collection_lists))
                                    .document(mContext.getString(R.string.document_accomplished))
                                    .collection(model.getState())
                                    .document(model.getId());


                            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        LandmarkStat landmarkStat = documentSnapshot.toObject(LandmarkStat.class);
                                        assert landmarkStat != null;
                                        landmarkStat.setLandmarkCounter(landmarkStat.getLandmarkCounter() + 1);

                                        DocumentReference documentReference = FirebaseFirestore.getInstance()
                                                .collection(mContext.getString(R.string.collection_stats))
                                                .document(mContext.getString(R.string.collection_landmarks))
                                                .collection(mContext.getString(R.string.collection_lists))
                                                .document(mContext.getString(R.string.document_accomplished))
                                                .collection(mContext.getString(R.string.document_meta))
                                                .document(model.getId());

                                        documentReference.set(landmarkStat).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //do nothing
                                            }
                                        });

                                        docRef1.set(landmarkStat).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //do nothing
                                            }
                                        });

                                    } else {
                                        LandmarkStat landmarkStat = new LandmarkStat();
                                        landmarkStat.setLandmark(model.getLandmark());
                                        landmarkStat.setLandmarkCounter(1);
                                        DocumentReference documentReference = FirebaseFirestore.getInstance()
                                                .collection(mContext.getString(R.string.collection_stats))
                                                .document(mContext.getString(R.string.collection_landmarks))
                                                .collection(mContext.getString(R.string.collection_lists))
                                                .document(mContext.getString(R.string.document_accomplished))
                                                .collection(mContext.getString(R.string.document_meta))
                                                .document(model.getId());

                                        documentReference.set(landmarkStat).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //do nothing
                                            }
                                        });
                                        docRef1.set(landmarkStat).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //do nothing
                                            }
                                        });
                                    }
                                }
                            });

                        } else {

                            new AlertDialog.Builder(mContext)
                                    .setTitle(mContext.getString(R.string.confirm))
                                    .setMessage(mContext.getString(R.string.accomplishlistremove_confirmation_msg))
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            accomplished_id_list.remove(model.getId());
                                            accomplished_list.remove(model.getLandmark().getName());

                                            DocumentReference bucketRef = FirebaseFirestore.getInstance()
                                                    .collection(mContext.getString(R.string.collection_users))
                                                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                                    .collection(mContext.getString(R.string.collection_accomplished_list))
                                                    .document(model.getId());

                                            bucketRef.delete();
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            btn_cmpl.setChecked(true);
                                        }
                                    })
                                    .setIcon(R.drawable.app_main_icon)
                                    .show();


                        }
                    }

                    @Override
                    public void onEventAnimationEnd(ImageView button, boolean buttonState) {

                    }

                    @Override
                    public void onEventAnimationStart(ImageView button, boolean buttonState) {

                    }
                });


                btn_details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(new AlphaAnimation(1F, 0.7F));
                        Intent intent = new Intent(mContext, LandmarkActivity.class);
                        Bundle args = new Bundle();
                        args.putString("state", model.getState());
                        args.putString("district", model.getdistrict());
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

    public static class MyViewHolder extends RecyclerView.ViewHolder {


        private final LinearLayout item_explore;
        private final TextView tv_placename;
        private final TextView tv_location;
        private final TextView tv_district;
        private final ImageView img;
        private final ImageView img_heart;

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
