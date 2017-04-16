package dz.esi.team.appprototype;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import dz.esi.team.appprototype.data.PlantCursorAdapter;
import dz.esi.team.appprototype.data.PlantDbHelper;
import dz.esi.team.appprototype.data.PlantRetriever;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static dz.esi.team.appprototype.HomePage.DISPLAY_STATE;
import static dz.esi.team.appprototype.HomePage.SHOW_PLANTS_BY_DEFAULT;
import static dz.esi.team.appprototype.HomePage.SHOW_PLANTS_BY_FAMILIES;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.CONTENT_URI;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry._ID;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.famille;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.image;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.sci_name;

public class SearchActivity extends BaseActivity{
    public static final String PLANT_QUERY = "PLANT_QUERY";
    private static final String TAG = HomePage.class.getSimpleName();
    private SearchView searchView;
    private String queryText = null;

    /* TODO : MOHAMED added : */
    private static final int PLANT_LOADER = 0;
    public static PlantDbHelper mDbHelper;
    ProgressBar progressBar;
    ListViewLoader listViewLoader;
    ListView searchResultListView;
    private String[] homeMenuProjection = {
            _ID,
            sci_name,
            image,
            famille
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (DISPLAY_STATE.equals(SHOW_PLANTS_BY_FAMILIES)) DISPLAY_STATE = SHOW_PLANTS_BY_DEFAULT;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_plantes);

        // this method will activate the layout toolBar , it is implemented in the BaseActivity
//        activateToolBar(false);

        // todo: Mohamed added :
        progressBar = (ProgressBar) findViewById(R.id.search_progress_bar);
        searchResultListView = (ListView) findViewById(R.id.search_result_list_view);
        View emptyView = findViewById(R.id.empty_view);
        searchResultListView.setEmptyView(emptyView);
        searchResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
                intent.putExtra("PlantID", id);
                startActivity(intent);
            }
        });

        mDbHelper = new PlantDbHelper(this);

        try {
            mDbHelper.createDataBase();
        } catch (Exception e) {
            Log.e("From Main.db creation", e.getMessage());
        }
        mDbHelper.openDataBase();

        listViewLoader = new ListViewLoader(progressBar,true);
        listViewLoader.execute();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());

        searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setSearchableInfo(searchableInfo);
        searchView.setIconified(false);
        searchView.setQuery(this.queryText,false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: query = " + query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                queryText =  newText ;

                final Cursor cursor = PlantRetriever.SearchPlants(newText);
                listViewLoader.reloadWithNewCursor(cursor);

                return true;

            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                finish();
                return true;
            }
        });
        return true;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(PLANT_QUERY,this.queryText);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
       this.queryText =  savedInstanceState.getString(PLANT_QUERY)  ;
        super.onRestoreInstanceState(savedInstanceState);
    }

    class ListViewLoader implements LoaderManager.LoaderCallbacks<Cursor> {

        PlantCursorAdapter mCursorAdapter = new PlantCursorAdapter(SearchActivity.this, null);
        ProgressBar progressBar;
        boolean firstRun;

        public ListViewLoader(ProgressBar progressBar, boolean firstRun) {
            this.progressBar = progressBar;
            this.firstRun = firstRun;
        }

        public void execute() {
            this.progressBar.setVisibility(VISIBLE);
            if (firstRun) {
                Log.v(TAG, "about to init loader");
                getLoaderManager().initLoader(PLANT_LOADER, null, this);
                Log.v(TAG, "loader inited");
                firstRun = false;
            } else {
                Cursor cursor = PlantRetriever.RetrievePlants(homeMenuProjection, null, null, DISPLAY_STATE);

                listViewLoader.progressBar.setVisibility(GONE);
                mCursorAdapter.swapCursor(cursor);
                searchResultListView.setAdapter(mCursorAdapter);
            }
        }

        public void reloadWithNewCursor(Cursor cursor) {
            listViewLoader.progressBar.setVisibility(GONE);
            mCursorAdapter.swapCursor(cursor);
            searchResultListView.setAdapter(mCursorAdapter);
        }


        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.v(TAG, "in loader creation");

            // the loader will execute the CP query method on a background thread
            return new CursorLoader(SearchActivity.this, CONTENT_URI, homeMenuProjection, null, null, DISPLAY_STATE);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            Log.v(TAG, "in loader finish");
            // update the adapter with this new cursor containing updated plant data
            listViewLoader.progressBar.setVisibility(GONE);
            mCursorAdapter.swapCursor(cursor);
            searchResultListView.setAdapter(mCursorAdapter);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            Log.v(TAG, "in loader reset");
            // delete the current data
            mCursorAdapter.swapCursor(null);
        }

    }
}
