package dz.esi.team.appprototype.data;

import android.database.Cursor;
import android.util.Log;

import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.CONTENT_URI;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.famille;

/**
 * Created by The King Mohamed on 28/03/2017.
 */

class PlantRetriever {

    // could add other critias (family view)
    static void RetrievePlants(String[] projection, String selection, String[] selectionArgs) {
        PlantProvider pp = new PlantProvider();
        Log.v("text", " in plantprofile , about to query");
        Log.v("text", " pp == null ? : " + (pp == null));

        Cursor plantProfile = pp.query(CONTENT_URI, projection, selection, selectionArgs, famille + "ASC");
        Log.v("text", " in plantprofile , done query");

        pp = null;
    }

}
