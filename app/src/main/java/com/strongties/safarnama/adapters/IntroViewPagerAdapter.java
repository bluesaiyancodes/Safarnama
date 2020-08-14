package com.strongties.safarnama.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.strongties.safarnama.R;
import com.strongties.safarnama.user_classes.ScreenItem;

import java.util.List;

public class IntroViewPagerAdapter extends PagerAdapter {

    Context mContext ;
    List<ScreenItem> mListScreen;

    public IntroViewPagerAdapter(Context mContext, List<ScreenItem> mListScreen) {
        this.mContext = mContext;
        this.mListScreen = mListScreen;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen = inflater.inflate(R.layout.layout_screen, null);

        ImageView imgSlide = layoutScreen.findViewById(R.id.intro_img);
        TextView title = layoutScreen.findViewById(R.id.intro_title);
        TextView description = layoutScreen.findViewById(R.id.intro_description);

        if (position == 0) {
            title.setText(mListScreen.get(position).getTitle());
            description.setText(mListScreen.get(position).getDescription());
            Glide.with(mContext).load(R.drawable.namaskara)
                    .transform(new FitCenter(), new RoundedCorners(30))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgSlide);
        } else if (position == 1) {
            title.setText(mListScreen.get(position).getTitle());
            description.setText(mListScreen.get(position).getDescription());
            Glide.with(mContext).asGif().load(R.raw.map_marker)
                    .transform(new FitCenter(), new RoundedCorners(30))
                    // .fitCenter()
                    // .apply(RequestOptions.bitmapTransform(new RoundedCorners(25)))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgSlide);


          /*  GifDrawable gifDrawable = null;
            try {
                gifDrawable = new GifDrawable(mContext.getResources(), R.raw.map_marker);
              //  gifDrawable.setLoopCount(1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imgSlide.setImageDrawable(gifDrawable);
           */


        } else if (position == 2) {
            title.setText(mListScreen.get(position).getTitle());
            description.setText(mListScreen.get(position).getDescription());
            Glide.with(mContext).asGif().load(R.raw.list_intro)
                    .transform(new FitCenter(), new RoundedCorners(30))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgSlide);
        } else if (position == 3) {
            title.setText(mListScreen.get(position).getTitle());
            description.setText(mListScreen.get(position).getDescription());
            Glide.with(mContext).asGif().load(R.raw.map_filters)
                    .transform(new FitCenter(), new RoundedCorners(30))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgSlide);
        } else if (position == 4) {
            title.setText(mListScreen.get(position).getTitle());
            description.setText(mListScreen.get(position).getDescription());
            Glide.with(mContext).asGif().load(R.raw.buddies)
                    .transform(new FitCenter(), new RoundedCorners(30))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgSlide);
        } else if (position == 5) {
            title.setText(mListScreen.get(position).getTitle());
            description.setText(mListScreen.get(position).getDescription());
            Glide.with(mContext).asGif().load(R.raw.badges)
                    .transform(new FitCenter(), new RoundedCorners(30))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgSlide);
        } else {
            title.setText(mListScreen.get(position).getTitle());
            description.setText(mListScreen.get(position).getDescription());
            imgSlide.setImageResource(mListScreen.get(position).getScreenImg());
        }


        container.addView(layoutScreen);

        return layoutScreen;





    }

    @Override
    public int getCount() {
        return mListScreen.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View)object);

    }
}
