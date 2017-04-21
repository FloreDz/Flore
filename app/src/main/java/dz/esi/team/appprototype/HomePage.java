package dz.esi.team.appprototype;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import dz.esi.team.appprototype.data.PlantCursorAdapter;
import dz.esi.team.appprototype.data.PlantDbHelper;
import dz.esi.team.appprototype.data.PlantRetriever;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.CONTENT_URI;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry._ID;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.famille;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.image;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.sci_name;


public class HomePage extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int LOAD_IMAGE_RESULT = 1;
    private static final String LOADED_IMAGE_PATH = "LOADED_IMAGE_PATH";
    private static final String LOADED_IMAGE_URI = "LOADED_IMAGE_URI";
    private static final String DISPLAY_TYPE = "DISPLAY_TYPE";
    private static final String TAG = HomePage.class.getSimpleName();
    private static final String ERROR_MESS = "something went wrong";
    private static final int PLANT_LOADER = 0;
    public static PlantDbHelper mDbHelper;
    private static String DISPLAY_STATE = SHOW_PLANTS_BY_FAMILIES;

    /* TODO : MOHAMED added : */
    public static ArrayList<String> plantsHeaders = null;
    ProgressBar progressBar;
    ListViewLoader listViewLoader;

    //layouts
    LinearLayout takeImageLayout;
    LinearLayout importImageLayout;
    DrawerLayout drawer;
    FrameLayout optionMenuBackground;

    // buttons
    FloatingActionButton fabRecognise;
    FloatingActionButton fabTakeImage;
    FloatingActionButton fabImportImage;

    //labels
    TextView importImageLabel;
    TextView takeImageLabel;
    ListView plantsListView;

    //image path
    private String imagePath;

    //costum components
    private ListView plantListView;
    private NavigationView navigationView;
    private Toolbar toolbar_search_access;
    private ActionBarDrawerToggle toggle;
    private Menu optionMenu;
    public static String[] homeMenuProjection = {
            _ID,
            sci_name,
            image,
            famille
    };

    // ****************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        introPageHandler();
        super.onCreate(savedInstanceState);
        Log.v("HomePage", "ACTIVITY CREATED");
        setContentView(R.layout.activity_home_page);
//        setupWindowAnimations();

        widgetHydration();

        //costume components
        navigationView.setNavigationItemSelectedListener(this);
        setSupportActionBar(toolbar_search_access);

        //layout
        optionMenuBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVisible()) {
                    hideOptionMenu();
                }
            }
        });


        /* TODO : MOHAMED added : */

        plantsListView = (ListView) findViewById(R.id.plantes_list_view);
        View emptyView = findViewById(R.id.empty_view);
        plantsListView.setEmptyView(emptyView);
        plantsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "item clicked id (of the view) : " + view.getId());
                Log.d(TAG, "item clicked == family : " + (R.id.item_family == view.getId()));
                Log.d(TAG, "item clicked == plant : " + (R.id.item_plant == view.getId()));
                Log.d(TAG, "onItemClick: the item with the position " + position
                        + " and the id " + id + " is clicked");
                Intent intent = new Intent(HomePage.this, ProfileActivity.class);
                intent.putExtra("PlantID", id);
                Log.d(TAG, "about to start activity 'plantactivity'");
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v("HomePage", "ACTIVITY STARTED");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("HomePage", "ACTIVITY RESUMED");

        mDbHelper = new PlantDbHelper(this);

        try {
            mDbHelper.createDataBase();
        } catch (Exception e) {
            Log.e("From Main, .db creation", e.getMessage());
        }
        mDbHelper.openDataBase();

        initializePlantsHeaders();

        listViewLoader = new ListViewLoader(progressBar,true);
        listViewLoader.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("HomePage", "ACTIVITY PAUSED");

        mDbHelper.close();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("HomePage", "ACTIVITY STOPPED");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v("HomePage", "ACTIVITY RESTARTED");
    }

    private void switchDisplayState() {
        String displayMessage;
        if (DISPLAY_STATE.equals(SHOW_PLANTS_BY_FAMILIES)) {
            DISPLAY_STATE = SHOW_PLANTS_BY_SCIENTIFIQUE_NAMES;
            displayMessage = "Affichage par plantes";
        } else {
            DISPLAY_STATE = SHOW_PLANTS_BY_FAMILIES;
            displayMessage = "Affichage par familles";
        }
        Toast.makeText(this, displayMessage, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "display state  changed to : " + DISPLAY_STATE);
    }

