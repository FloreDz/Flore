package dz.esi.team.appprototype.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by The King Mohamed on 25/03/2017.
 */

public class PlantContract {

    //TODO: do not forget to change the package name
    public static final String CONTENT_AUTHORITY = "dz.esi.team.appprototype";
    public static final String PATH_PLANTS = "Plante";
    public static final String PATH_FAMILIES = "Famille";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private PlantContract() {
    }


    public static final class PlantEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PLANTS);

        public static final String TABLE_NAME = "Plante";

        public static final String _ID = BaseColumns._ID,
                sci_name = "sci_name",
                nom = "nom",
                image = "image",
                idFamille = "idFamille",
                resume = "resume",
                constituants = "constituants",
                partiesUtilitees = "partiesUtilitees",
                effets = "effets",
                effetsSecondaires = "effetsSecondaires",
                indications = "indications",
                contreIndication = "contreIndication",
                interaction = "interaction",
                preparation = "preparation",
                lieu = "lieu",
                periodeRecolte = "periodeRecolte",
                remarques = "remarques",
                source = "source",
                liens = "liens";
    }

    public static final class FamilyEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FAMILIES);

        public static final String TABLE_NAME = "Famille";

        public static final String _ID = BaseColumns._ID, nom = "nom";
    }
}
