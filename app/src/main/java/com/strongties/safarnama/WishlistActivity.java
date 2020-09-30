package com.strongties.safarnama;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.strongties.safarnama.adapters.PagerAdapterWishlist;

public class WishlistActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapterWishlist adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        tabLayout = findViewById(R.id.tablayout_id);
        viewPager = findViewById(R.id.viewpager_id);
        adapter = new PagerAdapterWishlist(getSupportFragmentManager());

        //Fragments Added Here
        adapter.AddFragment(new RV_ExploreFragment(),getString(R.string.Explore));
        adapter.AddFragment(new RV_BucketlistFragment(),getString(R.string.BucketList));
        adapter.AddFragment(new RV_AccomplishedFragment(),getString(R.string.Accomplished));

        viewPager.setPageTransformer(true, new FadePageTransformer());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        //viewPager.setOffscreenPageLimit(0);

        //Set Icon For tabs
        //tabLayout.getTabAt(0).setIcon(R.drawable.);



        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //Objects.requireNonNull(viewPager.getAdapter()).notifyDataSetChanged();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });





    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_top);
    }




}
