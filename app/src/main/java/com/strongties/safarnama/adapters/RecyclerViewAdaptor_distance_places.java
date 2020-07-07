package com.strongties.safarnama.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.strongties.safarnama.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdaptor_distance_places extends RecyclerView.Adapter<RecyclerViewAdaptor_distance_places.ViewHolder>{

    public static final String TAG = "RecyclerViewAdaptor";

    //Variables
    private ArrayList<String> mNames = new ArrayList<>();
    private  ArrayList<String> mImageURLs = new ArrayList<>();
    private  ArrayList<String> mtypes = new ArrayList<>();
    private Context mcontext;



    public RecyclerViewAdaptor_distance_places(Context context, ArrayList<String> names, ArrayList<String> imageURLs, ArrayList<String> types){
        mNames = names;
        mImageURLs = imageURLs;
        mtypes = types;
        mcontext = context;
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

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image;
        TextView name;
        TextView type;

        public ViewHolder(View itemView){
            super(itemView);

            image = itemView.findViewById(R.id.distance_menu_image);
            name = itemView.findViewById(R.id.distance_menu_name);
            type = itemView.findViewById(R.id.distance_menu_type);
        }



    }






}
