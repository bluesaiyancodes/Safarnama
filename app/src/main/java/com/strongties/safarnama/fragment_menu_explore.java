package com.strongties.safarnama;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.strongties.safarnama.adapters.RecyclerViewAdapter_explore;
import com.strongties.safarnama.user_classes.LandmarkMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static android.content.Context.MODE_PRIVATE;

public class fragment_menu_explore extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "Menu Explore Fragment";

    Context mContext;
    SQLiteDatabase database;

    Spinner statespinner;
    Spinner districtspinner;

    String local;

    ArrayAdapter<CharSequence> districtadapter;
    ArrayList<String> districts_array;

    ChipGroup chipGroup;

    private RecyclerView myrecyclerview;
    private RecyclerViewAdapter_explore adapter_explore = null;

    private ArrayList<String> selectedChips;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();

        //Maintain Chip checked List
        selectedChips = new ArrayList<>();

        // Get local state information from shared preferences
        SharedPreferences pref = mContext.getSharedPreferences("myPrefs", MODE_PRIVATE);
        local = pref.getString("localState", "Odisha");

        CollectionReference collRef = FirebaseFirestore.getInstance()
                .collection(getString(R.string.collection_landmarks))
                .document(getString(R.string.document_meta))
                .collection(getString(R.string.collection_all));

        Query query = collRef
                .whereIn("state", Collections.singletonList(local))
                .orderBy("district", Query.Direction.ASCENDING);


        FirestoreRecyclerOptions<LandmarkMeta> option = new FirestoreRecyclerOptions.Builder<LandmarkMeta>()
                .setQuery(query, LandmarkMeta.class)
                .build();
        adapter_explore = new RecyclerViewAdapter_explore(option);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_explore, container, false);

        //set Recycle View
        myrecyclerview = root.findViewById(R.id.explore_menu_recyclerview);
        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        myrecyclerview.setAdapter(adapter_explore);


        ArrayList<String> states_array = new ArrayList<>();
        districts_array = new ArrayList<>();

        //Add default entries
        states_array.add(getString(R.string.all_states));
        districts_array.add(getString(R.string.all_districts));


        DatabaseHelper dbhelper = new DatabaseHelper(getContext());
        database = dbhelper.getReadableDatabase();

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


        //State Spinner
        statespinner = root.findViewById(R.id.explore_state_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> stateadapter = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, states_array);
        // Specify the layout to use when the list of choices appears
        stateadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        statespinner.setAdapter(stateadapter);


        //set selection of spinner to local state
        statespinner.setSelection(stateadapter.getPosition(local));

        statespinner.setOnItemSelectedListener(this);


        //District Data preparation
        cursor = database.rawQuery("SELECT district FROM INDIAINFO where state = ?", new String[]{local});

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            Toast.makeText(getContext(), getString(R.string.error_fetching), Toast.LENGTH_SHORT).show();
        }

        do {
            assert cursor != null;
            districts_array.add(cursor.getString(0));
        } while (cursor.moveToNext());

        //add an item at last
        districts_array.add(getString(R.string.all_districts));
        cursor.close();


        //District Spinner
        districtspinner = root.findViewById(R.id.explore_district_spinner);
        districtadapter = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, districts_array);
        districtadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtspinner.setAdapter(districtadapter);
        districtspinner.setOnItemSelectedListener(this);


        chipGroup = root.findViewById(R.id.explore_chip_group);
        //Initialize scroll View
        ScrollView scrollView = root.findViewById(R.id.explore_main_scroll);


        // Initializing Buttons
        MaterialButton btn_choose_region = root.findViewById(R.id.explore_choose_region);
        MaterialButton btn_filter_category = root.findViewById(R.id.explore_filter_category);


        //Initializing Atomic References
        AtomicReference<Boolean> btn_choose_region_flag = new AtomicReference<>(true);
        AtomicReference<Boolean> btn_filter_category_flag = new AtomicReference<>(false);

        //For Choose Region UI
        LinearLayout layout_choose_region = root.findViewById(R.id.explore_state_district_layout);
        btn_choose_region.setOnClickListener(view -> {
            if (btn_choose_region_flag.get()) {
                layout_choose_region.setVisibility(View.GONE);
                btn_choose_region_flag.set(false);
            } else {
                layout_choose_region.setVisibility(View.VISIBLE);
                if (btn_filter_category_flag.get()) {
                    btn_filter_category.performClick(); //Collapse filter category
                }
                btn_choose_region_flag.set(true);
            }

        });


        //For Filter Category UI
        MaterialButton btn_filter = root.findViewById(R.id.explore_filter_btn);
        btn_filter_category.setOnClickListener(view -> {
            if (btn_filter_category_flag.get()) {
                chipGroup.setVisibility(View.GONE);
                btn_filter.setVisibility(View.GONE);
                btn_filter_category_flag.set(false);
            } else {
                chipGroup.setVisibility(View.VISIBLE);
                if (btn_choose_region_flag.get()) {
                    btn_choose_region.performClick();   //Collapse region chooser
                }
                btn_filter.setVisibility(View.VISIBLE);
                btn_filter_category_flag.set(true);
            }

        });

        //Initialise the category icons
        ImageView iv_cat1 = root.findViewById(R.id.explre_icon_cat1);
        ImageView iv_cat2 = root.findViewById(R.id.explre_icon_cat2);
        ImageView iv_cat3 = root.findViewById(R.id.explre_icon_cat3);
        ImageView iv_cat4 = root.findViewById(R.id.explre_icon_cat4);
        ImageView iv_cat5 = root.findViewById(R.id.explre_icon_cat5);
        ImageView iv_cat6 = root.findViewById(R.id.explre_icon_cat6);
        ImageView iv_cat7 = root.findViewById(R.id.explre_icon_cat7);
        ImageView iv_cat8 = root.findViewById(R.id.explre_icon_cat8);
        ImageView iv_cat9 = root.findViewById(R.id.explre_icon_cat9);
        ImageView iv_cat10 = root.findViewById(R.id.explre_icon_cat10);


        //Initialise the chips
        Chip chip_cat1 = root.findViewById(R.id.explore_chip_cat1); //Dams and Reservoirs
        Chip chip_cat2 = root.findViewById(R.id.explore_chip_cat2); //Education and History
        Chip chip_cat3 = root.findViewById(R.id.explore_chip_cat3); //Garden and Parks
        Chip chip_cat4 = root.findViewById(R.id.explore_chip_cat4); //Hills and Caves
        Chip chip_cat5 = root.findViewById(R.id.explore_chip_cat5); //Iconic Places
        Chip chip_cat6 = root.findViewById(R.id.explore_chip_cat6); //Nature and WildLife
        Chip chip_cat7 = root.findViewById(R.id.explore_chip_cat7); //Port and Seabeach
        Chip chip_cat8 = root.findViewById(R.id.explore_chip_cat8); //Religious Sites
        Chip chip_cat9 = root.findViewById(R.id.explore_chip_cat9); //Waterbodies
        Chip chip_cat10 = root.findViewById(R.id.explore_chip_cat10);   //Zoos and Reserves


        //Set btn click ui effects
        chip_cat1.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                iv_cat1.setVisibility(View.VISIBLE);
                selectedChips.add(getString(R.string.category1));
            } else {
                iv_cat1.setVisibility(View.GONE);
                selectedChips.remove(getString(R.string.category1));
            }
        });

        chip_cat2.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                iv_cat2.setVisibility(View.VISIBLE);
                selectedChips.add(getString(R.string.category2));
            } else {
                iv_cat2.setVisibility(View.GONE);
                selectedChips.remove(getString(R.string.category2));
            }
        });

        chip_cat3.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                iv_cat3.setVisibility(View.VISIBLE);
                selectedChips.add(getString(R.string.category3));
            } else {
                iv_cat3.setVisibility(View.GONE);
                selectedChips.remove(getString(R.string.category3));
            }
        });

        chip_cat4.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                iv_cat4.setVisibility(View.VISIBLE);
                selectedChips.add(getString(R.string.category4));
            } else {
                iv_cat4.setVisibility(View.GONE);
                selectedChips.remove(getString(R.string.category4));
            }
        });

        chip_cat5.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                iv_cat5.setVisibility(View.VISIBLE);
                selectedChips.add(getString(R.string.category5));
            } else {
                iv_cat5.setVisibility(View.GONE);
                selectedChips.remove(getString(R.string.category5));
            }
        });

        chip_cat6.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                iv_cat6.setVisibility(View.VISIBLE);
                selectedChips.add(getString(R.string.category6));
            } else {
                iv_cat6.setVisibility(View.GONE);
                selectedChips.remove(getString(R.string.category6));
            }
        });

        chip_cat7.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                iv_cat7.setVisibility(View.VISIBLE);
                selectedChips.add(getString(R.string.category7));
            } else {
                iv_cat7.setVisibility(View.GONE);
                selectedChips.remove(getString(R.string.category7));
            }
        });

        chip_cat8.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                iv_cat8.setVisibility(View.VISIBLE);
                selectedChips.add(getString(R.string.category8));
            } else {
                iv_cat8.setVisibility(View.GONE);
                selectedChips.remove(getString(R.string.category8));
            }
        });

        chip_cat9.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                iv_cat9.setVisibility(View.VISIBLE);
                selectedChips.add(getString(R.string.category9));
            } else {
                iv_cat9.setVisibility(View.GONE);
                selectedChips.remove(getString(R.string.category9));
            }
        });

        chip_cat10.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                iv_cat10.setVisibility(View.VISIBLE);
                selectedChips.add(getString(R.string.category10));
            } else {
                iv_cat10.setVisibility(View.GONE);
                selectedChips.remove(getString(R.string.category10));
            }
        });


        //Filter Button Click
        btn_filter.setOnClickListener(view -> {

            //UI changes
            if (btn_filter_category_flag.get()) {
                btn_filter_category.performClick();
            }
            if (btn_choose_region_flag.get()) {
                btn_choose_region.performClick();
            }

            Log.d(TAG, "Selected Cats -> " + selectedChips.toString());

            //if no chip selection made
            if (selectedChips.size() == 0) {
                new AlertDialog.Builder(mContext)
                        .setTitle(getString(R.string.unsupported))
                        .setMessage(getString(R.string.selectcategory_msg))
                        .setPositiveButton(getString(R.string.okay), (dialogInterface, i) -> {
                            //dismis alert dialog
                            dialogInterface.dismiss();
                        })
                        .setIcon(R.drawable.app_main_icon)
                        .show();
                return;
            }

            //Limit functionality to only selected state and not all states
            if (statespinner.getSelectedItem().toString().equals(getString(R.string.all_states))) {
                new AlertDialog.Builder(mContext)
                        .setTitle(getString(R.string.unsupported))
                        .setMessage(getString(R.string.selectstate_msg))
                        .setPositiveButton(getString(R.string.okay), (dialogInterface, i) -> {
                            //clear Selection
                            selectedChips.clear();
                            iv_cat1.setVisibility(View.GONE);
                            iv_cat2.setVisibility(View.GONE);
                            iv_cat3.setVisibility(View.GONE);
                            iv_cat4.setVisibility(View.GONE);
                            iv_cat5.setVisibility(View.GONE);
                            iv_cat6.setVisibility(View.GONE);
                            iv_cat7.setVisibility(View.GONE);
                            iv_cat8.setVisibility(View.GONE);
                            iv_cat9.setVisibility(View.GONE);
                            iv_cat10.setVisibility(View.GONE);

                            chip_cat1.setChecked(false);
                            chip_cat2.setChecked(false);
                            chip_cat3.setChecked(false);
                            chip_cat4.setChecked(false);
                            chip_cat5.setChecked(false);
                            chip_cat6.setChecked(false);
                            chip_cat7.setChecked(false);
                            chip_cat8.setChecked(false);
                            chip_cat9.setChecked(false);
                            chip_cat10.setChecked(false);


                            //open region selection
                            if (!btn_choose_region_flag.get()) {
                                btn_choose_region.performClick();
                            }

                            //dismis alert dialog
                            dialogInterface.dismiss();
                        })
                        .setIcon(R.drawable.app_main_icon)
                        .show();
                return;
            }


            //If all districts selected
            if (districtspinner.getSelectedItem().toString().equals(getString(R.string.all_districts))) {
                CollectionReference collRef = FirebaseFirestore.getInstance()
                        .collection(getString(R.string.collection_landmarks))
                        .document(statespinner.getSelectedItem().toString())
                        .collection(getString(R.string.document_meta));

                Query query = collRef
                        .whereIn("category", selectedChips)
                        .orderBy("district", Query.Direction.ASCENDING);

                FirestoreRecyclerOptions<LandmarkMeta> new_option = new FirestoreRecyclerOptions.Builder<LandmarkMeta>()
                        .setQuery(query, LandmarkMeta.class)
                        .build();

                adapter_explore.updateOptions(new_option);
                adapter_explore.notifyDataSetChanged();
            } else {


                CollectionReference collRef = FirebaseFirestore.getInstance()
                        .collection(getString(R.string.collection_landmarks))
                        .document(statespinner.getSelectedItem().toString())
                        .collection(districtspinner.getSelectedItem().toString());

                Query query = collRef
                        .whereIn("category", selectedChips)
                        .orderBy("district", Query.Direction.ASCENDING);

                FirestoreRecyclerOptions<LandmarkMeta> new_option = new FirestoreRecyclerOptions.Builder<LandmarkMeta>()
                        .setQuery(query, LandmarkMeta.class)
                        .build();

                adapter_explore.updateOptions(new_option);
                adapter_explore.notifyDataSetChanged();
            }

            //Scroll the window
            // scrollView.smoothScrollTo(0, scrollView.getBottom());


        });


        return root;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (adapter_explore != null) {
            adapter_explore.startListening();
        }


    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter_explore != null) {
            adapter_explore.stopListening();
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == R.id.explore_state_spinner) {
            Cursor cursor;
            //Clear chip group
            chipGroup.clearCheck();
            //Clear Arraylist
            districts_array.clear();
            //Add default entry
            districts_array.add(getString(R.string.all_districts));

            //Check for default selected item
            //if not default item
            if (!adapterView.getAdapter().getItem(i).toString().equals(getString(R.string.all_states))) {

                //District Data preparation
                cursor = database.rawQuery("SELECT district FROM INDIAINFO where state = ? ORDER BY district ASC;", new String[]{adapterView.getAdapter().getItem(i).toString()});


                Log.d(TAG, "Spinner -> " + adapterView.getAdapter().getItem(i).toString());

                if (cursor != null) {
                    cursor.moveToFirst();
                } else {
                    Toast.makeText(getContext(), getString(R.string.error_fetching), Toast.LENGTH_SHORT).show();
                }

                do {
                    assert cursor != null;
                    districts_array.add(cursor.getString(0));
                } while (cursor.moveToNext());
                districts_array.add(getString(R.string.all_districts));
                cursor.close();
            } else {
                //if default item

                //District Data preparation
                cursor = database.rawQuery("SELECT district FROM INDIAINFO ORDER BY district ASC", new String[]{});

                if (cursor != null) {
                    cursor.moveToFirst();
                } else {
                    Toast.makeText(getContext(), getString(R.string.error_fetching), Toast.LENGTH_SHORT).show();
                }

                do {
                    assert cursor != null;
                    districts_array.add(cursor.getString(0));
                } while (cursor.moveToNext());
                districts_array.add(getString(R.string.all_districts));
                cursor.close();
            }

            //notify adapter as data is changed
            districtadapter.notifyDataSetChanged();
            districtspinner.setSelection(1000);


        } else if (adapterView.getId() == R.id.explore_district_spinner) {
            if (i == districts_array.size() - 1) {
                //set selection -> calls this function again but does not enter this block
                districtspinner.setSelection(0);
                Log.d(TAG, "District modified -> " + 0);

            } else {
                //if all states selected
                if (statespinner.getSelectedItem().toString().equals(getString(R.string.all_states))) {
                    //if all district selected
                    if (districtspinner.getSelectedItem().toString().equals(getString(R.string.all_districts))) {
                        CollectionReference collRef = FirebaseFirestore.getInstance()
                                .collection(getString(R.string.collection_landmarks))
                                .document(getString(R.string.document_meta))
                                .collection(getString(R.string.collection_all));

                        Query query = collRef
                                .orderBy("district", Query.Direction.ASCENDING);

                        FirestoreRecyclerOptions<LandmarkMeta> new_option = new FirestoreRecyclerOptions.Builder<LandmarkMeta>()
                                .setQuery(query, LandmarkMeta.class)
                                .build();

                        adapter_explore.updateOptions(new_option);
                    } else {
                        CollectionReference collRef = FirebaseFirestore.getInstance()
                                .collection(getString(R.string.collection_landmarks))
                                .document(getString(R.string.document_meta))
                                .collection(getString(R.string.collection_all));

                        Query query = collRef
                                .whereIn("district", Collections.singletonList(districtspinner.getSelectedItem().toString()))
                                .orderBy("district", Query.Direction.ASCENDING);

                        FirestoreRecyclerOptions<LandmarkMeta> new_option = new FirestoreRecyclerOptions.Builder<LandmarkMeta>()
                                .setQuery(query, LandmarkMeta.class)
                                .build();

                        adapter_explore.updateOptions(new_option);
                        adapter_explore.notifyDataSetChanged();
                    }

                } else {

                    //if all districts selected
                    if (districtspinner.getSelectedItem().toString().equals(getString(R.string.all_districts))) {
                        CollectionReference collRef = FirebaseFirestore.getInstance()
                                .collection(getString(R.string.collection_landmarks))
                                .document(getString(R.string.document_meta))
                                .collection(getString(R.string.collection_all));

                        Query query = collRef
                                .whereIn("state", Collections.singletonList(statespinner.getSelectedItem().toString()))
                                .orderBy("district", Query.Direction.ASCENDING);

                        FirestoreRecyclerOptions<LandmarkMeta> new_option = new FirestoreRecyclerOptions.Builder<LandmarkMeta>()
                                .setQuery(query, LandmarkMeta.class)
                                .build();

                        adapter_explore.updateOptions(new_option);
                    } else {
                        CollectionReference collRef = FirebaseFirestore.getInstance()
                                .collection(getString(R.string.collection_landmarks))
                                .document(getString(R.string.document_meta))
                                .collection(getString(R.string.collection_all));

                        Query query = collRef
                                .whereIn("district", Collections.singletonList(districtspinner.getSelectedItem().toString()))
                                .orderBy("district", Query.Direction.ASCENDING);

                        FirestoreRecyclerOptions<LandmarkMeta> new_option = new FirestoreRecyclerOptions.Builder<LandmarkMeta>()
                                .setQuery(query, LandmarkMeta.class)
                                .build();

                        adapter_explore.updateOptions(new_option);
                    }
                    adapter_explore.notifyDataSetChanged();
                }

            }

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
