package com.strongties.safarnama;

import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.strongties.safarnama.adapters.RecyclerViewAdapter_Journey_location;
import com.strongties.safarnama.user_classes.RV_Journey_Location;
import com.strongties.safarnama.user_classes.RV_Journey_Tour;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class JourneyActivity extends AppCompatActivity {


    RecyclerViewAdapter_Journey_location journey_loc_adapter;
    private RecyclerView journey_recycler_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);

        String journey_id = Objects.requireNonNull(getIntent().getExtras()).getString("id");
        int sl_number = Objects.requireNonNull(getIntent().getExtras()).getInt("number", 0);

        RelativeLayout header = findViewById(R.id.act_journey_header_RL);
        RelativeLayout footer = findViewById(R.id.act_journey_footer_RL);

        ArrayList<Integer> header_img_urls = new ArrayList<>();
        header_img_urls.add(R.drawable.t_card_1);
        header_img_urls.add(R.drawable.t_card_2);
        header_img_urls.add(R.drawable.t_card_3);
        header_img_urls.add(R.drawable.t_card_4);

        ArrayList<Integer> footer_img_urls = new ArrayList<>();
        footer_img_urls.add(R.drawable.r1);
        footer_img_urls.add(R.drawable.r2);
        footer_img_urls.add(R.drawable.r3);
        footer_img_urls.add(R.drawable.r4);

        header.setBackgroundResource(header_img_urls.get(sl_number));
        footer.setBackgroundResource(footer_img_urls.get(sl_number));

        CircleImageView btn_back = findViewById(R.id.act_journey_back);
        TextView title = findViewById(R.id.act_journey_title);
        TextView desc = findViewById(R.id.act_journey_desc);
        TextView duration = findViewById(R.id.act_journey_duration);


        DocumentReference docref = FirebaseFirestore.getInstance()
                .collection(getString(R.string.collection_journey))
                .document(getString(R.string.document_states))
                .collection("Odisha")
                .document(journey_id);

        docref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    RV_Journey_Tour tour = task.getResult().toObject(RV_Journey_Tour.class);
                    title.setText(tour.getName());
                    desc.setText(tour.getDescription());
                    duration.setText(tour.getDuration());
                }
            }
        });

        btn_back.setOnClickListener(view -> {
            onBackPressed();
        });


        CollectionReference locations = FirebaseFirestore.getInstance()
                .collection(getString(R.string.collection_journey))
                .document(getString(R.string.document_states))
                .collection("Odisha")
                .document(journey_id)
                .collection(getString(R.string.collection_locations));

        Query query = locations
                .orderBy("place_pos", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<RV_Journey_Location> option = new FirestoreRecyclerOptions.Builder<RV_Journey_Location>()
                .setQuery(query, RV_Journey_Location.class)
                .build();

        journey_loc_adapter = new RecyclerViewAdapter_Journey_location(option);

        journey_recycler_view = findViewById(R.id.act_journey_recycler_view);
        journey_recycler_view.setLayoutManager(new LinearLayoutManager(this));
        journey_recycler_view.setAdapter(journey_loc_adapter);


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_top, R.anim.exit_to_bottom);
    }

    @Override
    public void onStart() {
        super.onStart();
        journey_loc_adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        journey_loc_adapter.stopListening();
    }
}