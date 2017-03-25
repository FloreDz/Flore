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
import android.widget.Toast;

import static dz.esi.team.appprototype.data.PlantContract.CONTENT_AUTHORITY;
import static dz.esi.team.appprototype.data.PlantContract.PATH_PLANTS;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.TABLE_NAME;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry._ID;

/**
 * Created by The King Mohamed on 25/03/2017.
 */

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
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
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
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        //set notification uri on the cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override // inserts a new pet and return it's new URI (concatenated with the new ID)
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);

        if (match == PLANTS)
            return insertPet(uri, values);
        else
            throw new IllegalArgumentException("Insertion is not supported for " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int deletedRows = db.delete(TABLE_NAME, selection, selectionArgs);
        Toast.makeText(getContext(), deletedRows
                + "Pet" + ((deletedRows == 1) ? "" : "s") + " deleted", Toast.LENGTH_SHORT).show();

        getContext().getContentResolver().notifyChange(uri, null);

        return deletedRows;
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


    private Uri insertPet(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long newRowId = db.insert(TABLE_NAME, null, values);

        if (newRowId == -1) {
            Toast.makeText(getContext(), "Error in saving", Toast.LENGTH_SHORT).show();
            Log.v(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        Toast.makeText(getContext(), "Pet saved", Toast.LENGTH_SHORT).show();

        // notify all listeners that the data has changed for the pet content uri
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, newRowId);
    }

}
