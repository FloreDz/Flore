package dz.esi.team.appprototype.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static dz.esi.team.appprototype.data.PlantContract.CONTENT_AUTHORITY;
import static dz.esi.team.appprototype.data.PlantContract.PATH_PLANTS;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.TABLE_NAME;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry._ID;

/**
 * Created by The King Mohamed on 25/03/2017.
 */

// TODO : don't forget to add the provider to manifest

public class PlantProvider extends ContentProvider {

    public static final String LOG_TAG = PlantProvider.class.getSimpleName();
    private static final int PLANTS = 100, PLANT_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PLANTS, PLANTS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PLANTS + "/#", PLANT_ID);
    }

    private PlantDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new PlantDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PLANTS:  // query the table
                cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PLANT_ID: // query the specific ID
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                Log.e(LOG_TAG, "IllegalArgumentException");
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        //set notification uri on the cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

}

