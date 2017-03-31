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

        ListView plantsListView = (ListView) findViewById(R.id.listview_plants);
        View emptyView = findViewById(R.id.empty_view);
        plantsListView.setEmptyView(emptyView);

        mCursorAdapter = new PlantCursorAdapter(this, null);
        plantsListView.setAdapter(mCursorAdapter);

        mDbHelper = new PlantDbHelper(this);

        try {
            mDbHelper.createDataBase();
        } catch (Exception e) {
            Log.e("From Main.db creation", e.getMessage());
        }
        mDbHelper.openDataBase();

        Log.v("DatabaseTest", "about to init loader");
        getLoaderManager().initLoader(PLANT_LOADER, null, this);
        Log.v("DatabaseTest", "loader inited");

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                _ID,
                sci_name,
                image
        };

        Log.v("DatabaseTest", "in loader creation");

        // the loader will execute the CP query method on a background thread
        return new CursorLoader(this, CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v("DatabaseTest", "in loader finish");
        // update the adapter with this new cursor containing updated pet data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v("DatabaseTest", "in loader reset");
        // delete the current data
        mCursorAdapter.swapCursor(null);
    }

}