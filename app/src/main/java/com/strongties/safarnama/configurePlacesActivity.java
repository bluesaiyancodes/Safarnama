package com.strongties.safarnama;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.strongties.safarnama.user_classes.Landmark;
import com.strongties.safarnama.user_classes.LandmarkMeta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class configurePlacesActivity extends AppCompatActivity {


    Context mContext;

    public static ArrayList<String> customSplitSpecific(String s)
    {
        ArrayList<String> words = new ArrayList<String>();
        boolean notInsideComma = true;
        int start =0, end=0;
        for(int i=0; i<s.length()-1; i++)
        {
            if(s.charAt(i)==',' && notInsideComma)
            {
                words.add(s.substring(start,i));
                start = i+1;
            }
            else if(s.charAt(i)=='"')
                notInsideComma=!notInsideComma;
        }
        words.add(s.substring(start));
        return words;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_top);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_configure_places_v2);

        mContext = getApplicationContext();




        Button btn_upload = findViewById(R.id.config_v2_upload);

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Read from Landmarks csv file
                InputStream is = mContext.getResources().openRawResource(R.raw.landmarks_odishav2);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, StandardCharsets.UTF_8));
                String line = "";
                int linecounter = 0;
                DocumentReference docRef;


                try {
                    while ((line = reader.readLine()) != null) {
                        // Split the line into different tokens (using the comma as a separator excluding commas inside quotes).
                        // Log.d("Database: ", line.toString());

                        ArrayList<String> tokens = customSplitSpecific(line);
                        //  Log.d("Database: ", "Custom token 0 -> "+tokens.get(0));


                        // String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
/*
                Log.d("Database: ", "token 0 -> "+tokens.get(0));
                Log.d("Database: ", "token 1 -> "+tokens.get(1));
                Log.d("Database: ", "token 2 -> "+tokens.get(2));
                Log.d("Database: ", "token 3 -> "+tokens.get(3));
                Log.d("Database: ", "token 4 -> "+tokens.get(4));
                Log.d("Database: ", "token 5 -> "+tokens.get(5));
                Log.d("Database: ", "token 6 -> "+tokens.get(6));
                Log.d("Database: ", "token 7 -> "+tokens.get(7));
                Log.d("Database: ", "token 8 -> "+tokens.get(8));
                Log.d("Database: ", "token 9 -> "+tokens.get(9));
                Log.d("Database: ", "token 10 -> "+tokens.get(10));
                Log.d("Database: ", "token 11 -> "+tokens.get(11));
                Log.d("Database: ", "token 12 -> "+tokens.get(12));
                Log.d("Database: ", "token 13 -> "+tokens.get(13));
                Log.d("Database: ", "token 14 -> "+tokens.get(14));

 */


                        //count lines
                        linecounter++;
                        //exclude the first line as it contains headers
                        if(linecounter == 1){
                            continue;
                        }
                        //Insert Data
                        Log.d("CloudEntry", "name -> " + tokens.get(0));
                        Log.d("CloudEntry", "geopoint -> lat - " + tokens.get(5) + "& lon - "+ tokens.get(6));
                        GeoPoint geoPoint = new GeoPoint(Double.parseDouble(tokens.get(5)), Double.parseDouble(tokens.get(6)));

                        Landmark landmark = new Landmark();
                        landmark.setName(tokens.get(0));
                        landmark.setId(tokens.get(1));
                        landmark.setState(tokens.get(2));
                        landmark.setDistrict(tokens.get(3));
                        landmark.setCity(tokens.get(4));
                        landmark.setGeo_point(geoPoint);
                        landmark.setCategory(tokens.get(7));
                        landmark.setFee(tokens.get(8));
                        landmark.setHours(tokens.get(9));
                        landmark.setShort_desc(tokens.get(10));
                        landmark.setLong_desc(tokens.get(11));
                        landmark.setHistory(tokens.get(12));
                        landmark.setImg_url(tokens.get(13));
                        landmark.setImg_all_url(tokens.get(14));

                        docRef = FirebaseFirestore.getInstance()
                                .collection(getString(R.string.collection_landmarks))
                                .document(landmark.getState())
                                .collection(landmark.getCity())
                                .document(landmark.getId());

                        docRef.set(landmark).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    // Toast.makeText(mContext, "Landmark Inserted Successfully", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    //Toast.makeText(mContext, "Encountered Errors. Try Later.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        LandmarkMeta landmarkMeta = new LandmarkMeta();
                        landmarkMeta.setId(landmark.getId());
                        landmarkMeta.setCity(landmark.getCity());
                        landmarkMeta.setState(landmark.getState());
                        landmarkMeta.setGeoPoint(landmark.getGeo_point());
                        landmarkMeta.setLandmark(landmark);
                        docRef = FirebaseFirestore.getInstance()
                                .collection(getString(R.string.collection_landmarks))
                                .document(getString(R.string.document_meta))
                                .collection(getString(R.string.collection_all))
                                .document(landmark.getId());

                        docRef.set(landmarkMeta).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    //  Toast.makeText(mContext, "LandmarkMeta Inserted Successfully", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    // Toast.makeText(mContext, "Encountered Errors. Try Later.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        Log.d("DatabaseHelper" ,"CSV read " );
                    }
                } catch (IOException e1) {
                    Log.e("DatabaseHelper", "Error" + line, e1);
                    e1.printStackTrace();
                }

                Toast.makeText(mContext, linecounter-1 + " Landmarks Inserted.", Toast.LENGTH_SHORT).show();

            }
        });


