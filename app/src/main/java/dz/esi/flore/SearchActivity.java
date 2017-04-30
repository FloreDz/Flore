package dz.esi.flore;

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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import dz.esi.flore.data.PlantCursorAdapter;
import dz.esi.flore.data.PlantRetriever;

import static dz.esi.flore.data.PlantContract.PlantEntry.CONTENT_URI;
import static dz.esi.flore.data.PlantContract.PlantEntry._ID;
import static dz.esi.flore.data.PlantContract.PlantEntry.famille;
import static dz.esi.flore.data.PlantContract.PlantEntry.sci_name;

public class SearchActivity extends BaseActivity {

    private static final int PLANT_LOADER = 0;
    private static final String PLANT_QUERY = "PLANT_QUERY";
    private static final String TAG = SearchActivity.class.getSimpleName();

    private SearchView searchView;
    private SearchListViewLoader searchListViewLoader;
    private ListView searchResultListView;
    private View emptyView;

    private String queryText = "";
    private String[] homeMenuProjection = {
            _ID,
            sci_name,
            famille
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_plantes);

        if (savedInstanceState != null)
            this.queryText = savedInstanceState.getString(PLANT_QUERY);

        activateToolBar(false);

        searchResultListView = (ListView) findViewById(R.id.search_result_list_view);
        emptyView = findViewById(R.id.empty_view);

        searchResultListView.setEmptyView(emptyView);
        searchResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
                intent.putExtra("PlantID", id);
                startActivity(intent);
            }
        });


        initDatabase(this);

        searchListViewLoader = new SearchListViewLoader();

        searchListViewLoader.populateSearchListView();


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());

        searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setSearchableInfo(searchableInfo);
        searchView.setIconified(false);
        searchView.setQuery(this.queryText, false);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: query = " + query);
                hideKeyboard();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                queryText = newText;

                final Cursor cursor = PlantRetriever.SearchPlants(newText);
                searchListViewLoader.reloadWithUpdatedCursor(cursor);

                return true;

            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d(TAG, "onClose: in");
                return true;
            }
        });

        return true;
    }

    private void hideKeyboard() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus()
                        .getWindowToken(),0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PLANT_QUERY, this.queryText);
        Log.d(TAG, "onSaveInstanceState: in");
    }

    class SearchListViewLoader implements LoaderManager.LoaderCallbacks<Cursor> {

        private PlantCursorAdapter mCursorAdapter = new PlantCursorAdapter(SearchActivity.this, null, SHOW_PLANTS_BY_SCIENTIFIQUE_NAMES);


        public void populateSearchListView() {

            Log.d(TAG, "about to init loader");
            getLoaderManager().initLoader(PLANT_LOADER, null, this);
            Log.d(TAG, "loader inited");
            Cursor cursor = PlantRetriever.SearchPlants(queryText);
            reloadWithUpdatedCursor(cursor);

        }

        public void reloadWithUpdatedCursor(Cursor cursor) {
            mCursorAdapter.swapCursor(cursor);
            searchResultListView.setAdapter(mCursorAdapter);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.d(TAG, "in loader creation");

            // the loader will execute the CP query method on a background thread
            return new CursorLoader(SearchActivity.this, CONTENT_URI, homeMenuProjection, null, null, SHOW_PLANTS_BY_SCIENTIFIQUE_NAMES);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {}

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            Log.d(TAG, "in loader reset");
            // delete the current data
            mCursorAdapter.swapCursor(null);
        }

    }
}