//    private void setupWindowAnimations() {
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            Log.v(TAG, "Into LOLLIPOP");
//            Fade fade = new Fade();
//            fade.setDuration(1000);
//            getWindow().setEnterTransition(fade);
//            getWindow().setExitTransition(fade);
//        }
//    }

    private void initializePlantsHeaders() {
        String[] projection = {famille, sci_name};
        String currentFamily = null;
        String nextFamily = null;
        Cursor cursor = null;

        try {
            Log.v(TAG, "just got into try");
            plantsHeaders = new ArrayList<>();
            Log.v(TAG, "beginning : ArrayList size = " + plantsHeaders.size());
            cursor = PlantRetriever.RetrievePlants(projection, null, null, SHOW_PLANTS_BY_FAMILIES);
            Log.v(TAG, "about to loop");

            if (cursor.moveToFirst()) {
                currentFamily = cursor.getString(0);
                plantsHeaders.add(cursor.getString(1));
                Log.v(TAG, "in if ,added in ArrayList : " + cursor.getString(1));
            }

            while (cursor.moveToNext()) {
                nextFamily = cursor.getString(0);
                if (!currentFamily.equals(nextFamily)) {
                    plantsHeaders.add(cursor.getString(1));
                    currentFamily = nextFamily;
                    Log.v(TAG, "in while ,added in ArrayList : " + cursor.getString(1));
                }
            }
            Log.v(TAG, "end : ArrayList size = " + plantsHeaders.size());
        } finally {
            if (cursor != null) cursor.close();
        }
        Log.v(TAG, "Finally : ArrayList = " + plantsHeaders.toString());

    }


    // *******************************************************************************************************


    public void widgetHydration() {

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // layouts
        optionMenuBackground = (FrameLayout) findViewById(R.id.option_menu_background);
        takeImageLayout = (LinearLayout) findViewById(R.id.layout_take_picture);
        importImageLayout = (LinearLayout) findViewById(R.id.layout_import_picture);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar_search_access = (Toolbar) findViewById(R.id.toolbar_search_access);
        setSupportActionBar(toolbar_search_access);

        //costume components
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        plantListView = (ListView) findViewById(R.id.plantes_list_view);
        toolbar_search_access = (Toolbar) findViewById(R.id.toolbar_search_access);
        setSupportActionBar(toolbar_search_access);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar_search_access, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle); // TODO: check this method, it's deprecated
        toggle.syncState();

        // buttons
        fabRecognise = (FloatingActionButton) findViewById(R.id.fab_recognition);
        fabImportImage = (FloatingActionButton) findViewById(R.id.fab_import_picture);
        fabTakeImage = (FloatingActionButton) findViewById(R.id.fab_take_picture);

        //labels
        importImageLabel = (TextView) findViewById(R.id.label_import_picture);
        takeImageLabel = (TextView) findViewById(R.id.label_take_picture);


    }

    public void introPageHandler() {
        // this thread will start the intro activity if the app is being lunched for the first time
        // declare a new thread to do a preference check
        Thread intro = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);
                Log.d(TAG, "run: this is the first start " + isFirstStart);
                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    Intent introActivityIntent = new Intent(HomePage.this, IntroActivity.class);
                    startActivity(introActivityIntent);

                    //  Make a new preferences editor
                    SharedPreferences.Editor preferenceEditor = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    preferenceEditor.putBoolean("firstStart", false);

                    //  Apply changes
                    preferenceEditor.apply();
                }
            }
        });
        intro.start();
    }

    @Override
    public void onBackPressed() {
        // pressing the back button event handling
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (isVisible()) {
            hideOptionMenu();
        } else super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: creating the option menu");
        getMenuInflater().inflate(R.menu.home_menu, menu);
        menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.getItem(1).setChecked(DISPLAY_STATE.equals(SHOW_PLANTS_BY_FAMILIES));
        this.optionMenu = menu;

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        switch (id) {
            case R.id.search:   // start the search activity
                Log.d(TAG, "onOptionsItemSelected: about to go into SearchActivity");
                startActivity(new Intent(HomePage.this, SearchActivity.class));
                break;
            case R.id.option_show_by_family:
                switchDisplayState();
                item.setChecked(DISPLAY_STATE.equals(SHOW_PLANTS_BY_FAMILIES));
                listViewLoader.execute();
                break;
            default:
        }

        return toggle.onOptionsItemSelected(item);

    }

    //selecting a navigation item from the menu drawer event handling
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent intent;
        int id = item.getItemId();

        if (id == R.id.nav_preferences) {

        } else if (id == R.id.nav_aboutus) {
            startActivity(new Intent(HomePage.this, AboutUsActivity.class));
        } else if (id == R.id.nav_user_guide) {
            intent = new Intent(HomePage.this, HelpActivity.class);
            startActivity(intent);
        }
        if (isVisible()) hideOptionMenu();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void fabOnClick(View v) {
        // one of the fab buttons was clicked
        // this method have been implemented in the XML file content_main.xml and linked to each one of the three fab buttons
        Log.d(TAG, "in fabOnClick: ");

        int id = v.getId();

        if (id == fabRecognise.getId()) {
            // the main main FAB is clicked
            if (isVisible()) {
                hideOptionMenu();
            } else {
                showOptionMenu();
            }

        } else if (isVisible()) {

            if (id == fabImportImage.getId()) {
                this.hideOptionMenu();
                this.loadImageFromGallery();

            } else if (id == fabTakeImage.getId()) {
                this.hideOptionMenu();
                this.phoneCameraAccess();
            }
        }
    }

    private void switchFabImageTo(FloatingActionButton fab, int resourceID) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            fab.setImageDrawable(getDrawable(resourceID));
        } else {
            fab.setImageResource(resourceID);
        }
    }

    public void labelOnClick(View v) {
        // one of the labels of floating action button is clicked
        // this method had been implemented in the XML file content_main.xml and linked to each one of the three label of FAB
        Log.d(TAG, "labelOnClick: ");

        int id = v.getId();
        if (isVisible()) {

            this.hideOptionMenu();
            if (id == importImageLabel.getId()) {
                this.loadImageFromGallery();
            } else if (id == takeImageLabel.getId()) {
                this.phoneCameraAccess();
            }
        }
    }

    private void loadImageFromGallery() {
        // accessing the phone gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, LOAD_IMAGE_RESULT);
    }

    private void phoneCameraAccess() {
        // accessing the phone Camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, LOAD_IMAGE_RESULT);
    }

    private void hideOptionMenu() {
        Log.d(TAG, "hideOptionMenu: hiding the option menu");

        switchFabImageTo(fabRecognise, R.drawable.camera_fab);

        final Animation rotateCamera = AnimationUtils.loadAnimation(HomePage.this, R.anim.rotate_to_camera);
        final Animation hideOptionButtons = AnimationUtils.loadAnimation(HomePage.this, R.anim.hide_buttons);

        this.disableLayoutsVisibility();
        this.disableClickable();

        takeImageLayout.startAnimation(hideOptionButtons);
        importImageLayout.startAnimation(hideOptionButtons);
        fabRecognise.startAnimation(rotateCamera);

    }

    // optionMenu display handling

    private void showOptionMenu() {
        Log.d(TAG, "showOptionMenu: showing the option menu");

        switchFabImageTo(fabRecognise, R.drawable.plus_button);

        final Animation rotateCamera = AnimationUtils.loadAnimation(HomePage.this, R.anim.rotate_to_x);
        final Animation showOptionButtons = AnimationUtils.loadAnimation(HomePage.this, R.anim.show_buttons);

        this.enableLayoutsVisibility();
        this.enableClickable();

        takeImageLayout.startAnimation(showOptionButtons);
        importImageLayout.startAnimation(showOptionButtons);
        fabRecognise.startAnimation(rotateCamera);

    }

    public void disableClickable() {

        importImageLayout.setClickable(false);
        takeImageLayout.setClickable(false);
        importImageLabel.setClickable(false);
        takeImageLabel.setClickable(false);
        fabImportImage.setClickable(false);
        fabTakeImage.setClickable(false);
    }

    public void enableClickable() {

        importImageLayout.setClickable(true);
        takeImageLayout.setClickable(true);
        importImageLabel.setClickable(true);
        takeImageLabel.setClickable(true);
        fabImportImage.setClickable(true);
        fabTakeImage.setClickable(true);

    }

    public void enableLayoutsVisibility() {

        takeImageLayout.setVisibility(VISIBLE);
        importImageLayout.setVisibility(VISIBLE);
        optionMenuBackground.setVisibility(VISIBLE);

    }

    public void disableLayoutsVisibility() {

        takeImageLayout.setVisibility(GONE);
        importImageLayout.setVisibility(GONE);
        optionMenuBackground.setVisibility(GONE);

    }

    public boolean isVisible() {
        return (takeImageLayout.getVisibility() == VISIBLE && importImageLayout.getVisibility() == VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // CheckING which request we're responding to
        if (requestCode == LOAD_IMAGE_RESULT) {
            // MakNGeI sure the request was successful
            if (resultCode == RESULT_OK) {

                Uri pickedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
                cursor.moveToFirst();
                this.imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

                // starting the ImageOptionsActivity activity
                Intent intent = new Intent(HomePage.this, ImageOptionsActivity.class);
                intent.putExtra(LOADED_IMAGE_PATH, this.imagePath);
                intent.putExtra(LOADED_IMAGE_URI, pickedImage.toString());
                startActivity(intent);

            } else {
                Toast.makeText(HomePage.this, ERROR_MESS, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(DISPLAY_TYPE, DISPLAY_STATE);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        DISPLAY_STATE = savedInstanceState.getString(DISPLAY_TYPE);
        super.onRestoreInstanceState(savedInstanceState);
    }

    class ListViewLoader implements LoaderManager.LoaderCallbacks<Cursor> {

        PlantCursorAdapter mCursorAdapter = new PlantCursorAdapter(HomePage.this, null ,DISPLAY_STATE);
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
                mCursorAdapter.setDISPLAY_STATE(DISPLAY_STATE);
                final Cursor cursor = PlantRetriever.RetrievePlants(homeMenuProjection, null, null, DISPLAY_STATE);
                mCursorAdapter.swapCursor(null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listViewLoader.progressBar.setVisibility(GONE);
                        mCursorAdapter.swapCursor(cursor);
                        plantsListView.setAdapter(mCursorAdapter);
                    }
                }, 1000);
            }
        }


        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            Log.v(TAG, "in loader creation");

            // the loader will execute the CP query method on a background thread
            return new CursorLoader(HomePage.this, CONTENT_URI, homeMenuProjection, null, null, DISPLAY_STATE);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            final Cursor cur = cursor;

            Log.v(TAG, "in loader finish");
            // update the adapter with this new cursor containing updated plant data
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    listViewLoader.progressBar.setVisibility(GONE);
                    mCursorAdapter.swapCursor(cur);
                    plantsListView.setAdapter(mCursorAdapter);
                }
            }, 1000);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            Log.v(TAG, "in loader reset");
            // delete the current data
            mCursorAdapter.swapCursor(null);
        }

    }


}
