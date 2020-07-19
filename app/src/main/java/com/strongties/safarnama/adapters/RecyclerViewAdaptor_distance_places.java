package com.strongties.safarnama.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.strongties.safarnama.LandmarkActivity;
import com.strongties.safarnama.R;
import com.strongties.safarnama.user_classes.LandmarkMeta;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdaptor_distance_places extends RecyclerView.Adapter<RecyclerViewAdaptor_distance_places.ViewHolder> {

    public static final String TAG = "RecyclerViewAdaptor";

    //Variables
    private ArrayList<String> mids = new ArrayList<>();
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageURLs = new ArrayList<>();
    private ArrayList<String> mtypes = new ArrayList<>();
    private Context mcontext;


    public RecyclerViewAdaptor_distance_places(Context context, ArrayList<String> names, ArrayList<String> imageURLs, ArrayList<String> types, ArrayList<String> ids) {
        mNames = names;
        mImageURLs = imageURLs;
        mtypes = types;
        mcontext = context;
        mids = ids;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called.");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_distance_1, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        Glide.with(mcontext)
                .asBitmap()
                .placeholder(R.drawable.loading_image)
                .load(mImageURLs.get(position))
                .into(holder.image);
        holder.name.setText(mNames.get(position));
        holder.type.setText(mtypes.get(position));

        holder.layout_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DocumentReference documentReference = FirebaseFirestore.getInstance()
                        .collection(mcontext.getString(R.string.collection_landmarks))
                        .document(mcontext.getString(R.string.document_meta))
                        .collection(mcontext.getString(R.string.collection_all))
                        .document(mids.get(position));

                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            assert doc != null;
                            LandmarkMeta landmarkMeta = doc.toObject(LandmarkMeta.class);


                            Intent intent = new Intent(mcontext, LandmarkActivity.class);
                            Bundle args = new Bundle();
                            args.putString("id", mids.get(position));
                            assert landmarkMeta != null;
                            args.putString("state", landmarkMeta.getState());
                            args.putString("city", landmarkMeta.getCity());
                            intent.putExtras(args);
                            mcontext.startActivity(intent);
                            ((Activity) mcontext).overridePendingTransition(R.anim.enter_from_top, R.anim.exit_to_bottom);




                        }
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView image;
        TextView name;
        TextView type;
        LinearLayout layout_item;

        public ViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.distance_menu_image);
            name = itemView.findViewById(R.id.distance_menu_name);
            type = itemView.findViewById(R.id.distance_menu_type);
            layout_item = itemView.findViewById(R.id.distance_item);
        }


    }


}
