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
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.strongties.safarnama.adapters.RecyclerViewAdapter_feed;
import com.strongties.safarnama.user_classes.UserFeed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class fragment_menu_feed extends Fragment {
    public static final String TAG = "FeedFragment";
    Context mContext;
    FirebaseFirestore mDb;
    Button public_feed, buddy_feed;
    private String mode = "global";
    private RecyclerViewAdapter_feed adapter_feed = null;
    private static final int REQUEST_IMAGE = 100;

    Uri imgurl;
    ImageView iv_image;
    private Boolean imgLoaded = Boolean.FALSE;

    public fragment_menu_feed() {
        this.mode = "global";
    }

    public fragment_menu_feed(String mode) {
        this.mode = mode;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_feed, container, false);

        mContext = getContext();
        mDb = FirebaseFirestore.getInstance();


        FloatingActionButton fab = root.findViewById(R.id.fab);
        public_feed = root.findViewById(R.id.menu_feed_public);
        buddy_feed = root.findViewById(R.id.menu_feed_buddy);


        if(mode.equals("global")){
            buddy_feed.setAlpha((float) 0.5);
        }else {
            public_feed.setAlpha((float) 0.5);
        }


        RecyclerView myrecyclerview = root.findViewById(R.id.rv_feed);
        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        myrecyclerview.setAdapter(adapter_feed);


        fab.setOnClickListener(view -> {

            view.startAnimation(new AlphaAnimation(1F, 0.7F));

            Dialog feed_insert_dialog = new Dialog(mContext);
            feed_insert_dialog.setContentView(R.layout.dialog_feed_insert);
            Objects.requireNonNull(feed_insert_dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            feed_insert_dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            RadioGroup type_text_radio = feed_insert_dialog.findViewById(R.id.post_type_text_radio);
            Button btn_sel_img = feed_insert_dialog.findViewById(R.id.post_sel_img);
            iv_image = feed_insert_dialog.findViewById(R.id.post_img);
            final EditText feed_body = feed_insert_dialog.findViewById(R.id.feed_insert_text);
            Button post = feed_insert_dialog.findViewById(R.id.feed_post_btn);


            btn_sel_img.setOnClickListener(v -> {
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


            post.setOnClickListener(v -> {
                v.startAnimation(new AlphaAnimation(1F, 0.7F));
                RadioButton type_text = feed_insert_dialog.findViewById(type_text_radio.getCheckedRadioButtonId());

                UserFeed userFeed = new UserFeed();
                userFeed.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                userFeed.setType(type_text.getText().toString());
                userFeed.setDatacontent(feed_body.getText().toString());
                userFeed.setLandmarkIncluded(Boolean.FALSE);
                userFeed.setLandmarkId("none");

                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();


                if (imgLoaded) {
                    ProgressDialog mProgressDialog = ProgressDialog.show(mContext, getString(R.string.uploading), getString(R.string.upload_post));
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    StorageReference storageRef = FirebaseStorage.getInstance()
                            .getReference()
                            .child("android/images/feed/" + ts + ".jpg");

                    storageRef.putFile(imgurl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    userFeed.setImgIncluded(Boolean.TRUE);
                                    userFeed.setImgUrl(uri.toString());


                                    DocumentReference docRef = mDb
                                            .collection(getString(R.string.collection_feed))
                                            .document(getString(R.string.document_global))
                                            .collection(getString(R.string.collection_posts))
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

                                    docRef = mDb
                                            .collection(getString(R.string.collection_feed))
                                            .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                            .collection(getString(R.string.collection_posts))
                                            .document(ts);

                                    docRef.set(userFeed).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast toast = Toast.makeText(mContext, getString(R.string.feed_post_success), Toast.LENGTH_SHORT);
                                                toast.getView().setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.dialog_bg_toast_colored));
                                                TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                                                toastmsg.setTextColor(Color.WHITE);
                                                toast.show();
                                                feed_insert_dialog.cancel();

                                                Log.d(TAG, "Successfully added to User feed list ");
                                            } else {
                                                Log.w(TAG, "Error getting documents.", task.getException());
                                            }
                                        }
                                    });


                                }
                            });

                            mProgressDialog.cancel();
                        }
                    });
                } else {

                    userFeed.setImgIncluded(Boolean.FALSE);
                    userFeed.setImgUrl("None");

                    DocumentReference docRef = mDb
                            .collection(getString(R.string.collection_feed))
                            .document(getString(R.string.document_global))
                            .collection(getString(R.string.collection_posts))
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

                    docRef = mDb
                            .collection(getString(R.string.collection_feed))
                            .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                            .collection(getString(R.string.collection_posts))
                            .document(ts);

                    docRef.set(userFeed).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast toast = Toast.makeText(mContext, getString(R.string.feed_post_success), Toast.LENGTH_SHORT);
                                toast.getView().setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.dialog_bg_toast_colored));
                                TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                                toastmsg.setTextColor(Color.WHITE);
                                toast.show();
                                feed_insert_dialog.cancel();

                                Log.d(TAG, "Successfully added to User feed list ");
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
                }


            });
            feed_insert_dialog.show();


        });


        public_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(new AlphaAnimation(1F, 0.7F));
                ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, new fragment_menu_feed("global"), "Global Feed Fragment").commit();
            }
        });

        buddy_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(new AlphaAnimation(1F, 0.7F));
                ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                        .replace(R.id.fragment_container, new fragment_menu_feed("buddy"), "Buddy Feed Fragment").commit();
            }
        });
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        getallfriends();
        if (adapter_feed != null) {
            adapter_feed.startListening();
        }


    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter_feed != null) {
            adapter_feed.stopListening();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDb = FirebaseFirestore.getInstance();

        if (mode.equals("global")) {

            ArrayList<String> typeList = new ArrayList<>();
            typeList.add(getString(R.string.public_feed));


            CollectionReference collRef = mDb
                    .collection(getString(R.string.collection_feed))
                    .document(getString(R.string.document_global))
                    .collection(getString(R.string.collection_posts));

            Query query = collRef
                    .whereIn("type", typeList)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(30);

                FirestoreRecyclerOptions<UserFeed> option = new FirestoreRecyclerOptions.Builder<UserFeed>()
                        .setQuery(query, UserFeed.class)
                        .build();
                adapter_feed = new RecyclerViewAdapter_feed(option, mode);

        }else {
            //adapter_feed = null;


            CollectionReference collRef = FirebaseFirestore.getInstance()
                    .collection(getString(R.string.collection_feed))
                    .document(getString(R.string.document_global))
                    .collection(getString(R.string.collection_posts));

            if (MainActivity.FriendList.size() != 0) {

                Query query = collRef
                        .whereIn("user_id", MainActivity.FriendList)
                        .orderBy("timestamp", Query.Direction.DESCENDING);

                FirestoreRecyclerOptions<UserFeed> option = new FirestoreRecyclerOptions.Builder<UserFeed>()
                        .setQuery(query, UserFeed.class)
                        .build();
                adapter_feed = new RecyclerViewAdapter_feed(option, mode);
            }


        }


    }

    private void getallfriends() {

        CollectionReference collRef = FirebaseFirestore.getInstance()
                .collection(getString(R.string.collection_relations))
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection(getString(R.string.collection_friendlist));

        collRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        String user_id = document.getId();
                        if(!MainActivity.FriendList.contains(user_id)){
                            MainActivity.FriendList.add(user_id);
                        }
                    }
                }
            }
        });

        Log.d(TAG, "FriendList" + MainActivity.FriendList);

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

        iv_image.setVisibility(View.VISIBLE);
        Glide.with(this).load(url)
                .into(iv_image);
        iv_image.setColorFilter(ContextCompat.getColor(mContext, android.R.color.transparent));
        imgLoaded = Boolean.TRUE;
    }

}
