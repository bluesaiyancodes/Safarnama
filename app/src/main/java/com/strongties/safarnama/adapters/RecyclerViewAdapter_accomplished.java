package com.strongties.safarnama.adapters;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.strongties.safarnama.DatabaseHelper;
import com.strongties.safarnama.R;
import com.strongties.safarnama.user_classes.RV_Accomplished;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RecyclerViewAdapter_accomplished extends RecyclerView.Adapter<RecyclerViewAdapter_accomplished.MyViewHolder> {


    Context mContext;
    List<RV_Accomplished> mData;
    Dialog myDialog;

    public RecyclerViewAdapter_accomplished(Context mContext, List<RV_Accomplished> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.rv_item_accomplished,parent,false);
        final MyViewHolder vHolder = new MyViewHolder(v);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        final String currentDate = sdf.format(new Date());

        //Dialog Initiation
        myDialog = new Dialog(mContext);
        myDialog.setContentView(R.layout.rv_dialog_accomplished);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));




        vHolder.item_explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                TextView dialog_placename_tv = myDialog.findViewById(R.id.dialog_accomplished_place_name);
                TextView dialog_description_tv = myDialog.findViewById(R.id.dialog_accomplished_description);
                TextView dialog_type_tv = myDialog.findViewById(R.id.dialog_accomplished_type);
                TextView dialog_date_tv = myDialog.findViewById(R.id.dialog_accomplished_date);
                ImageView dialog_img = myDialog.findViewById(R.id.dialog_accomplished_img);

                Button btn_wish = myDialog.findViewById(R.id.dialog_accomplished_addtowishlist);
                Button btn_cmpl = myDialog.findViewById(R.id.dialog_accomplished_removecompletelist);

                dialog_placename_tv.setText(mData.get(vHolder.getAdapterPosition()).getPlacename());
                dialog_description_tv.setText(mData.get(vHolder.getAdapterPosition()).getDescription());
                dialog_type_tv.setText(mData.get(vHolder.getAdapterPosition()).getType());
                dialog_date_tv.setText(mData.get(vHolder.getAdapterPosition()).getDatecreated());
                Glide.with(mContext)
                        .load(mData.get(vHolder.getAdapterPosition()).getPhotoUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                        .into(dialog_img);

                //dialog_img.setImageResource(mData.get(vHolder.getAdapterPosition()).getPhoto());


                btn_wish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseHelper dbhelper = new DatabaseHelper(mContext);
                        SQLiteDatabase database = dbhelper.getWritableDatabase();

                        ContentValues values = new ContentValues();
                        values.put("visit", "tovisit");
                        values.put("date", currentDate);

                        Cursor cursor = database.rawQuery("SELECT visit FROM LANDMARKS WHERE id = ?", new String[]{Integer.toString(mData.get(vHolder.getAdapterPosition()).getId())});

                        if (cursor != null) {
                            cursor.moveToFirst();
                        }
                        if (!cursor.getString(0).equals("tovisit")) {
                            database.update("LANDMARKS", values, " id = ?", new String[]{Integer.toString(mData.get(vHolder.getAdapterPosition()).getId())});
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.wishlistadd), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.wishlistreadd), Toast.LENGTH_SHORT).show();
                        }
                        cursor.close();


                    }
                });

                btn_cmpl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseHelper dbhelper = new DatabaseHelper(mContext);
                        SQLiteDatabase database = dbhelper.getWritableDatabase();

                        ContentValues values = new ContentValues();
                        values.put("visit", "notvisited");
                        values.put("date", currentDate);

                        Cursor cursor = database.rawQuery("SELECT visit FROM LANDMARKS WHERE id = ?", new String[]{Integer.toString(mData.get(vHolder.getAdapterPosition()).getId())});

                        if (cursor != null) {
                            cursor.moveToFirst();
                        }
                        if (!cursor.getString(0).equals("notvisited")) {
                            database.update("LANDMARKS", values, " id = ?", new String[]{Integer.toString(mData.get(vHolder.getAdapterPosition()).getId())});
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.accomplishlistremove), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.accomplishlistreremove), Toast.LENGTH_SHORT).show();
                        }


                    }
                });


                myDialog.show();
            }
        });



        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tv_placename.setText(mData.get(position).getPlacename());
        holder.tv_location.setText(mData.get(position).getLocation());
        holder.tv_district.setText(mData.get(position).getState());
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


        private LinearLayout item_explore;
        private TextView tv_placename;
        private TextView tv_location;
        private TextView tv_district;
        private ImageView img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            item_explore = itemView.findViewById(R.id.accomplished_item_id);
            tv_placename = itemView.findViewById(R.id.placename_accomplished);
            tv_location = itemView.findViewById(R.id.location_accomplished);
            tv_district = itemView.findViewById(R.id.state_accomplished);
            img = itemView.findViewById(R.id.img_accomplished);
        }
    }


}
