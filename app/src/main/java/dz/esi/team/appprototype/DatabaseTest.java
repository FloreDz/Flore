package dz.esi.team.appprototype;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import dz.esi.team.appprototype.data.PlantCursorAdapter;
import dz.esi.team.appprototype.data.PlantDbHelper;

import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.CONTENT_URI;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry._ID;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.image;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.sci_name;

public class DatabaseTest extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PLANT_LOADER = 0;
    PlantCursorAdapter mCursorAdapter;

    PlantDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_test);

        ListView plantsListView = (ListView) findViewById(R.id.listview_plantes);
        View emptyView = findViewById(R.id.empty_view);
        plantsListView.setEmptyView(emptyView);

        mCursorAdapter = new PlantCursorAdapter(this, null);
        plantsListView.setAdapter(mCursorAdapter);

        try {
            mDbHelper = new PlantDbHelper(this);
            mDbHelper.createDataBase();
        } catch (Exception e) {
            Log.e("From Main.db creation", e.getMessage());
        }
        mDbHelper.openDataBase();

        getLoaderManager().initLoader(PLANT_LOADER, null, this);


/*
        try {
            mDbHelper.createDataBase();
        } catch (Exception e) {
            Log.e("From Main.db creation",e.getMessage());
        }
        mDbHelper.openDataBase();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Log.v("Got readableDB","from Main");
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME ,null);
            Log.v("Got the Query","from Main.try2");
            TextView tv = (TextView) findViewById(R.id.tv);
            Log.v("Setting count","from Main.try , count to be : " + cursor.getCount());
            tv.setText(cursor.getCount() + "");

        } catch (Exception e) {
            Log.e("Query",e.getMessage() + ", Stack printed");
            e.printStackTrace();
        } finally {
            cursor.close();
        }
*/

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                _ID,
                sci_name,
                image
        };

        // the loader will execute the CP query method on a background thread
        return new CursorLoader(this, CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // update the adapter with this new cursor containing updated pet data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // delete the current data
        mCursorAdapter.swapCursor(null);
    }


}