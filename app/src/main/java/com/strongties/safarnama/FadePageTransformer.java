package com.strongties.safarnama;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class FadePageTransformer implements ViewPager.PageTransformer {
    /*public void transformPage(@NonNull View page, float position) {
        page.setAlpha(0f);
        page.setVisibility(View.VISIBLE);

        // Start Animation for a short period of time
        page.animate()
                .alpha(1f)
                .setDuration(page.getResources().getInteger(android.R.integer.config_shortAnimTime));
    }

     */

    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    @Override
    public void transformPage( View page, float position ) {
        int pageWidth = page.getWidth();
        int pageHeight = page.getHeight();

        if ( position < -1 ) { // [ -Infinity,-1 )
            // This page is way off-screen to the left.
            page.setAlpha( 0 );
        }
        else if ( position <= 1 ) { // [ -1,1 ]
            // Modify the default slide transition to shrink the page as well
            float scaleFactor = Math.max( MIN_SCALE, 1 - Math.abs( position ) );
            float vertMargin = pageHeight * ( 1 - scaleFactor ) / 2;
            float horzMargin = pageWidth * ( 1 - scaleFactor ) / 2;
            if ( position < 0 ) {
                page.setTranslationX( horzMargin - vertMargin / 2 );
            } else {
                page.setTranslationX( -horzMargin + vertMargin / 2 );
            }

            // Scale the page down ( between MIN_SCALE and 1 )
            page.setScaleX( scaleFactor );
            page.setScaleY( scaleFactor );

            // Fade the page relative to its size.
            page.setAlpha( MIN_ALPHA +
                    ( scaleFactor - MIN_SCALE ) /
                            ( 1 - MIN_SCALE ) * ( 1 - MIN_ALPHA ));

        } else { // ( 1,+Infinity ]
            // This page is way off-screen to the right.
            page.setAlpha( 0 );
        }
    }










}