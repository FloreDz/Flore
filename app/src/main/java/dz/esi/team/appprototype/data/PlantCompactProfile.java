package dz.esi.team.appprototype.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import dz.esi.team.appprototype.data.PlantContract.PlantEntry;

import static dz.esi.team.appprototype.HomePage.mDbHelper;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.TABLE_NAME;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry._ID;


/**
 * Created by The King Mohamed on 28/03/2017.
 */

public class PlantCompactProfile {

    private String id;
    private String sci_name;
    private String image;
    private String famille;

    public PlantCompactProfile(Long plantID) {  // TODO : free up the Cursor

        if (plantID < 0L) return;

        Cursor plantProfile = null;

        try {

            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            String[] selectionArgs = {"" + plantID};
            Log.v("text", " in plantprofile , about to query");
            plantProfile = db.query(TABLE_NAME, null, _ID + "=?", selectionArgs, null, null, null);
            Log.v("text", " in plantprofile , done query");

            plantProfile.moveToFirst();

            this.sci_name = plantProfile.
                    getString(plantProfile.getColumnIndexOrThrow(PlantEntry.sci_name));
            this.image = plantProfile.
                    getString(plantProfile.getColumnIndexOrThrow(PlantEntry.image));
            this.famille = plantProfile.
                    getString(plantProfile.getColumnIndexOrThrow(PlantEntry.famille));
            this.id = plantID.toString();

        } catch (Exception e) {
            Log.v("PlantComProfil Cnstrctr", e.getMessage());
        } finally {
            plantProfile.close();
        }

    }

    public String get_ID() {
        return id;
    }

    public String getSci_name() {
        return sci_name;
    }

    public String getImage() {
        return image;
    }

    public String getFamille() {
        return famille;
    }

}
