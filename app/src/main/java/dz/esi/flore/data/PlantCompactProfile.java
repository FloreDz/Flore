package dz.esi.flore.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static dz.esi.flore.BaseActivity.mDbHelper;
import static dz.esi.flore.data.PlantContract.PlantEntry.TABLE_NAME;

/**
 * Created on 22/04/2017.
 */

public class PlantCompactProfile {

    private String id;
    private String sci_name;
    private String image;
    private String famille;


    public PlantCompactProfile(Long plantID) {

        if (plantID < 0L) return;

        Cursor plantProfile = null;

        try {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            String[] projection = {
                    PlantEntry.sci_name,
                    PlantEntry.famille
            };
            String[] selectionArgs = {"" + plantID};
            Log.v("PlantProfile", " , about to query");
            plantProfile = db.query(TABLE_NAME, projection, PlantEntry._ID + "=?", selectionArgs, null, null, null);
            Log.v("PlantProfile", " , done query");

            plantProfile.moveToFirst();

            this.sci_name = plantProfile.
                    getString(plantProfile.getColumnIndexOrThrow(PlantContract.PlantEntry.sci_name));
            this.image = this.sci_name.toLowerCase();
            this.famille = plantProfile.
                    getString(plantProfile.getColumnIndexOrThrow(PlantContract.PlantEntry.famille));
            this.id = plantID.toString();


            Log.v("PlantCompactProfile", " , about to exit the constructor");

        } catch (Exception e) {
            Log.v("PlantCoProfile Cnstrctr", e.getMessage());
        } finally {
            plantProfile.close();
        }

    }

    public String get_ID() {
        return this.id;
    }

    public String getSci_name() {
        return this.sci_name;
    }

    public String getImage() {
        return this.image;
    }

    public String getFamille() {
        return this.famille;
    }
}

/**
 * last verification 28/04/2017
 */
