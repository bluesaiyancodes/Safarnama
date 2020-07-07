package com.strongties.safarnama;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.strongties.safarnama.user_classes.User;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class fragment_menu_buddies_profile extends Fragment {

    private static final String TAG = "Buddy Profile Fragment";

    Context mContext;

    User currentuser;

    ImageView iv_photo;
    EditText et_name;
    public static final int PICK_IMAGE = 1;

    StorageReference storageRef;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_buddies_profile, container, false);

        mContext = getContext();

        assert mContext != null;
        currentuser = ((UserClient)(mContext.getApplicationContext())).getUser();


        et_name = root.findViewById(R.id.profile_name);
        TextView tv_email = root.findViewById(R.id.profile_email);
        iv_photo = root.findViewById(R.id.menu3_profile_img);

        Button btn_done = root.findViewById(R.id.profile_done);
        Button btn_save = root.findViewById(R.id.profile_save_changes);

        et_name.setText(currentuser.getUsername());
        tv_email.setText(currentuser.getEmail());

        Glide.with(mContext)
                .load(currentuser.getPhoto())
                .placeholder(R.drawable.loading_image)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                .into(iv_photo);


        iv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE);
            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_top,R.anim.exit_to_bottom)
                        .replace(R.id.fragment_container, new fragment_menu_buddies(), "Buddy Profile Fragment").commit();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, "Save ", Toast.LENGTH_SHORT).show();

                new AlertDialog.Builder(mContext)
                        .setTitle("Feature in Progress")
                        .setMessage("The saving feature is in development. We request you to maintain patience.")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                dialog.cancel();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();



                //Upload Image to Firebase Storage

                if(!et_name.getText().toString().equals(currentuser.getUsername())){
                   // currentuser.setUsername(et_name.getText().toString());

                }


            }
        });




        return root;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {
            //TODO: action

            try {
                InputStream inputStream = mContext.getContentResolver().openInputStream(Objects.requireNonNull(data.getData()));
                Glide.with(mContext)
                        .load(data.getDataString())
                        .placeholder(R.drawable.loading_image)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                        .into(iv_photo);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
