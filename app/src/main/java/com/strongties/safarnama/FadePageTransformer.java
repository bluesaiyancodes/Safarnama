package com.strongties.safarnama;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

public class FadePageTransformer implements ViewPager.PageTransformer {
    public void transformPage(@NonNull View page, float position) {
        page.setAlpha(0f);
        page.setVisibility(View.VISIBLE);

        // Start Animation for a short period of time
        page.animate()
                .alpha(1f)
                .setDuration(page.getResources().getInteger(android.R.integer.config_shortAnimTime));
    }

}