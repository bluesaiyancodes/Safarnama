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


public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DBname = "wander";
    private static int version = 5;
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DBname, null, version);

        this.context = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //Create Table
        String sql = "CREATE TABLE LANDMARKS (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, city TEXT, district TEXT, state TEXT, lat REAL, lon REAL, type TEXT, descshort TEXT, description TEXT, history TEXT, url TEXT, imgurls TEXT, visit TEXT, date TEXT)";
        db.execSQL(sql);

        //Read from Landmarks csv file
        InputStream is = context.getResources().openRawResource(R.raw.landmarksv2);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8));
        String line = "";
        int linecounter = 0;

        try {
            while ((line = reader.readLine()) != null) {
                // Split the line into different tokens (using the comma as a separator excluding commas inside quotes).
                String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                //count lines
                linecounter++;
                //exclude the first line as it contains headers
                if(linecounter == 1){
                    continue;
                }
                insertdata(tokens[0], tokens[1], tokens[2], tokens[3], Double.parseDouble(tokens[4]), Double.parseDouble(tokens[5]),tokens[6], tokens[7], tokens[8], tokens[9],  tokens[10], tokens[11], db);

                Log.d("DatabaseHelper" ,"CSV read " );
            }
        } catch (IOException e1) {
            Log.e("DatabaseHelper", "Error" + line, e1);
            e1.printStackTrace();
        }



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS LANDMARKS");
        onCreate(db);

    }
    private void insertdata(String name, String city, String district, String state, double lat, double lon, String type,String descshort, String description, String history, String url, String imgurls, SQLiteDatabase database){
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("city", city);
        values.put("district", district);
        values.put("state", state);
        values.put("lat", lat);
        values.put("lon", lon);
        values.put("type", type);
        values.put("descshort", descshort);
        values.put("description", description);
        values.put("history", history);
        values.put("url", url);
        values.put("imgurls", imgurls);
        values.put("visit", "notvisited");
        values.put("date", "00-00-0000");

        database.insert("LANDMARKS", null, values);

    }
}
