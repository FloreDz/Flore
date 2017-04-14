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

public class PlantProfile {

    private String id;
    private String sci_name;
    private String nom;
    private String image;
    private String famille;
    private String resume;
    private String constituants;
    private String partiesUtilisees;
    private String effets;
    private String effetsSecondaires;
    private String indications;
    private String contreIndication;
    private String interaction;
    private String preparation;
    private String lieu;
    private String periodeRecolte;
    private String remarques;
    private String source;
    private String liens;


    public PlantProfile(Long plantID) {  // TODO : free up the Cursor

        if (plantID < 0L) return;

        Cursor plantProfile = null;

        try {     /*
            PlantProvider pp = new PlantProvider();
            Log.v("text", " pp == null ? : " + (pp == null));
            Log.v("PlantProfile try", " new Uri : " + ContentUris.withAppendedId(CONTENT_URI, plantID).toString());
            plantProfile = pp.query(ContentUris.withAppendedId(CONTENT_URI, plantID),
                    null, null, null, null);
            pp = null;  */


            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            String[] selectionArgs = {"" + plantID};
            Log.v("PlantProfile", " , about to query");
            plantProfile = db.query(TABLE_NAME, null, _ID + "=?", selectionArgs, null, null, null);
            Log.v("PlantProfile", " , done query");

            plantProfile.moveToFirst();

            this.sci_name = plantProfile.
                    getString(plantProfile.getColumnIndexOrThrow(PlantEntry.sci_name));
            this.nom = plantProfile.
                    getString(plantProfile.getColumnIndexOrThrow(PlantEntry.nom));
            this.image = plantProfile.
                    getString(plantProfile.getColumnIndexOrThrow(PlantEntry.image));
            this.famille = plantProfile.
                    getString(plantProfile.getColumnIndexOrThrow(PlantEntry.famille));
            this.resume = plantProfile.
                    getString(plantProfile.getColumnIndexOrThrow(PlantEntry.resume));
            this.constituants = plantProfile.
                    getString(plantProfile.getColumnIndexOrThrow(PlantEntry.constituants));
            this.partiesUtilisees = plantProfile.
                    getString(plantProfile.getColumnIndexOrThrow(PlantEntry.partiesUtilisees));
            this.effets = plantProfile.
                    getString(plantProfile.getColumnIndexOrThrow(PlantEntry.effets));
            this.effetsSecondaires = plantProfile.
                    getString(plantProfile.getColumnIndexOrThrow(PlantEntry.effetsSecondaires));
            this.indications = plantProfile.
                    getString(plantProfile.getColumnIndexOrThrow(PlantEntry.indications));
            this.contreIndication = plantProfile.
                    getString(plantProfile.getColumnIndexOrThrow(PlantEntry.contreIndication));
            this.interaction = plantProfile.
                    getString(plantProfile.getColumnIndexOrThrow(PlantEntry.interaction));
            this.preparation = plantProfile.
                    getString(plantProfile.getColumnIndexOrThrow(PlantEntry.preparation));
            this.lieu = plantProfile.
                    getString(plantProfile.getColumnIndexOrThrow(PlantEntry.lieu));
            this.periodeRecolte = plantProfile.
                    getString(plantProfile.getColumnIndexOrThrow(PlantEntry.periodeRecolte));
            this.remarques = plantProfile.
                    getString(plantProfile.getColumnIndexOrThrow(PlantEntry.remarques));
            this.source = plantProfile.
                    getString(plantProfile.getColumnIndexOrThrow(PlantEntry.source));
            this.liens = plantProfile.
                    getString(plantProfile.getColumnIndexOrThrow(PlantEntry.liens));
            this.id = plantID.toString();

            Log.v("PlantProfile", " , about to exit the constructor");

        } catch (Exception e) {
            Log.v("PlantProfil Constructor", e.getMessage());
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

    public String getNom() {
        return nom;
    }

    public String getImage() {
        return image;
    }

    public String getFamille() {
        return famille;
    }

    public String getResume() {
        return resume;
    }

    public String getConstituants() {
        return constituants;
    }

    public String getPartiesUtilisees() {
        return partiesUtilisees;
    }

    public String getEffets() {
        return effets;
    }

    public String getEffetsSecondaires() {
        return effetsSecondaires;
    }

    public String getIndications() {
        return indications;
    }

    public String getContreIndication() {
        return contreIndication;
    }

    public String getInteraction() {
        return interaction;
    }

    public String getPreparation() {
        return preparation;
    }

    public String getLieu() {
        return lieu;
    }

    public String getPeriodeRecolte() {
        return periodeRecolte;
    }

    public String getRemarques() {
        return remarques;
    }

    public String getSource() {
        return source;
    }

    public String getLiens() {
        return liens;
    }


}