/*
        //Configure_places_v1
        Button submit = findViewById(R.id.config_places_submit);
        EditText name = findViewById(R.id.config_places_name);
        EditText id = findViewById(R.id.config_places_id);
        EditText state = findViewById(R.id.config_places_state);
        EditText district = findViewById(R.id.config_places_district);
        EditText city = findViewById(R.id.config_places_city);
        EditText lat = findViewById(R.id.config_places_lat);
        EditText lon = findViewById(R.id.config_places_lon);
        EditText type = findViewById(R.id.config_places_type);
        EditText fee = findViewById(R.id.config_places_fee);
        EditText hours = findViewById(R.id.config_places_time);
        EditText short_desc = findViewById(R.id.config_places_short_desc);
        EditText long_desc = findViewById(R.id.config_places_long_desc);
        EditText history = findViewById(R.id.config_places_history);
        EditText img_url = findViewById(R.id.config_places_img);
        EditText img_all_url = findViewById(R.id.config_places_img_all);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                GeoPoint geoPoint = new GeoPoint(Double.parseDouble(lat.getText().toString()), Double.parseDouble(lon.getText().toString()));


                Landmark landmark = new Landmark();
                landmark.setName(name.getText().toString());
                landmark.setId(id.getText().toString());
                landmark.setState(state.getText().toString());
                landmark.setDistrict(district.getText().toString());
                landmark.setCity(city.getText().toString());
                landmark.setGeo_point(geoPoint);
                landmark.setCategory(type.getText().toString());
                landmark.setFee(fee.getText().toString());
                landmark.setHours(hours.getText().toString());
                landmark.setShort_desc(short_desc.getText().toString());
                landmark.setLong_desc(long_desc.getText().toString());
                landmark.setHistory(history.getText().toString());
                landmark.setImg_url(img_url.getText().toString());
                landmark.setImg_all_url(img_all_url.getText().toString());


                DocumentReference docRef = FirebaseFirestore.getInstance()
                        .collection(getString(R.string.collection_landmarks))
                        .document(landmark.getState())
                        .collection(landmark.getCity())
                        .document(landmark.getId());

                docRef.set(landmark).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(mContext, "Landmark Inserted Successfully", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(mContext, "Encountered Errors. Try Later.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

 */




    }


}
