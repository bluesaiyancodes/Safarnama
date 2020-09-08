package com.strongties.safarnama;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.strongties.safarnama.adapters.RecylerViewAdapter_place_images;

import java.util.Objects;

public class placeImagesActivity extends AppCompatActivity{

    RecylerViewAdapter_place_images adapter_place_images;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_place_images);

        mContext = getBaseContext();


        Button btn_back = findViewById(R.id.dialog_place_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(new AlphaAnimation(1F, 0.7F));
                onBackPressed();
            }
        });


        String img_urls = Objects.requireNonNull(getIntent().getExtras()).getString("img_urls");
        String name = getIntent().getExtras().getString("name");
        setTitle(name);
        assert img_urls != null;
        String [] imgs = img_urls.split(" ");


        RecyclerView recyclerView = findViewById(R.id.rv_place_image);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter_place_images =  new RecylerViewAdapter_place_images(this, imgs, name);
        recyclerView.setAdapter(adapter_place_images);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

}
