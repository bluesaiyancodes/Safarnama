package com.strongties.safarnama;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.io.IOException;

import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;

//import com.bumptech.glide.load.resource.gif.GifDrawable;

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
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
      //  }else {
        //    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        //}


        imageView = findViewById(R.id.splash_img);
        progressBar = findViewById(R.id.splash_progress);

        // progressBar.setVisibility(View.VISIBLE);
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
            gifDrawable.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted(int loopNumber) {
                    SplashScreen.this.startActivity(new Intent(SplashScreen.this, LoginScreen.class));
                    SplashScreen.this.finish();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setImageDrawable(gifDrawable);
/*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SplashScreen.this.startActivity(new Intent(SplashScreen.this, LoginScreen.class));
                SplashScreen.this.finish();
            }
        },4000); //4 Secs

 */

/*
        Glide.with(this)
                .asGif()
                .load(R.raw.app_splash)
                .listener(new RequestListener<GifDrawable>() {

                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                        resource.setLoopCount(1);
                        resource.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                            @Override
                            public void onAnimationEnd(Drawable drawable) {
                                SplashScreen.this.startActivity(new Intent(SplashScreen.this, LoginScreen.class));
                                SplashScreen.this.finish();
                            }
                        });
                        return false;
                    }
                })
                .into(imageView);

 */


    }
}
