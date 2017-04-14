package dz.esi.team.appprototype.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static dz.esi.team.appprototype.HomePage.mDbHelper;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.TABLE_NAME;

/**
 * Created by The King Mohamed on 28/03/2017.
 */

public class PlantRetriever {

    public static Cursor RetrievePlants(String[] projection, String selection, String[] selectionArgs, String order) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Log.v("text", " in plantretriver , about to query");
        Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, order);
        Log.v("text", " in plantretriver  , done query");

        return cursor;
    }

}
