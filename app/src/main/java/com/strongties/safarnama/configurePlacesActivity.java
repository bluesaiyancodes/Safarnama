package com.strongties.safarnama;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class configurePlacesActivity extends AppCompatActivity {


    FirebaseFirestore mDb;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_places);

        mContext = getApplicationContext();

        Button submit = findViewById(R.id.config_places_submit);
        EditText name = findViewById(R.id.config_places_name);
        EditText state = findViewById(R.id.config_places_state);
        EditText district = findViewById(R.id.config_places_district);
        EditText city = findViewById(R.id.config_places_city);
        EditText lat = findViewById(R.id.config_places_lat);
        EditText lon = findViewById(R.id.config_places_lon);
        EditText type = findViewById(R.id.config_places_type);
        EditText short_desc = findViewById(R.id.config_places_short_desc);
        EditText long_desc = findViewById(R.id.config_places_long_desc);
        EditText history = findViewById(R.id.config_places_history);
        EditText img_url = findViewById(R.id.config_places_img);
        EditText img_all_url = findViewById(R.id.config_places_img_all);


        mDb = FirebaseFirestore.getInstance();




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_top);
    }


}
