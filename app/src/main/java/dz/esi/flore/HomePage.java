package dz.esi.flore;

import android.animation.Animator;
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
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
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

import dz.esi.flore.data.PlantCursorAdapter;
import dz.esi.flore.data.PlantRetriever;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static dz.esi.flore.data.PlantContract.PlantEntry.CONTENT_URI;
import static dz.esi.flore.data.PlantContract.PlantEntry._ID;
import static dz.esi.flore.data.PlantContract.PlantEntry.famille;
import static dz.esi.flore.data.PlantContract.PlantEntry.sci_name;


public class HomePage extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String FEEDBACK_FORM_URL = "https://goo.gl/IMDiRB";
    private static final int LOAD_IMAGE_RESULT = 1;
    private static final String LOADED_IMAGE_PATH = "LOADED_IMAGE_PATH";
    private static final String LOADED_IMAGE_URI = "LOADED_IMAGE_URI";
    private static final String DISPLAY_TYPE = "DISPLAY_TYPE";
    private static final String TAG = HomePage.class.getSimpleName();
    private static final String ERROR_MESS = "something went wrong";
    private static final int PLANT_LOADER = 0;

    private  SharedPreferences sharedPref ;
    private static String DISPLAY_STATE = SHOW_PLANTS_BY_FAMILIES;

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
            famille
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        introPageHandler();
        if(savedInstanceState == null) {
            sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            if (sharedPref.getString("DISPLAY_STATE","0").compareTo("0") == 0)
                DISPLAY_STATE=SHOW_PLANTS_BY_SCIENTIFIQUE_NAMES;
            else
                DISPLAY_STATE=SHOW_PLANTS_BY_FAMILIES ;
        }
        super.onCreate(savedInstanceState);
        Log.d("HomePage", "ACTIVITY CREATED");
        setContentView(R.layout.activity_home_page);
        setupWindowAnimations();
        widgetHydration();
        navigationView.setNavigationItemSelectedListener(this);
        setSupportActionBar(toolbar_search_access);

        optionMenuBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVisible()) {
                    hideOptionMenu();
                }
            }
        });
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
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
        
       initDatabase(this);

        initializePlantsHeaders();

        listViewLoader = new ListViewLoader(progressBar,true);
        listViewLoader.execute();
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.v("HomePage", "ACTIVITY STOPPED");
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onStop: my db helper =?= null  "+(mDbHelper == null ) );
        mDbHelper.close();
        Log.d(TAG, "onStop: my db helper =?= null  "+(mDbHelper == null ) );
        super.onDestroy();
    }

    private void switchDisplayState() {
        String displayMessage;
        if (DISPLAY_STATE.equals(SHOW_PLANTS_BY_FAMILIES)) {
            DISPLAY_STATE = SHOW_PLANTS_BY_SCIENTIFIQUE_NAMES;
            displayMessage = "Affichage par noms scientifiques";
        } else {
            DISPLAY_STATE = SHOW_PLANTS_BY_FAMILIES;
            displayMessage = "Affichage par familles";
        }
        Toast.makeText(this, displayMessage, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "display state  changed to : " + DISPLAY_STATE);
    }

    private void setupWindowAnimations() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "Into LOLLIPOP");
            getWindow().setEnterTransition(new Fade().setDuration(1000));
            getWindow().setExitTransition(new Fade().setDuration(1000));
        }
    }

    private void initializePlantsHeaders() {
        String[] projection = {famille, sci_name};
        String currentFamily = null;
        String nextFamily = null;
        Cursor cursor = null;

        try {
            Log.d(TAG, "just got into try");
            plantsHeaders = new ArrayList<>();
            Log.d(TAG, "beginning : ArrayList size = " + plantsHeaders.size());
            cursor = PlantRetriever.RetrievePlants(projection, null, null, SHOW_PLANTS_BY_FAMILIES);
            Log.d(TAG, "about to loop");

            if (cursor.moveToFirst()) {
                currentFamily = cursor.getString(0);
                plantsHeaders.add(cursor.getString(1));
                Log.d(TAG, "in if ,added in ArrayList : " + cursor.getString(1));
            }

            while (cursor.moveToNext()) {
                nextFamily = cursor.getString(0);
                if (!currentFamily.equals(nextFamily)) {
                    plantsHeaders.add(cursor.getString(1));
                    currentFamily = nextFamily;
                    Log.d(TAG, "in while ,added in ArrayList : " + cursor.getString(1));
                }
            }
            Log.d(TAG, "end : ArrayList size = " + plantsHeaders.size());
        } finally {
            if (cursor != null) cursor.close();
        }
        Log.d(TAG, "Finally : ArrayList = " + plantsHeaders.toString());

    }

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
            startActivity(new Intent(HomePage.this, SettingsActivity.class));
        } else if (id == R.id.nav_aboutus) {
            startActivity(new Intent(HomePage.this, AboutUs.class));
        } else if (id == R.id.nav_user_guide) {
            intent = new Intent(HomePage.this, HelpActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_feedback){

            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
            browserIntent.setData(Uri.parse(FEEDBACK_FORM_URL));
            if (browserIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(browserIntent);
            }
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
        final Animation hideOptionButtons = AnimationUtils.loadAnimation(HomePage.this, R.anim.fade_out_with_translation);
        final Animation hideOptionMenuBackground = AnimationUtils.loadAnimation(HomePage.this, R.anim.fade_out);
        rotateCamera.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                switchFabImageTo(fabRecognise, R.drawable.camera_fab);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });



        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            // get the center for the clipping circle
            int centerX =optionMenuBackground.getRight();
            int centerY = optionMenuBackground.getBottom();
            // get the final radius for the clipping circle
            int startRadius = (int) Math
                    .hypot(optionMenuBackground.getWidth(), optionMenuBackground.getHeight());
            int endRadius = 0;
            // create the animator for this view (the start radius is zero)
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(optionMenuBackground, centerX, centerY, startRadius, endRadius);
            // make the view visible and start the animation
            anim.setDuration(400);
           anim.addListener(new Animator.AnimatorListener() {
               @Override
               public void onAnimationStart(Animator animation) {
                   disableClickable();
                   takeImageLayout.startAnimation(hideOptionButtons);
                   importImageLayout.startAnimation(hideOptionButtons);
                   fabRecognise.startAnimation(rotateCamera);

               }

               @Override
               public void onAnimationEnd(Animator animation) {
                   disableLayoutsVisibility();

               }

               @Override
               public void onAnimationCancel(Animator animation) {

               }

               @Override
               public void onAnimationRepeat(Animator animation) {

               }
           });

            anim.start();
        }else {
            takeImageLayout.startAnimation(hideOptionButtons);
            importImageLayout.startAnimation(hideOptionButtons);
            optionMenuBackground.startAnimation(hideOptionMenuBackground);
            fabRecognise.startAnimation(rotateCamera);
            this.disableLayoutsVisibility();
            this.disableClickable();
        }


    }

    private void showOptionMenu() {
        Log.d(TAG, "showOptionMenu: showing the option menu");

        final Animation rotateCamera = AnimationUtils.loadAnimation(HomePage.this, R.anim.rotate_to_x);
        final Animation showOptionButtons = AnimationUtils.loadAnimation(HomePage.this, R.anim.fade_in_with_translation);
        final Animation showOptionMenuBackground = AnimationUtils.loadAnimation(HomePage.this, R.anim.fade_in);
        rotateCamera.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                switchFabImageTo(fabRecognise, R.drawable.plus_button);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            // get the center for the clipping circle
            int centerX = optionMenuBackground.getRight();
            int centerY = optionMenuBackground.getBottom();
            // get the final radius for the clipping circle
            int startRadius = 0;
            int endRadius = (int) Math
                    .hypot(optionMenuBackground.getWidth(), optionMenuBackground.getHeight());
            // create the animator for this view (the start radius is zero)
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(optionMenuBackground, centerX, centerY, startRadius, endRadius);
            // make the view visible and start the animation


            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    enableLayoutsVisibility();
                    takeImageLayout.startAnimation(showOptionButtons);
                    importImageLayout.startAnimation(showOptionButtons);
                    fabRecognise.startAnimation(rotateCamera);

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    enableClickable();

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            anim.setDuration(700);
            anim.start();

        }else{
            enableLayoutsVisibility();
            enableClickable();
            optionMenuBackground.startAnimation(showOptionMenuBackground);
            takeImageLayout.startAnimation(showOptionButtons);
            importImageLayout.startAnimation(showOptionButtons);
            fabRecognise.startAnimation(rotateCamera);

        }



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
        optionMenuBackground.setVisibility(View.VISIBLE);


    }

    public void disableLayoutsVisibility() {

        takeImageLayout.setVisibility(GONE);
        importImageLayout.setVisibility(GONE);
        optionMenuBackground.setVisibility(GONE);

    }

    public boolean isVisible() {
        return (takeImageLayout.getVisibility() == VISIBLE && importImageLayout.getVisibility() == VISIBLE);
    }

    public void doNothing(View view){
        // ignoring the familly header clicks
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

            if (firstRun) {
                this.progressBar.setVisibility(VISIBLE);
                Log.d(TAG, "about to init loader");
                getLoaderManager().initLoader(PLANT_LOADER, null, this);
                Log.d(TAG, "loader inited");
                firstRun = false;
            } else {
                mCursorAdapter.setDISPLAY_STATE(DISPLAY_STATE);
                final Cursor cursor = PlantRetriever.RetrievePlants(homeMenuProjection, null, null, DISPLAY_STATE);
                final Animation showListView = AnimationUtils.loadAnimation(HomePage.this, R.anim.fade_in_with_translation);
                mCursorAdapter.swapCursor(cursor);
                plantsListView.setAdapter(mCursorAdapter);
                plantListView.startAnimation(showListView);
            }
        }



        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            Log.v(TAG, "in loader creation");

            // the loader will executeSearchQuery the CP query method on a background thread
            return new CursorLoader(HomePage.this, CONTENT_URI, homeMenuProjection, null, null, DISPLAY_STATE);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            final Cursor cur = cursor;
            final Animation showListView = AnimationUtils.loadAnimation(HomePage.this, R.anim.fade_in_with_translation);
            Log.v(TAG, "in loader finish");
            // update the adapter with this new cursor containing updated plant data
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    listViewLoader.progressBar.setVisibility(GONE);
                    mCursorAdapter.swapCursor(cur);
                    plantsListView.setAdapter(mCursorAdapter);
                    plantListView.startAnimation(showListView);
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
/**
 * last verification 28/04/2017
 */
