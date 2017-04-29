package dz.esi.flore.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import dz.esi.flore.BaseActivity;
import dz.esi.flore.HomePage;

import static dz.esi.flore.data.PlantContract.PlantEntry.TABLE_NAME;
import static dz.esi.flore.data.PlantContract.PlantEntry.sci_name;

/**
 * Created by  28/03/2017.
 */

public class PlantRetriever {

    private static final String TAG = PlantRetriever.class.getSimpleName();

    public static Cursor RetrievePlants(String[] projection, String selection, String[] selectionArgs, String order) {
        SQLiteDatabase db = BaseActivity.mDbHelper.getReadableDatabase();
        Log.d(TAG, "RetrievePlants: , about to query");
        Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, order);
        Log.d(TAG, "RetrievePlants: , done query");

        return cursor;
    }

    public static Cursor SearchPlants(String nameStartsWith) {
        SQLiteDatabase db = BaseActivity.mDbHelper.getReadableDatabase();
        nameStartsWith = nameStartsWith.trim().toUpperCase();
        String selection = "UPPER (" + sci_name + ") like ?";
        String[] selectionArgs = new String[] {nameStartsWith + "%"};

        Log.d(TAG, "RetrievePlants: , about to query");
        Cursor cursor = db.query(TABLE_NAME, HomePage.homeMenuProjection, selection, selectionArgs, null, null, BaseActivity.SHOW_PLANTS_BY_SCIENTIFIQUE_NAMES);
        Log.d(TAG, "RetrievePlants: , done query");

        return cursor;
    }

}
/**
 * last verification 28/04/2017
 */

