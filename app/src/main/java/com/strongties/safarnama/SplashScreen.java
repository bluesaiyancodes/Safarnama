package com.strongties.safarnama;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;

public class SplashScreen extends AppCompatActivity {

    private ImageView imageView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


       // SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);


//        String mode = prefs.getString("mode", "light");
  //      if(mode.equals("light")){
    //        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
      //  }else {
        //    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        //}


        imageView = findViewById(R.id.splash_img);
        progressBar = findViewById(R.id.splash_progress);

        progressBar.setVisibility(View.VISIBLE);
/*
        Glide.with(this).asGif().load(R.raw.app_splash)
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SplashScreen.this.startActivity(new Intent(SplashScreen.this, LoginScreen.class));
                SplashScreen.this.finish();
            }
        },5300); //5.3 Secs
 */

        GifDrawable gifDrawable = null;
        try {
            gifDrawable = new GifDrawable(getResources(), R.raw.app_splash);
            gifDrawable.setLoopCount(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setImageDrawable(gifDrawable);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SplashScreen.this.startActivity(new Intent(SplashScreen.this, LoginScreen.class));
                SplashScreen.this.finish();
            }
        },5300); //5.3 Secs




    }
}
