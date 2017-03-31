package dz.esi.team.appprototype.data;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.CONTENT_URI;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry._ID;

/**
 * Created by The King Mohamed on 28/03/2017.
 */

class PlantsIDsRetriever {

    static ArrayList<Long> plantsIDs = new ArrayList<>();

    static void RetrieveIDs() {

        Log.v("text", "in static, about to begin transfer");
        String[] projectionPlants = {_ID};
        PlantProvider pp = new PlantProvider();
        Log.v("text", "in static, about to query");
        Cursor cursorPlantsIDs = pp.query(CONTENT_URI, projectionPlants, null, null, null);
        Log.v("text", "in static, done query, about to begin array hydration");
        while (cursorPlantsIDs.moveToNext()) {
            plantsIDs.add(cursorPlantsIDs.getLong(0));
        }
        Log.v("text", "in static, done array hydration");
        pp = null;

    }

}
