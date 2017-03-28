package dz.esi.team.appprototype.data;

import android.content.ContentUris;
import android.database.Cursor;

import dz.esi.team.appprototype.data.PlantContract.FamilyEntry;

import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.CONTENT_URI;


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
    private String partiesUtilitees;
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


    public PlantProfile(Long plantID) {

        if (plantID < 0) return;

        PlantProvider pp = new PlantProvider();

        Cursor plantProfile = pp.query(ContentUris.withAppendedId(CONTENT_URI, plantID),
                null, null, null, null);

        this.sci_name = plantProfile.
                getString(plantProfile.getColumnIndex(PlantContract.PlantEntry.sci_name));
        this.nom = plantProfile.
                getString(plantProfile.getColumnIndex(PlantContract.PlantEntry.nom));
        this.image = plantProfile.
                getString(plantProfile.getColumnIndex(PlantContract.PlantEntry.image));
        this.resume = plantProfile
                .getString(plantProfile.getColumnIndex(PlantContract.PlantEntry.resume));
        this.constituants = plantProfile.
                getString(plantProfile.getColumnIndex(PlantContract.PlantEntry.constituants));
        this.partiesUtilitees = plantProfile.
                getString(plantProfile.getColumnIndex(PlantContract.PlantEntry.partiesUtilitees));
        this.effets = plantProfile.
                getString(plantProfile.getColumnIndex(PlantContract.PlantEntry.effets));
        this.effetsSecondaires = plantProfile.
                getString(plantProfile.getColumnIndex(PlantContract.PlantEntry.effetsSecondaires));
        this.indications = plantProfile.
                getString(plantProfile.getColumnIndex(PlantContract.PlantEntry.indications));
        this.contreIndication = plantProfile.
                getString(plantProfile.getColumnIndex(PlantContract.PlantEntry.contreIndication));
        this.interaction = plantProfile.
                getString(plantProfile.getColumnIndex(PlantContract.PlantEntry.interaction));
        this.preparation = plantProfile.
                getString(plantProfile.getColumnIndex(PlantContract.PlantEntry.preparation));
        this.lieu = plantProfile.
                getString(plantProfile.getColumnIndex(PlantContract.PlantEntry.lieu));
        this.periodeRecolte = plantProfile.
                getString(plantProfile.getColumnIndex(PlantContract.PlantEntry.periodeRecolte));
        this.remarques = plantProfile.
                getString(plantProfile.getColumnIndex(PlantContract.PlantEntry.remarques));
        this.source = plantProfile.
                getString(plantProfile.getColumnIndex(PlantContract.PlantEntry.source));
        this.liens = plantProfile.
                getString(plantProfile.getColumnIndex(PlantContract.PlantEntry.liens));
        this.id = plantID.toString();


        String familyID = plantProfile.
                getString(plantProfile.getColumnIndex(PlantContract.PlantEntry.idFamille));

        Long idFamille = Long.parseLong(familyID);

        Cursor plantFamily = pp
                .query(ContentUris.withAppendedId(FamilyEntry.CONTENT_URI, idFamille),
                        null, null, null, null);

        this.famille = plantFamily.getString(plantFamily.getColumnIndex(FamilyEntry.nom));
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

    public String getPartiesUtilitees() {
        return partiesUtilitees;
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
