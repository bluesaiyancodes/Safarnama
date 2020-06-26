package com.strongties.safarnama;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

public class Dialogue_Gmap extends AppCompatDialogFragment {



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogue_map_marker, null);

        Bundle args = getArguments();

        ImageView imageView = view.findViewById(R.id.dialog_marker_image);
        final ProgressBar progressBar = view.findViewById(R.id.dialog_progress);

        TextView name = view.findViewById(R.id.dialog_marker_name);
        TextView type = view.findViewById(R.id.dialog_marker_type);
        TextView lat = view.findViewById(R.id.dialog_marker_lat);
        TextView lon = view.findViewById(R.id.dialog_marker_lon);
        TextView desc = view.findViewById(R.id.dialog_marker_desc);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);

        Glide.with(this).load(args.getString("url"))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);

        if(args.getString("name") != null){
            name.setText(args.getString("name"));
        }else{
            name.setText("Invalid Name");
        }

        if(args.getString("type") != null){
            type.setText(args.getString("type"));
        }else{
            type.setText("Invalid type");
        }

        if(args.getString("lat") != null){
            lat.setText(args.getString("lat"));
        }else{
            lat.setText("Invalid Lat");
        }

        if(args.getString("lon") != null){
            lon.setText(args.getString("lon"));
        }else{
            lon.setText("Invalid Lon");
        }

        if(args.getString("desc") != null){
            desc.setText(args.getString("desc"));
        }else{
            desc.setText("Invalid Description");
        }



        builder.setView(view)
                .setTitle("Information")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }
}
