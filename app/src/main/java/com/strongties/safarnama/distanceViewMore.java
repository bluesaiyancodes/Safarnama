package com.strongties.safarnama;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.strongties.safarnama.adapters.RecyclerViewAdaptor_distance_search;
import com.strongties.safarnama.user_classes.RV_Distance;
import com.strongties.safarnama.user_classes.RV_DistanceSearch;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class distanceViewMore extends AppCompatActivity {
    private static final String TAG = "ViewMore Activity";
    RecyclerViewAdaptor_distance_search recyclerAdapter;
    private List<RV_Distance> list_Distance;
    private List<RV_DistanceSearch> list_DistanceSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_view_more);

        TextView tv_min = findViewById(R.id.distanceViewmore_loc1);
        TextView tv_max = findViewById(R.id.distanceViewmore_loc2);

        tv_min.setText(Objects.requireNonNull(getIntent().getExtras()).getString("min"));
        tv_max.setText(Objects.requireNonNull(getIntent().getExtras()).getString("max"));

        list_Distance = Objects.requireNonNull(getIntent().getExtras()).getParcelableArrayList("list");
        list_DistanceSearch = new ArrayList<>();

        String view_more_type = list_Distance.get(0).getMeta();
        Objects.requireNonNull(this.getSupportActionBar()).setTitle(view_more_type);

        CircleImageView back = findViewById(R.id.distanceViewmore_go_back);
        back.setOnClickListener(view -> onBackPressed());


        RecyclerView myrecyclerview = findViewById(R.id.distanceViewmore_recyclerview);
        myrecyclerview.setHasFixedSize(true);

        myrecyclerview.setLayoutManager(new LinearLayoutManager(this));
        //myrecyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //for Double Formatting
        DecimalFormat df = new DecimalFormat("0.00");

        for (RV_Distance distance : list_Distance) {
            String dist_text = df.format(distance.getDistance() / 1000.0);
            dist_text += "KM (Approx.)";

            list_DistanceSearch.add(new RV_DistanceSearch(distance.getId(), distance.getName(), distance.getDistrict(), distance.getCity(), distance.getCategory(), distance.getImg_url(), dist_text, distance.getDistance()));
        }

        // Sort the List except hot places
        if (!view_more_type.equals(getString(R.string.hot_places)))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                list_DistanceSearch.sort(Comparator.comparingDouble(RV_DistanceSearch::getDistance));
            }

        recyclerAdapter = new RecyclerViewAdaptor_distance_search(this, list_DistanceSearch);
        myrecyclerview.setAdapter(recyclerAdapter);
        // recyclerAdapter.notifyDataSetChanged();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_top);
    }
}