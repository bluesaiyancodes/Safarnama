package com.strongties.safarnama;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DBname = "wander";
    private static int version = 11;
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DBname, null, version);

        this.context = context.getApplicationContext();
    }

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
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS LANDMARKS");
        onCreate(db);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //Create Table
        String sql = "CREATE TABLE LANDMARKS (id INTEGER PRIMARY KEY AUTOINCREMENT, place_id TEXT, name TEXT, lat REAL, lon REAL, state TEXT, city TEXT, type TEXT, url TEXT)";
        db.execSQL(sql);

        //Read from Landmarks csv file
        InputStream is = context.getResources().openRawResource(R.raw.landmarks_odisha);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8));
        String line = "";
        int linecounter = 0;

        try {

            while ((line = reader.readLine()) != null) {
                // Split the line into different tokens (using the comma as a separator excluding commas inside quotes).
                ArrayList<String> tokens = customSplitSpecific(line);
                //count lines
                linecounter++;
                //exclude the first line as it contains headers
                if(linecounter == 1){
                    continue;
                }
                insertdata(tokens.get(0), tokens.get(1), tokens.get(2), tokens.get(4), Double.parseDouble(tokens.get(5)), Double.parseDouble(tokens.get(6)),tokens.get(7), tokens.get(13), db);

                Log.d("DatabaseHelper" ,"CSV read " );
            }
        } catch (IOException e1) {
            Log.e("DatabaseHelper", "Error" + line, e1);
            e1.printStackTrace();
        }



    }

    private void insertdata(String name, String place_id, String state, String city, double lat, double lon, String type, String url, SQLiteDatabase database){
        ContentValues values = new ContentValues();
        values.put("place_id", place_id);
        values.put("name", name);
        values.put("state", state);
        values.put("city", city);
        values.put("lat", lat);
        values.put("lon", lon);
        values.put("type", type);
        values.put("url", url);

        database.insert("LANDMARKS", null, values);

    }
}
