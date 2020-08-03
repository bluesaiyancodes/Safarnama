package com.strongties.safarnama.background_tasks;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

import com.strongties.safarnama.DatabaseHelper;
import com.strongties.safarnama.R;

import static com.strongties.safarnama.MainActivity.places_id_list;
import static com.strongties.safarnama.MainActivity.places_list;

public class OtherBackgroundTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "Avatar BG";

    private Context mContext;

    public OtherBackgroundTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // do some background work here

        DatabaseHelper dbhelper = new DatabaseHelper(mContext);
        SQLiteDatabase database = dbhelper.getReadableDatabase();

        Cursor cursor;

        cursor = database.rawQuery("SELECT name, place_id FROM LANDMARKS", new String[]{});

        if(cursor != null){
            cursor.moveToFirst();
        }else{
            Toast.makeText(mContext, mContext.getString(R.string.error_fetching), Toast.LENGTH_SHORT).show();
        }
        do {
            assert cursor != null;
            places_list.add(cursor.getString(0));
            places_id_list.add(cursor.getString(1));
        }while (cursor.moveToNext());

        cursor.close();

        return true;
    }

    @Override
    protected void onPostExecute(final Boolean result) {
        if (this.isCancelled()) {
            return;
        }
    }
}