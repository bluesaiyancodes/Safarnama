package com.strongties.safarnama;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.strongties.safarnama.adapters.RecyclerViewAdapter_inspiration;
import com.strongties.safarnama.user_classes.RV_Inspiration;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;


public class fragment_menu_inspiration extends Fragment {
    private static final String TAG = "Inspiration Fragment";
    private static final int REQUEST_IMAGE = 100;
    Context mContext;
    Uri imgurl;
    ImageView iv_img_upload;
    AtomicReference<Boolean> isImageChanged;
    RecyclerViewAdapter_inspiration inspiration_adapter;
    private RecyclerView inspiration_recycler_view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();


        CollectionReference inspirations = FirebaseFirestore.getInstance()
                .collection(getString(R.string.collection_inspirations));

        Query query = inspirations
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<RV_Inspiration> option = new FirestoreRecyclerOptions.Builder<RV_Inspiration>()
                .setQuery(query, RV_Inspiration.class)
                .build();

        Lifecycle lifecycle = getLifecycle();
        inspiration_adapter = new RecyclerViewAdapter_inspiration(option, lifecycle);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_menu_inspiration, container, false);
        isImageChanged = new AtomicReference<>(Boolean.FALSE);


        //FAB
        FloatingActionButton fab = root.findViewById(R.id.inspiration_fab);

        DocumentReference documentReference = FirebaseFirestore.getInstance()
                .collection("Dev")
                .document("developers")
                .collection("contributors")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().exists()) {
                    fab.setVisibility(View.GONE);
                } else {
                    fab.setVisibility(View.VISIBLE);
                }
            }
        });

        //Toast.makeText(mContext, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail(), Toast.LENGTH_SHORT).show();


        fab.setOnClickListener(view -> {

            //Dialog Initiation
            Dialog myDialog = new Dialog(mContext);
            myDialog.setContentView(R.layout.dialog_inspiration);
            Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            //Content Chooser
            RelativeLayout rl_contentchooser = myDialog.findViewById(R.id.dialog_inspiration_content_chooser);
            AppCompatButton btn_contentchooser_img = myDialog.findViewById(R.id.dialog_inspiration_image_btn);
            AppCompatButton btn_contentchooser_vid = myDialog.findViewById(R.id.dialog_inspiration_video_btn);

            //Image Content Chooser
            RelativeLayout rl_img_type = myDialog.findViewById(R.id.dialog_inspiration_image_content_chooser);
            AppCompatButton btn_img_type_upload = myDialog.findViewById(R.id.dialog_inspiration_image_upload);
            AppCompatButton btn_img_type_link = myDialog.findViewById(R.id.dialog_inspiration_image_link);

            //Image Content: Upload
            RelativeLayout rl_img_upload = myDialog.findViewById(R.id.dialog_inspiration_content_image_upload_layout);
            iv_img_upload = myDialog.findViewById(R.id.dialog_inspiration_content_image_upload_img);
            TextView tv_img_upload_error = myDialog.findViewById(R.id.dialog_inspiration_content_image_upload_error);
            EditText et_img_upload_loc = myDialog.findViewById(R.id.dialog_inspiration_content_image_upload_loc);
            TextView tv_img_upload_add_desc = myDialog.findViewById(R.id.dialog_inspiration_content_image_upload_add_desc);
            EditText et_img_upload_desc = myDialog.findViewById(R.id.dialog_inspiration_content_image_upload_desc);

            //Image Content: Link
            RelativeLayout rl_img_link = myDialog.findViewById(R.id.dialog_inspiration_content_image_link_layout);
            EditText et_img_link_url = myDialog.findViewById(R.id.dialog_inspiration_content_image_link_url);
            TextView tv_img_link_error = myDialog.findViewById(R.id.dialog_inspiration_content_image_link_error);
            EditText et_img_link_loc = myDialog.findViewById(R.id.dialog_inspiration_content_image_link_loc);
            TextView tv_img_link_add_desc = myDialog.findViewById(R.id.dialog_inspiration_content_image_link_add_desc);
            EditText et_img_link_desc = myDialog.findViewById(R.id.dialog_inspiration_content_image_link_desc);

            //Video Content: Link
            RelativeLayout rl_vid_link = myDialog.findViewById(R.id.dialog_inspiration_content_video_link_layout);
            EditText et_vid_link_url = myDialog.findViewById(R.id.dialog_inspiration_content_video_link_url);
            TextView tv_vid_link_error = myDialog.findViewById(R.id.dialog_inspiration_content_video_link_error);
            EditText et_vid_link_loc = myDialog.findViewById(R.id.dialog_inspiration_content_video_link_loc);
            TextView tv_vid_link_add_desc = myDialog.findViewById(R.id.dialog_inspiration_content_video_link_add_desc);
            EditText et_vid_link_desc = myDialog.findViewById(R.id.dialog_inspiration_content_video_link_desc);

            //submit button
            AppCompatButton btn_post = myDialog.findViewById(R.id.dialog_inspiration_btn);


            RV_Inspiration inspiration = new RV_Inspiration();

            //show content chooser
            rl_contentchooser.setVisibility(View.VISIBLE);
            rl_img_type.setVisibility(View.GONE);
            rl_img_upload.setVisibility(View.GONE);
            rl_img_link.setVisibility(View.GONE);
            rl_vid_link.setVisibility(View.GONE);
            btn_post.setVisibility(View.GONE);

            //define content chooser

            //if video is chosen
            btn_contentchooser_vid.setOnClickListener(view1 -> {
                rl_contentchooser.setVisibility(View.GONE);
                rl_vid_link.setVisibility(View.VISIBLE);
                et_vid_link_desc.setVisibility(View.GONE);

                btn_post.setVisibility(View.VISIBLE);

                inspiration.setContent_type(getString(R.string.video));
            });
            //add desc functionality
            tv_vid_link_add_desc.setOnClickListener(view1 -> {
                et_vid_link_desc.setVisibility(View.VISIBLE);
            });


            //if image is chosen
            btn_contentchooser_img.setOnClickListener(view1 -> {
                rl_contentchooser.setVisibility(View.GONE);
                rl_img_type.setVisibility(View.VISIBLE);

                inspiration.setContent_type(getString(R.string.image));
            });

            //img type upload is chosen
            btn_img_type_upload.setOnClickListener(view1 -> {
                rl_img_type.setVisibility(View.GONE);
                rl_img_upload.setVisibility(View.VISIBLE);
                et_img_upload_desc.setVisibility(View.GONE);

                btn_post.setVisibility(View.VISIBLE);
            });
            //add desc functionality
            tv_img_upload_add_desc.setOnClickListener(view1 -> {
                et_img_upload_desc.setVisibility(View.VISIBLE);
            });

            //img type link is chosen
            btn_img_type_link.setOnClickListener(view1 -> {
                rl_img_type.setVisibility(View.GONE);
                rl_img_link.setVisibility(View.VISIBLE);
                et_img_link_desc.setVisibility(View.GONE);

                btn_post.setVisibility(View.VISIBLE);
            });
            //add desc functionality
            tv_img_link_add_desc.setOnClickListener(view1 -> {
                et_img_link_desc.setVisibility(View.VISIBLE);
            });

            //Timestamp
            long tsLong = System.currentTimeMillis() / 1000;
            String ts = Long.toString(tsLong);

            //image selection for upload functionality
            iv_img_upload.setOnClickListener(view1 -> {

                ImagePickerActivity.clearCache(mContext);
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


            });

            btn_post.setOnClickListener(view1 -> {
                //For Video URL submission
                if (inspiration.getContent_type().equals(getString(R.string.video))) {
                    //check for empty URL
                    if (et_vid_link_url.getText().toString().equals("")) {
                        tv_vid_link_error.setText(getString(R.string.alert_empty_url));
                        tv_vid_link_error.setVisibility(View.VISIBLE);
                    } else {
                        //check for empty location
                        if (et_vid_link_loc.getText().toString().equals("")) {
                            tv_vid_link_error.setText(getString(R.string.empty_location));
                            tv_vid_link_error.setVisibility(View.VISIBLE);
                        } else {
                            tv_vid_link_error.setVisibility(View.GONE);

                            //add timestamp as id
                            inspiration.setContent_id(ts);
                            //add url
                            inspiration.setContent_url(et_vid_link_url.getText().toString().trim());
                            //add location
                            inspiration.setContent_location(et_vid_link_loc.getText().toString().trim());
                            //add description
                            if (et_vid_link_desc.getText().toString().equals("")) {
                                inspiration.setContent_description("NA");
                            } else {
                                inspiration.setContent_description(et_vid_link_desc.getText().toString());
                            }
                            //set like count
                            inspiration.setLike_count(0);


                            //upload Data
                            DocumentReference docRef = FirebaseFirestore.getInstance()
                                    .collection(getString(R.string.collection_inspirations))
                                    .document(ts);

                            docRef.set(inspiration).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(mContext, getString(R.string.post_success), Toast.LENGTH_LONG).show();
                                    myDialog.cancel();
                                }
                            });

                        }
                    }
                } else if (inspiration.getContent_type().equals(getString(R.string.image))) {

                    //check for image type
                    //if image upload else img link
                    if (isImageChanged.get()) {

                        //check for empty location
                        if (et_img_upload_loc.getText().toString().equals("")) {
                            tv_img_upload_error.setText(getString(R.string.empty_location));
                            tv_img_upload_error.setVisibility(View.VISIBLE);
                        } else {
                            tv_img_upload_error.setVisibility(View.GONE);
                            //add location
                            inspiration.setContent_location(et_img_upload_loc.getText().toString().trim());
                            //set id as timestamp
                            inspiration.setContent_id(ts);
                            //add description
                            if (et_img_upload_desc.getText().toString().equals("")) {
                                inspiration.setContent_description("NA");
                            } else {
                                inspiration.setContent_description(et_img_upload_desc.getText().toString());
                            }
                            //set like count
                            inspiration.setLike_count(0);

                            //set url
                            ProgressDialog mProgressDialog = ProgressDialog.show(mContext, "Uploading", "Uploading Information to Server");
                            mProgressDialog.setCanceledOnTouchOutside(false);
                            StorageReference storageRef = FirebaseStorage.getInstance()
                                    .getReference()
                                    .child("android/images/inspiration/image_" + ts + ".jpg");

                            storageRef.putFile(imgurl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            inspiration.setContent_url(uri.toString());

                                            //upload Data
                                            DocumentReference docRef = FirebaseFirestore.getInstance()
                                                    .collection(getString(R.string.collection_inspirations))
                                                    .document(ts);

                                            docRef.set(inspiration).addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(mContext, getString(R.string.post_success), Toast.LENGTH_LONG).show();
                                                }
                                            });


                                        }
                                    });

                                    mProgressDialog.cancel();
                                    myDialog.cancel();
                                }
                            });

                        }


                    } else {

                        //check for empty URL
                        if (et_img_link_url.getText().toString().equals("")) {
                            tv_img_link_error.setText(getString(R.string.alert_empty_url));
                            tv_img_link_error.setVisibility(View.VISIBLE);
                        } else if (!(et_img_link_url.getText().toString().contains(".jpg") || et_img_link_url.getText().toString().contains(".jpeg") || et_img_link_url.getText().toString().contains(".png"))) {
                            tv_img_link_error.setText(getString(R.string.invalid_img_url));
                        } else {
                            //check for empty location
                            if (et_img_link_loc.getText().toString().equals("")) {
                                tv_img_link_error.setText(getString(R.string.empty_location));
                                tv_img_link_error.setVisibility(View.VISIBLE);
                            } else {
                                tv_img_link_error.setVisibility(View.GONE);

                                //add timestamp as id
                                inspiration.setContent_id(ts);
                                //add url
                                inspiration.setContent_url(et_img_link_url.getText().toString().trim());
                                //add location
                                inspiration.setContent_location(et_img_link_loc.getText().toString().trim());
                                //add description
                                if (et_img_link_desc.getText().toString().equals("")) {
                                    inspiration.setContent_description("NA");
                                } else {
                                    inspiration.setContent_description(et_img_link_desc.getText().toString());
                                }
                                //set like count
                                inspiration.setLike_count(0);


                                //upload Data
                                DocumentReference docRef = FirebaseFirestore.getInstance()
                                        .collection(getString(R.string.collection_inspirations))
                                        .document(ts);

                                docRef.set(inspiration).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(mContext, getString(R.string.post_success), Toast.LENGTH_LONG).show();
                                        myDialog.cancel();
                                    }
                                });

                            }
                        }


                    }

                }
            });


            myDialog.show();

        });


        //Set Recycler View
        inspiration_recycler_view = root.findViewById(R.id.inspiration_recycler_view);
        inspiration_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        inspiration_recycler_view.setAdapter(inspiration_adapter);


        return root;
    }


    @Override
    public void onStart() {
        super.onStart();
        inspiration_adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        inspiration_adapter.stopListening();
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
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);

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
        Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void loadProfile(String url) {
        Log.d(TAG, "Image cache path: " + url);

        Glide.with(this).load(url)
                .into(iv_img_upload);
        iv_img_upload.setColorFilter(ContextCompat.getColor(mContext, android.R.color.transparent));
        isImageChanged.set(Boolean.TRUE);
    }
}