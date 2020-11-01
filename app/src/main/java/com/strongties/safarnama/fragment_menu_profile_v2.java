package com.strongties.safarnama;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.strongties.safarnama.user_classes.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.strongties.safarnama.MainActivity.accomplish_type_count;
import static com.strongties.safarnama.MainActivity.accomplished_list;
import static com.strongties.safarnama.MainActivity.bucket_type_count;

public class fragment_menu_profile_v2 extends Fragment {

    private static final String TAG = "Buddy Profile Fragment";

    Context mContext;

    User currentuser;


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
        TextView tv_buddycount = root.findViewById(R.id.profile_v2_buddy_count);

        //set Dp
        Glide.with(mContext)
                .load(currentuser.getPhoto())
                .placeholder(R.drawable.loading_image)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                .into(iv_dp);

        //set Username
        tv_name.setText(currentuser.getUsername());
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

}
