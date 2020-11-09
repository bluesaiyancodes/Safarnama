package com.strongties.safarnama;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.strongties.safarnama.adapters.RecyclerViewAdapter_Journey_tour;
import com.strongties.safarnama.user_classes.RV_Journey_Tour;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class fragment_menu_journey extends Fragment implements AdapterView.OnItemSelectedListener {

    Context mContext;
    View v;

    Spinner statespinner;
    String local;
    RecyclerViewAdapter_Journey_tour journey_tour_adapter;
    private RecyclerView journey_recycler_view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();

        // Get local state information from shared preferences
        SharedPreferences pref = mContext.getSharedPreferences("myPrefs", MODE_PRIVATE);
        local = pref.getString("localState", "Odisha");

        CollectionReference journeys = FirebaseFirestore.getInstance()
                .collection(getString(R.string.collection_journey))
                .document(getString(R.string.document_states))
                .collection(local);

        FirestoreRecyclerOptions<RV_Journey_Tour> option = new FirestoreRecyclerOptions.Builder<RV_Journey_Tour>()
                .setQuery(journeys, RV_Journey_Tour.class)
                .build();

        journey_tour_adapter = new RecyclerViewAdapter_Journey_tour(option);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_menu_journey, container, false);


        journey_recycler_view = v.findViewById(R.id.journey_recycler_view);
        journey_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        journey_recycler_view.setAdapter(journey_tour_adapter);


        // State Spinner
        {
            ArrayList<String> states_array = new ArrayList<>();

            DatabaseHelper dbhelper = new DatabaseHelper(getContext());
            SQLiteDatabase database = dbhelper.getReadableDatabase();
            //fetch data from db
            {
                Cursor cursor;

                //State data preparation
                cursor = database.rawQuery("SELECT DISTINCT state FROM INDIAINFO ", new String[]{});

                if (cursor != null) {
                    cursor.moveToFirst();
                } else {
                    Toast.makeText(getContext(), getString(R.string.error_fetching), Toast.LENGTH_SHORT).show();
                }

                do {
                    assert cursor != null;
                    states_array.add(cursor.getString(0));
                } while (cursor.moveToNext());

                cursor.close();
            }
            //State Spinner
            statespinner = v.findViewById(R.id.journey_state_spinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> stateadapter = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, states_array);
            // Specify the layout to use when the list of choices appears
            stateadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            statespinner.setAdapter(stateadapter);


            //set selection of spinner to local state
            statespinner.setSelection(stateadapter.getPosition(local));

            statespinner.setOnItemSelectedListener(this);

        }

        return v;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        CollectionReference colRef = FirebaseFirestore.getInstance()
                .collection(mContext.getString(R.string.collection_journey))
                .document(mContext.getString(R.string.document_states))
                .collection(statespinner.getSelectedItem().toString());

        FirestoreRecyclerOptions<RV_Journey_Tour> new_option = new FirestoreRecyclerOptions.Builder<RV_Journey_Tour>()
                .setQuery(colRef, RV_Journey_Tour.class)
                .build();

        journey_tour_adapter.updateOptions(new_option);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onStart() {
        super.onStart();
        journey_tour_adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        journey_tour_adapter.stopListening();
    }

}