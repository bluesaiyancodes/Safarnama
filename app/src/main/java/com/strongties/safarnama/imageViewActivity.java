package com.strongties.safarnama;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.Objects;

public class imageViewActivity extends AppCompatActivity {

    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Image Saved in " + Environment.DIRECTORY_PICTURES, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        String imageUrl= Objects.requireNonNull(getIntent().getExtras()).getString("imageUrl");
        String name = getIntent().getExtras().getString("name");
        Button back_btn = findViewById(R.id.image_view_back);
        Button download_btn = findViewById(R.id.image_view_download);
        setTitle(name);
        assert name != null;
        name = name.replace(" ", "");

        PhotoView photoView = findViewById(R.id.photo_view);
        Glide.with(getBaseContext())
                .load(imageUrl)
               // .centerCrop()
                .placeholder(R.drawable.loading_image)
                // .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(photoView);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        String finalName = name;
        download_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                downloadFile(imageUrl, finalName);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void downloadFile(String uRl, String name) {
        long tsLong = System.currentTimeMillis()/1000;
        String ts = Long.toString(tsLong);

        String img_path = name + ts + ".jpg";

        DownloadManager mgr = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(uRl);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("Downloading Image")
                .setDescription("Please wait while the download completes")
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, img_path);

        registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        mgr.enqueue(request);

    }
}