package com.strongties.safarnama;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class fragment_menu_explore extends Fragment implements AdapterView.OnItemSelectedListener {

    Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_explore, container, false);


        ArrayList<String> planets_array = new ArrayList<>();
        planets_array.add("Mercury");
        planets_array.add("Venus");
        planets_array.add("Earth");
        planets_array.add("Mars");
        planets_array.add("Jupiter");
        planets_array.add("Saturn");
        planets_array.add("Uranus");
        planets_array.add("Neptune");
        planets_array.add("Mercury1");
        planets_array.add("Venus1");
        planets_array.add("Earth1");
        planets_array.add("Mars1");
        planets_array.add("Jupiter1");
        planets_array.add("Saturn1");
        planets_array.add("Uranus1");
        planets_array.add("Neptune1");
        planets_array.add("Mercury2");
        planets_array.add("Venus2");
        planets_array.add("Earth2");
        planets_array.add("Mars2");
        planets_array.add("Jupiter2");
        planets_array.add("Saturn2");
        planets_array.add("Uranus2");
        planets_array.add("Neptune2");


        Spinner spinner = root.findViewById(R.id.explore_state_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, planets_array);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setSelection(adapter.getPosition("Jupiter"));

        spinner.setOnItemSelectedListener(this);

        return root;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
