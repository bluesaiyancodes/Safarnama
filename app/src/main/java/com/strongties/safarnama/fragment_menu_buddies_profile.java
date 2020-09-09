package com.strongties.safarnama;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.strongties.safarnama.user_classes.User;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.strongties.safarnama.MainActivity.accomplished_list;
import static com.strongties.safarnama.MainActivity.bucket_list;

public class fragment_menu_buddies_profile extends Fragment {

    private static final String TAG = "Buddy Profile Fragment";
    private static final int REQUEST_IMAGE = 100;

    Context mContext;

    User currentuser;

    ImageView iv_photo;
    EditText et_name;


    StorageReference storageRef;
    Boolean imageChanged = Boolean.FALSE;
    Uri imgurl;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_buddies_profile, container, false);

        mContext = getContext();


        assert mContext != null;
        currentuser = ((UserClient)(mContext.getApplicationContext())).getUser();


        et_name = root.findViewById(R.id.profile_name);
        TextView tv_email = root.findViewById(R.id.profile_email);
        iv_photo = root.findViewById(R.id.menu3_profile_img);
        ImageView iv_avatar = root.findViewById(R.id.menu3_buddies_profile_badge);

        Button btn_done = root.findViewById(R.id.profile_done);
        Button btn_save = root.findViewById(R.id.profile_save_changes);

        et_name.setText(currentuser.getUsername());
        tv_email.setText(currentuser.getEmail());


        TextView bucketcount = root.findViewById(R.id.profile_bucket_count);
        Log.d(TAG, "bucketSize -> " + bucket_list.size());
        bucketcount.setText(Integer.toString(bucket_list.size()));

        TextView accomplishcount = root.findViewById(R.id.profile_accomplished_count);
        accomplishcount.setText(Integer.toString(accomplished_list.size()));


        Glide.with(mContext)
                .load(currentuser.getPhoto())
                .placeholder(R.drawable.loading_image)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                .into(iv_photo);

        switch (currentuser.getAvatar()) {
            case "0 Star":
                Glide.with(mContext)
                        .load(R.drawable.avatar_0_star)
                        .placeholder(R.drawable.loading_image)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                        .into(iv_avatar);
                break;
            case "1 Star":
                Glide.with(mContext)
                        .load(R.drawable.avatar_1_star)
                        .placeholder(R.drawable.loading_image)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                        .into(iv_avatar);
                break;
            case "2 Star":
                Glide.with(mContext)
                        .load(R.drawable.avatar_2_star)
                        .placeholder(R.drawable.loading_image)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                        .into(iv_avatar);
                break;
            case "3 Star":
                Glide.with(mContext)
                        .load(R.drawable.avatar_3_star)
                        .placeholder(R.drawable.loading_image)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                        .into(iv_avatar);
                break;
            case "4 Star":
                Glide.with(mContext)
                        .load(R.drawable.avatar_4_star)
                        .placeholder(R.drawable.loading_image)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                        .into(iv_avatar);
                break;
            case "5 Star":
                Glide.with(mContext)
                        .load(R.drawable.avatar_5_star)
                        .placeholder(R.drawable.loading_image)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                        .into(iv_avatar);
                break;
            case "Developer":
                Glide.with(mContext)
                        .load(R.drawable.avatar_6_star)
                        .placeholder(R.drawable.loading_image)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                        .into(iv_avatar);
                break;
            default:
                Glide.with(mContext)
                        .load(R.drawable.loading_image)
                        .placeholder(R.drawable.loading_image)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                        .into(iv_avatar);
                break;
        }


        Intent intent = Objects.requireNonNull(getActivity()).getIntent();
        ImagePickerActivity.clearCache(mContext);

        iv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(new AlphaAnimation(1F, 0.7F));
                /*
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

               // startActivityForResult(chooserIntent, PICK_IMAGE);

                 */


                Dexter.withActivity(getActivity())
                        .withPermissions(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    showImagePickerOptions();
                                }

                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    showSettingsDialog();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }

                        }).check();


            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(new AlphaAnimation(1F, 0.7F));
                ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_bottom)
                        .replace(R.id.fragment_container, new fragment_menu_buddies(), "Buddy Profile Fragment").commit();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(new AlphaAnimation(1F, 0.7F));
                //Toast.makeText(mContext, "Save ", Toast.LENGTH_SHORT).show();

              /*
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
               */


                //Upload Image to Firebase Storage
                ProgressDialog mProgressDialog = ProgressDialog.show(mContext, "Uploading", "Uploading Information to Server");
                mProgressDialog.setCanceledOnTouchOutside(false);

                if (imageChanged) {
                    storageRef = FirebaseStorage.getInstance()
                            .getReference()
                            .child("images/" + FirebaseAuth.getInstance().getUid() + "/dp.jpg");

                    storageRef.putFile(imgurl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            DocumentReference docRef = FirebaseFirestore.getInstance()
                                    .collection(getString(R.string.collection_users))
                                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        User user = documentSnapshot.toObject(User.class);
                                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                assert user != null;
                                                user.setPhoto(uri.toString());
                                                DocumentReference dRef = FirebaseFirestore.getInstance()
                                                        .collection(getString(R.string.collection_users))
                                                        .document(FirebaseAuth.getInstance().getUid());
                                                dRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(mContext, "Image Uploaded", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });

                                    }
                                }
                            });

                            mProgressDialog.cancel();
                        }
                    });
                }


                if (!et_name.getText().toString().equals(currentuser.getUsername())) {
                    // currentuser.setUsername(et_name.getText().toString());

                    DocumentReference docRef = FirebaseFirestore.getInstance()
                            .collection(getString(R.string.collection_users))
                            .document(FirebaseAuth.getInstance().getUid());

                    User user = new User();
                    user.setUser_id(currentuser.getUser_id());
                    user.setUsername(et_name.getText().toString());
                    user.setEmail(currentuser.getEmail());
                    user.setPhoto(currentuser.getPhoto());
                    user.setAvatar(currentuser.getAvatar());

                    docRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast toast = Toast.makeText(mContext, getString(R.string.change_success), Toast.LENGTH_SHORT);
                                toast.getView().setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.dialog_bg_toast_colored));
                                TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                                toastmsg.setTextColor(Color.WHITE);
                                toast.show();

                                Log.d(TAG, "Successfully added to User feed list ");
                            }else{
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });

                }


            }
        });


        return root;
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(mContext, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }


    private void launchCameraIntent() {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), uri);

                    // loading profile image from local cache
                    assert uri != null;
                    imgurl = uri;
                    loadProfile(uri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", Objects.requireNonNull(getActivity()).getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void loadProfile(String url) {
        Log.d(TAG, "Image cache path: " + url);

        Glide.with(this).load(url)
                .into(iv_photo);
        iv_photo.setColorFilter(ContextCompat.getColor(mContext, android.R.color.transparent));
        imageChanged = Boolean.TRUE;
    }


    /*


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {

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

     */


}
