package dz.esi.flore.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created on 25/03/2017.
 */

public class PlantContract {

    //TODO:  change the package name
    public static final String CONTENT_AUTHORITY = "dz.esi.flore";
    public static final String PATH_PLANTS = "Plante";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private PlantContract() {
    }


    public static final class PlantEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PLANTS);

        public static final String TABLE_NAME = "Plante";

        public static final String
                _ID = BaseColumns._ID,
                sci_name = "sci_name",
                nom = "nom",
                image = "image",
                famille = "famille",
                resume = "resume",
                constituants = "constituants",
                partiesUtilisees = "partiesUtilisees",
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

}
/**
 * last verification 28/04/2017
 */