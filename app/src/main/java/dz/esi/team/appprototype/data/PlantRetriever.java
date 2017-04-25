package dz.esi.team.appprototype.data;

import android.database.Cursor;
import android.database.StaleDataException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static dz.esi.team.appprototype.BaseActivity.SHOW_PLANTS_BY_SCIENTIFIQUE_NAMES;
import static dz.esi.team.appprototype.HomePage.mDbHelper;
import static dz.esi.team.appprototype.HomePage.homeMenuProjection;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.TABLE_NAME;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.sci_name;

/**
 * Created by The King Mohamed on 28/03/2017.
 */

public class PlantRetriever {

    private static final String TAG = PlantRetriever.class.getSimpleName();

    public static Cursor RetrievePlants(String[] projection, String selection, String[] selectionArgs, String order) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Log.d(TAG, "RetrievePlants: , about to query");
        Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, order);
        Log.d(TAG, "RetrievePlants: , done query");

        return cursor;
    }

    public static Cursor SearchPlants(String nameStartsWith) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        nameStartsWith = nameStartsWith.trim().toUpperCase();
        String selection = "UPPER (" + sci_name + ") like ?";
        String[] selectionArgs = new String[] {nameStartsWith + "%"};

        Log.d(TAG, "RetrievePlants: , about to query");
        Cursor cursor = db.query(TABLE_NAME, homeMenuProjection, selection, selectionArgs, null, null, SHOW_PLANTS_BY_SCIENTIFIQUE_NAMES);
        Log.d(TAG, "RetrievePlants: , done query");

        return cursor;
    }

}
