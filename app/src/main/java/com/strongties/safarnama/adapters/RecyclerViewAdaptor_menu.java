package com.strongties.safarnama.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.strongties.safarnama.R;
import com.strongties.safarnama.fragment_menu_distance;
import com.strongties.safarnama.fragment_menu_googleMap;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdaptor_menu extends RecyclerView.Adapter<RecyclerViewAdaptor_menu.ViewHolder>{

    public static final String TAG = "RecyclerViewAdaptor";

    //Variables
    private ArrayList<String> mNames = new ArrayList<>();
    private  ArrayList<String> mImageURLs = new ArrayList<>();
    private Context mcontext;



    public RecyclerViewAdaptor_menu(Context context, ArrayList<String> names, ArrayList<String> imageURLs){
        mNames = names;
        mImageURLs = imageURLs;
        mcontext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called.");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_layout_listitem, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        Glide.with(mcontext)
                .asBitmap()
                .load(mImageURLs.get(position))
                .into(holder.image);
        holder.name.setText(mNames.get(position));

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(mcontext, mNames.get(position), Toast.LENGTH_SHORT).show();
                if (position == 0){

                    ((AppCompatActivity)mcontext).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right)
                            .replace(R.id.fragment_container, new fragment_menu_googleMap(), "Menu Google map").commit();
                }
                if (position == 1){
                    //Toast.makeText(mcontext, "Menu 2", Toast.LENGTH_SHORT).show();
                    ((AppCompatActivity)mcontext).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left)
                            .replace(R.id.fragment_container, new fragment_menu_distance(), "Menu Distance").commit();

                }
                if (position == 2){

                    Toast.makeText(mcontext, "Menu 3", Toast.LENGTH_SHORT).show();
                }
                if (position == 3){

                    Toast.makeText(mcontext, "Menu 4", Toast.LENGTH_SHORT).show();
                }
                if (position == 4){

                    Toast.makeText(mcontext, "Menu 5", Toast.LENGTH_SHORT).show();
                }
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

        public ViewHolder(View itemView){
            super(itemView);

            image = itemView.findViewById(R.id.menu_image);
            name = itemView.findViewById(R.id.menu_name);
        }



    }






}
