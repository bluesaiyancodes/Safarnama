package com.strongties.safarnama.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.strongties.safarnama.R;
import com.strongties.safarnama.user_classes.RV_Inspiration;
import com.strongties.safarnama.user_classes.UserShort;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.util.Objects;

public class RecyclerViewAdapter_inspiration extends FirestoreRecyclerAdapter<RV_Inspiration, RecyclerViewAdapter_inspiration.MyViewHolder> {


    private static final String TAG = "Adapter JourneyTour";
    Context mContext;
    Lifecycle lifecycle;
    private FirebaseFirestore mDb;

    public RecyclerViewAdapter_inspiration(@NonNull FirestoreRecyclerOptions<RV_Inspiration> options, Lifecycle lifecycle) {
        super(options);
        this.lifecycle = lifecycle;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        final View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_inspiration, parent, false);
        final MyViewHolder vHolder = new MyViewHolder(v);

        mContext = parent.getContext();


        return vHolder;
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull RV_Inspiration model) {

        if (model.getContent_type().equals(mContext.getString(R.string.video))) {
            holder.main_img.setVisibility(View.GONE);
            holder.video_player.setVisibility(View.VISIBLE);

            //set video player
            lifecycle.addObserver(holder.video_player);
            holder.video_player.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {

                    String[] list = model.getContent_url().split("/");
                    String videoId = list[3];
                    youTubePlayer.cueVideo(videoId, 0f); // Loads video but does not play
                    // youTubePlayer.loadVideo(videoId, 0f); // Loads and plays video
                }
            });

        } else {
            holder.video_player.setVisibility(View.GONE);
            holder.main_img.setVisibility(View.VISIBLE);

            Glide.with(mContext)
                    .load(model.getContent_url())
                    .placeholder(R.drawable.loading_image)
                    .centerCrop()
                    .into(holder.main_img);
        }

        //set desc
        if (model.getContent_description().equals("NA")) {
            holder.tv_description.setVisibility(View.GONE);
        } else {
            holder.tv_description.setText(model.getContent_description());
        }

        //set location
        holder.tv_location.setText(model.getContent_location());

        Log.d(TAG, "Inspiration adapter -> " + model.toString());
        //set like count
        holder.tv_like_count.setText(Integer.toString(model.getLike_count()));

        //set the like btn if user has liked
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection(mContext.getString(R.string.collection_inspirations))
                .document(model.getContent_id())
                .collection(mContext.getString(R.string.collection_users))
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                holder.btn_like.setChecked(task.getResult().exists());
            }
        });


        holder.btn_like.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                //Log.d(TAG, "Like -> OnEvent - "+ buttonState);

                holder.btn_like.setEnabled(false);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        holder.btn_like.setEnabled(true);
                    }
                }, 5000);   //5 seconds


                if (buttonState) {
                    DocumentReference docRef = FirebaseFirestore.getInstance()
                            .collection(mContext.getString(R.string.collection_inspirations))
                            .document(model.getContent_id());

                    docRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                RV_Inspiration inspiration = task.getResult().toObject(RV_Inspiration.class);
                                inspiration.setLike_count(inspiration.getLike_count() + 1);

                                DocumentReference docRef1 = FirebaseFirestore.getInstance()
                                        .collection(mContext.getString(R.string.collection_inspirations))
                                        .document(model.getContent_id());
                                docRef1.set(inspiration);

                                DocumentReference docRef2 = FirebaseFirestore.getInstance()
                                        .collection(mContext.getString(R.string.collection_inspirations))
                                        .document(model.getContent_id())
                                        .collection(mContext.getString(R.string.collection_users))
                                        .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

                                UserShort user = new UserShort();
                                user.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                user.setUser_id(FirebaseAuth.getInstance().getUid());
                                docRef2.set(user);
                            }
                        }
                    });

                } else {
                    DocumentReference docRef = FirebaseFirestore.getInstance()
                            .collection(mContext.getString(R.string.collection_inspirations))
                            .document(model.getContent_id());

                    docRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                RV_Inspiration inspiration = task.getResult().toObject(RV_Inspiration.class);
                                inspiration.setLike_count(inspiration.getLike_count() - 1);

                                DocumentReference docRef1 = FirebaseFirestore.getInstance()
                                        .collection(mContext.getString(R.string.collection_inspirations))
                                        .document(model.getContent_id());
                                docRef1.set(inspiration);

                                DocumentReference docRef2 = FirebaseFirestore.getInstance()
                                        .collection(mContext.getString(R.string.collection_inspirations))
                                        .document(model.getContent_id())
                                        .collection(mContext.getString(R.string.collection_users))
                                        .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

                                docRef2.delete();
                            }
                        }
                    });

                }


            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {
                //  Log.d(TAG, "Like -> OnEnd - "+ buttonState);
            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {
                //  Log.d(TAG, "Like -> OnStart - "+ buttonState);


            }
        });

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {


        private final ImageView main_img;
        private final YouTubePlayerView video_player;
        private final TextView tv_description;
        private final TextView tv_location;
        private final TextView tv_like_count;
        private final SparkButton btn_like;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            main_img = itemView.findViewById(R.id.inspiration_image);
            video_player = itemView.findViewById(R.id.inspiration_youtube_player);
            tv_location = itemView.findViewById(R.id.inspiration_location);
            tv_description = itemView.findViewById(R.id.inspiration_description);
            tv_like_count = itemView.findViewById(R.id.inspiration_like_count);

            btn_like = itemView.findViewById(R.id.inspiration_like_btn);

        }
    }


}
