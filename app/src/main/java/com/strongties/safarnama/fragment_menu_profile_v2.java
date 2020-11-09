package com.strongties.safarnama;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static com.strongties.safarnama.MainActivity.accomplish_type_count;
import static com.strongties.safarnama.MainActivity.accomplished_list;
import static com.strongties.safarnama.MainActivity.bucket_type_count;

public class fragment_menu_profile_v2 extends Fragment {

    private static final String TAG = "Buddy Profile Fragment";

    Context mContext;

    User currentuser;

    private static final int REQUEST_IMAGE = 100;
    Uri imgurl;
    CircleImageView iv_new_dp;
    AtomicReference<Boolean> isImageChanged;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_profile_v2, container, false);

        mContext = getContext();


        assert mContext != null;
        currentuser = ((UserClient) (mContext.getApplicationContext())).getUser();


        //Shared Preferences
        SharedPreferences prefs = mContext.getSharedPreferences("myPrefs", MODE_PRIVATE);


        //Section 1 : Head

        //Declare variables
        TextView tv_name = root.findViewById(R.id.profile_v2_name);
        TextView tv_email = root.findViewById(R.id.profile_v2_email);
        TextView tv_locality = root.findViewById(R.id.profile_v2_locality);
        TextView tv_state = root.findViewById(R.id.profile_v2_state);
        ImageView iv_dp = root.findViewById(R.id.profile_v2_dp);
        CircleImageView iv_dp_edit = root.findViewById(R.id.profile_v2_edit);
        ImageView iv_name_edit = root.findViewById(R.id.profile_v2_name_edit_ic);
        TextView tv_buddycount = root.findViewById(R.id.profile_v2_buddy_count);

        //set Dp
        Glide.with(mContext)
                .load(currentuser.getPhoto())
                .placeholder(R.drawable.loading_image)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                .into(iv_dp);

        //DP Changes and Updates
        {

            iv_dp_edit.setOnClickListener(view -> {
                //Dialog Initiation
                Dialog myDialog = new Dialog(mContext);
                myDialog.setContentView(R.layout.dialog_dp_change);
                Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                iv_new_dp = myDialog.findViewById(R.id.dialog_dp_change_iv);
                AppCompatButton btn_save = myDialog.findViewById(R.id.dialog_dp_change_save_btn);
                TextView btn_cancel = myDialog.findViewById(R.id.dialog_dp_change_cancel_btn);

                //Show Old Image
                Glide.with(mContext)
                        .load(currentuser.getPhoto())
                        .placeholder(R.drawable.loading_image)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                        .into(iv_new_dp);
                iv_new_dp.setAlpha(0.7f);

                //Set a isImageChanged flag
                isImageChanged = new AtomicReference<>(Boolean.FALSE);
                ImagePickerActivity.clearCache(mContext);
                iv_new_dp.setOnClickListener(view1 -> {

                    view1.startAnimation(new AlphaAnimation(1F, 0.7F));

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


                btn_cancel.setOnClickListener(view1 -> {
                    myDialog.cancel();
                });
                btn_save.setOnClickListener(view1 -> {

                    if (isImageChanged.get()) {

                        ProgressDialog mProgressDialog = ProgressDialog.show(mContext, "Uploading", "Uploading Information to Server");
                        mProgressDialog.setCanceledOnTouchOutside(false);
                        StorageReference storageRef = FirebaseStorage.getInstance()
                                .getReference()
                                .child("android/images/dp/" + FirebaseAuth.getInstance().getUid() + "/dp.jpg");

                        storageRef.putFile(imgurl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                DocumentReference docRef = FirebaseFirestore.getInstance()
                                        .collection(getString(R.string.collection_users))
                                        .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                                docRef.get().addOnSuccessListener(documentSnapshot -> {
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
                                                        // Toast.makeText(mContext, "Image Uploaded", Toast.LENGTH_SHORT).show();
                                                        currentuser.setPhoto(uri.toString());
                                                        Glide.with(mContext)
                                                                .load(currentuser.getPhoto())
                                                                .placeholder(R.drawable.loading_image)
                                                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                                                                .into(iv_dp);
                                                        myDialog.cancel();
                                                    }
                                                });
                                            }
                                        });

                                    }
                                });

                                mProgressDialog.cancel();
                            }
                        });
                    }


                });

                myDialog.show();

            });
        }


        //set Username
        tv_name.setText(currentuser.getUsername());
        //Username Update Changes
        {
            iv_name_edit.setOnClickListener(view -> {
                //Dialog Initiation
                BottomSheetDialog myDialog = new BottomSheetDialog(mContext);
                myDialog.setContentView(R.layout.dialog_name_change);
                Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                EditText et_newname = myDialog.findViewById(R.id.dialog_name_change);
                AppCompatTextView btn_save = myDialog.findViewById(R.id.dialog_name_change_save_btn);
                AppCompatTextView btn_cancel = myDialog.findViewById(R.id.dialog_name_change_cancel_btn);

                btn_cancel.setOnClickListener(view1 -> {
                    myDialog.cancel();
                });
                btn_save.setOnClickListener(view1 -> {
                    DocumentReference docRef = FirebaseFirestore.getInstance()
                            .collection(getString(R.string.collection_users))
                            .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

                    User user = new User();
                    user.setUser_id(currentuser.getUser_id());
                    user.setUsername(et_newname.getText().toString());
                    user.setEmail(currentuser.getEmail());
                    user.setPhoto(currentuser.getPhoto());
                    user.setAvatar(currentuser.getAvatar());

                    docRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast toast = Toast.makeText(mContext, getString(R.string.change_success), Toast.LENGTH_SHORT);
                                toast.getView().setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.dialog_bg_toast_colored));
                                TextView toastmsg = toast.getView().findViewById(android.R.id.message);
                                toastmsg.setTextColor(Color.WHITE);
                                toast.show();
                            }
                        }
                    });

                    myDialog.cancel();
                    currentuser.setUsername(user.getUsername());
                    tv_name.setText(currentuser.getUsername());
                });

                myDialog.show();
            });
        }

        //set Email
        tv_email.setText(currentuser.getEmail());

        //Set Locality and Local State
        tv_locality.setText(prefs.getString("locality", "Unknown"));
        tv_state.setText(prefs.getString("localState", "Unknown"));

        //Set Buddy Count
        tv_buddycount.setText(Integer.toString(MainActivity.FriendList.size()));


        //Section 2 : Badge

        //Declare variables
        ImageView iv_badge = root.findViewById(R.id.profile_v2_badge);
        ProgressBar badge_progress = root.findViewById(R.id.profile_v2_badge_progress);
        TextView tv_badge = root.findViewById(R.id.profile_v2_badge_progress_text);
        ImageView iv_badgeicon0 = root.findViewById(R.id.profile_v2_badge_0);
        ImageView iv_badgeicon1 = root.findViewById(R.id.profile_v2_badge_1);
        ImageView iv_badgeicon2 = root.findViewById(R.id.profile_v2_badge_2);
        ImageView iv_badgeicon3 = root.findViewById(R.id.profile_v2_badge_3);
        ImageView iv_badgeicon4 = root.findViewById(R.id.profile_v2_badge_4);
        ImageView iv_badgeicon5 = root.findViewById(R.id.profile_v2_badge_5);

        int accomplished_count = accomplished_list.size();
        int limit = 0;
        int remain = 0;
        String badgeText = "";


        switch (currentuser.getAvatar()) {
            case "0 Star":
                limit = 1;
                remain = limit - accomplished_count;
                badgeText = remain + " ";
                if (remain == 1) {
                    badgeText += getString(R.string.badge_progress_singular);
                } else {
                    badgeText += getString(R.string.badge_progress_plural);
                }
                tv_badge.setText(badgeText);

                //Set Progress Bar
                badge_progress.setMax(limit);
                badge_progress.setProgress(remain);

                iv_badgeicon0.setAlpha(1f);
                iv_badgeicon1.setAlpha(0.2f);
                iv_badgeicon2.setAlpha(0.2f);
                iv_badgeicon3.setAlpha(0.2f);
                iv_badgeicon4.setAlpha(0.2f);
                iv_badgeicon5.setAlpha(0.2f);

                Glide.with(mContext)
                        .load(R.drawable.avatar_0_star)
                        .placeholder(R.drawable.loading_image)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                        .into(iv_badge);
                break;
            case "1 Star":
                limit = 10;
                remain = limit - accomplished_count;
                badgeText = remain + " ";
                if (remain == 1) {
                    badgeText += getString(R.string.badge_progress_singular);
                } else {
                    badgeText += getString(R.string.badge_progress_plural);
                }
                tv_badge.setText(badgeText);

                //Set Progress Bar
                badge_progress.setMax(limit);
                badge_progress.setProgress(remain);

                iv_badgeicon0.setAlpha(0.2f);
                iv_badgeicon1.setAlpha(1f);
                iv_badgeicon2.setAlpha(0.2f);
                iv_badgeicon3.setAlpha(0.2f);
                iv_badgeicon4.setAlpha(0.2f);
                iv_badgeicon5.setAlpha(0.2f);

                Glide.with(mContext)
                        .load(R.drawable.avatar_1_star)
                        .placeholder(R.drawable.loading_image)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                        .into(iv_badge);
                break;
            case "2 Star":
                limit = 30;
                remain = limit - accomplished_count;
                badgeText = remain + " ";
                if (remain == 1) {
                    badgeText += getString(R.string.badge_progress_singular);
                } else {
                    badgeText += getString(R.string.badge_progress_plural);
                }
                tv_badge.setText(badgeText);

                //Set Progress Bar
                badge_progress.setMax(limit);
                badge_progress.setProgress(remain);


                iv_badgeicon0.setAlpha(0.2f);
                iv_badgeicon1.setAlpha(0.2f);
                iv_badgeicon2.setAlpha(1f);
                iv_badgeicon3.setAlpha(0.2f);
                iv_badgeicon4.setAlpha(0.2f);
                iv_badgeicon5.setAlpha(0.2f);

                Glide.with(mContext)
                        .load(R.drawable.avatar_2_star)
                        .placeholder(R.drawable.loading_image)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                        .into(iv_badge);
                break;
            case "3 Star":
                limit = 50;
                remain = limit - accomplished_count;
                badgeText = remain + " ";
                if (remain == 1) {
                    badgeText += getString(R.string.badge_progress_singular);
                } else {
                    badgeText += getString(R.string.badge_progress_plural);
                }
                tv_badge.setText(badgeText);

                //Set Progress Bar
                badge_progress.setMax(limit);
                badge_progress.setProgress(remain);


                iv_badgeicon0.setAlpha(0.2f);
                iv_badgeicon1.setAlpha(0.2f);
                iv_badgeicon2.setAlpha(0.2f);
                iv_badgeicon3.setAlpha(1f);
                iv_badgeicon4.setAlpha(0.2f);
                iv_badgeicon5.setAlpha(0.2f);

                Glide.with(mContext)
                        .load(R.drawable.avatar_3_star)
                        .placeholder(R.drawable.loading_image)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                        .into(iv_badge);
                break;
            case "4 Star":
                limit = 100;
                remain = limit - accomplished_count;
                badgeText = remain + " ";
                if (remain == 1) {
                    badgeText += getString(R.string.badge_progress_singular);
                } else {
                    badgeText += getString(R.string.badge_progress_plural);
                }
                tv_badge.setText(badgeText);

                //Set Progress Bar
                badge_progress.setMax(limit);
                badge_progress.setProgress(remain);


                iv_badgeicon0.setAlpha(0.2f);
                iv_badgeicon1.setAlpha(0.2f);
                iv_badgeicon2.setAlpha(0.2f);
                iv_badgeicon3.setAlpha(0.2f);
                iv_badgeicon4.setAlpha(1f);
                iv_badgeicon5.setAlpha(0.2f);

                Glide.with(mContext)
                        .load(R.drawable.avatar_4_star)
                        .placeholder(R.drawable.loading_image)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                        .into(iv_badge);
                break;
            case "5 Star":
                tv_badge.setText(getString(R.string.avatar_5_congratulations));

                //Set Progress Bar
                badge_progress.setMax(100);
                badge_progress.setProgress(100);


                iv_badgeicon0.setAlpha(0.2f);
                iv_badgeicon1.setAlpha(0.2f);
                iv_badgeicon2.setAlpha(0.2f);
                iv_badgeicon3.setAlpha(0.2f);
                iv_badgeicon4.setAlpha(0.2f);
                iv_badgeicon5.setAlpha(1f);

                Glide.with(mContext)
                        .load(R.drawable.avatar_5_star)
                        .placeholder(R.drawable.loading_image)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                        .into(iv_badge);
                break;
            case "Developer":
                tv_badge.setText("Congratulations on being a developer");


                //Set Progress Bar
                if (Build.VERSION.SDK_INT >= 24) {
                    badge_progress.setProgress(100, true);
                } else {
                    badge_progress.setProgress(100);
                }

                iv_badgeicon0.setAlpha(0.2f);
                iv_badgeicon1.setAlpha(0.2f);
                iv_badgeicon2.setAlpha(0.2f);
                iv_badgeicon3.setAlpha(0.2f);
                iv_badgeicon4.setAlpha(0.2f);
                iv_badgeicon5.setAlpha(0.2f);

                Glide.with(mContext)
                        .load(R.drawable.avatar_6_star)
                        .placeholder(R.drawable.loading_image)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                        .into(iv_badge);
                break;
            default:
                Glide.with(mContext)
                        .load(R.drawable.loading_image)
                        .placeholder(R.drawable.loading_image)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                        .into(iv_badge);
                break;
        }


        //Section 2 : Badge

        //Declare variables
        TextView tv_stat_bucketlist = root.findViewById(R.id.profile_v2_stat_bucket);
        TextView tv_stat_accomlished = root.findViewById(R.id.profile_v2_stat_accomplished);

        //Set the stats
        tv_stat_bucketlist.setText(Integer.toString(MainActivity.bucket_list.size()));
        tv_stat_accomlished.setText(Integer.toString(accomplished_list.size()));

        //Plot Chart
        plot_chart1(root);

        return root;
    }

    void plot_chart1(View view) {

        List<BarEntry> yAccomplish = new ArrayList<>();
        List<BarEntry> yBucket = new ArrayList<>();

        ArrayList<String> xVals = new ArrayList<>();


        /*        xVals.add(getString(R.string.category1));
        xVals.add(getString(R.string.category2));
        xVals.add(getString(R.string.category3));
        xVals.add(getString(R.string.category4));
        xVals.add(getString(R.string.category5));
        xVals.add(getString(R.string.category6));
        xVals.add(getString(R.string.category7));
        xVals.add(getString(R.string.category8));
        xVals.add(getString(R.string.category9));
        xVals.add(getString(R.string.category10));

 */


        int count = 0;
        for (int value : bucket_type_count.values()) {
            yBucket.add(new BarEntry(count, value));
            Log.d(TAG, "Test Cat Bucket - " + count + " -> " + value);
            count += 1;
        }

        count = 0;
        for (Map.Entry<String, Integer> entry : accomplish_type_count.entrySet()) {

            String key = entry.getKey();
            Integer value = entry.getValue();

            yAccomplish.add(new BarEntry(count, value));
            Log.d(TAG, "Test Cat Accomplished - " + count + " -> " + value);

            xVals.add(key);
            count += 1;
        }

        /*
        count = 0;
        for(Map.Entry<String, Integer> entry : bucket_type_count.entrySet()) {

            String key = entry.getKey();
            Integer value = entry.getValue();

            Log.d(TAG, "Test Cat Bucket - type - "+ key + " count - " + count + " -> " + value);
            count += 1;
        }
*/


        BarChart barChart = view.findViewById(R.id.profile_v2_chart1);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setEnabled(false);
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);


        //data


        BarDataSet set1, set2;

        // create 2 datasets with different types
        set1 = new BarDataSet(yAccomplish, getString(R.string.accomplished));
        set1.setColor(Color.rgb(2, 244, 245));
        set2 = new BarDataSet(yBucket, getString(R.string.wishlist));
        set2.setColor(Color.rgb(250, 10, 2));
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        dataSets.add(set2);
        BarData data = new BarData(dataSets);
        barChart.setData(data);


        barChart.setTouchEnabled(true);

        float groupSpace = 0.1f;
        float barSpace = 0.00f; // x2 dataset
        float barWidth = 0.45f; // x2 dataset
        // (0.46 + 0.02) * 2 + 0.04 = 1.00 -> interval per "group"


        barChart.getBarData().setBarWidth(barWidth);
        barChart.getXAxis().setAxisMinimum(0);
        barChart.groupBars(0, groupSpace, barSpace);


        //Y-axis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(10f);
        leftAxis.setAxisMinimum(0f);
        barChart.getAxisRight().setEnabled(false);

        //X-axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(0.5f);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(true);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(10);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));
        xAxis.setLabelRotationAngle(-90f);

        Legend legend = barChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);

        barChart.zoom(1.15f, 0.9f, 0.0f, 0.0f);

        barChart.invalidate();


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
                .into(iv_new_dp);
        iv_new_dp.setColorFilter(ContextCompat.getColor(mContext, android.R.color.transparent));
        iv_new_dp.setAlpha(1f);
        isImageChanged.set(Boolean.TRUE);
    }


}
