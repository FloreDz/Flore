package dz.esi.team.appprototype;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dz.esi.team.appprototype.utils.MedicalPlant;
import dz.esi.team.appprototype.utils.MedicalPlantsAdapter;
import dz.esi.team.appprototype.utils.MedicalPlantsFamily;
import dz.esi.team.appprototype.utils.Section;
import dz.esi.team.appprototype.utils.ViewHolder;

public class HomePage extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int LOAD_IMAGE_RESULT = 1;
    public static final String LOADED_IMAGE_PATH = "LOADED_IMAGE_PATH";
    public static final String LOADED_IMAGE_URI = "LOADED_IMAGE_URI";
    private static final String TAG = "HomePage";
    public static final String ERROR_MESS = "Something went wrong";

    //image path
    private String imagePath;

    //costum components
    private ListView plantListView;
    private NavigationView navigationView;
    private Toolbar toolbar_search_access;
    private ActionBarDrawerToggle toggle;
    private SwipeRefreshLayout swipeRefreshLayout;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");


        introPageHandler();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        widgetHydration();


        //data handling
        // loading medical plant list view in the background
        loadMedicalPlantsListView("all");

        //handling events
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

       /* swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        swipeRefreshLayout.setColorSchemeResources(R.color.logo_green_light);
                        Toast.makeText(HomePage.this, "refresh", Toast.LENGTH_LONG).show();
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.

                    }

                }
        );*/




    }

    public void widgetHydration() {
        // widgets hydration

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
        drawer.setDrawerListener(toggle);
        toggle.syncState();
      //  swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);


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
        //  Declare a new thread to do a preference check
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
        getMenuInflater().inflate(R.menu.search_menu, menu);
            menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return super.onCreateOptionsMenu(menu);
    }

    //slecting a navigation item from the menu drawer event handling
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent intent  ;
        int id = item.getItemId();

       if (id == R.id.nav_preferences) {

        } else if (id == R.id.nav_aboutus) {

        } else if (id == R.id.nav_user_guide) {

        }
        if(isVisible()) hideOptionMenu();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Intent intent;

        int id = item.getItemId();
        // stating the searh activity
        if (id == R.id.app_search_bar) {
            intent = new Intent(HomePage.this, SearchPlantesActivity.class);
            startActivity(intent);
            return true;
        }

        return toggle.onOptionsItemSelected(item);
    }


    public void fabOnClick(View v) {
        // one of the floating action button is being clicked
        // this method had been implemented in the XML file content_main.xml and linked to each one of the three FAB
        Log.d(TAG, "fabOnClick: ");
        int id = v.getId();

        if (id == fabRecognise.getId()) {
            // the main FAB + is being clicked 
            if (isVisible()) {
                hideOptionMenu();
            } else {
                showOptionMenu();
            }

        }else  if (isVisible()){

            if (id == fabImportImage.getId()) {
                this.hideOptionMenu();
                this.loadImageFromGallery();


            } else if (id == fabTakeImage.getId()) {
                this.hideOptionMenu();
                this.phoneCameraAccess();
                ImageView imageView;

            }
        }
    }


    public void labelOnClick(View v) {
        // one of the label of floating action button is being clicked
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


    private class MedicalPlanesListViewHandler extends AsyncTask<String, Void, String> implements AdapterView.OnItemClickListener {
        // this nested class extend AsyncTask , it use to load data in the background using multiThreding
        // after we implement the data base the signature will change to <String,Void,Cursor> the cursor will
        // point to the first element of the data that match our search query sent from the search activity

        @Override
        protected String doInBackground(String... params) {

            return "azeddine";
        }


        @Override
        protected void onPostExecute(String s) {

            List<Section> sectionArrayList  = new ArrayList<>();
            Section family = new MedicalPlantsFamily("medical plantes family 1");
            sectionArrayList.add(family);
            Section plant  = new MedicalPlant((MedicalPlantsFamily) family,"paln 1 ");
            sectionArrayList.add(plant);
             plant  = new MedicalPlant((MedicalPlantsFamily) family,"paln 2 ");
            sectionArrayList.add(plant);
            plant  = new MedicalPlant((MedicalPlantsFamily) family,"paln 3 ");
            sectionArrayList.add(plant);
            family = new MedicalPlantsFamily("medical plantes family 2");
            sectionArrayList.add(family);
            plant  = new MedicalPlant((MedicalPlantsFamily) family,"palnt 1 ");
            sectionArrayList.add(plant);
            plant  = new MedicalPlant((MedicalPlantsFamily) family,"palnt 2 ");
            sectionArrayList.add(plant);
            plant  = new MedicalPlant((MedicalPlantsFamily) family,"palnt 3 ");
            sectionArrayList.add(plant);


            MedicalPlantsAdapter plantsAdapter = new MedicalPlantsAdapter(HomePage.this,R.layout.listview_plantes,R.layout.plants_family_header,sectionArrayList);
            plantListView.setAdapter(plantsAdapter);
            plantListView.setOnItemClickListener(this);
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "onItemClick: the item with the position " + position + " and the id" + id);

            String remarque = ((ViewHolder) view.getTag()).getTitle().getText().toString();
            Toast.makeText(HomePage.this, remarque, Toast.LENGTH_LONG).show();
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

    // optionMenu display handling
    private void hideOptionMenu() {
        Log.d(TAG, "hideOptionMenu: hiding the option menu");
        final FloatingActionButton fabRecognise = (FloatingActionButton) findViewById(R.id.fab_recognition);


        takeImageLayout = (LinearLayout) findViewById(R.id.layout_take_picture);
        importImageLayout = (LinearLayout) findViewById(R.id.layout_import_picture);


        final Animation rotateToPlus = AnimationUtils.loadAnimation(HomePage.this, R.anim.show_button_layout);
        final Animation hideOptionButton = AnimationUtils.loadAnimation(HomePage.this, R.anim.hide_buttons);



        this.disableLayoutsVisibility();
        this.disableClickable();

        takeImageLayout.startAnimation(hideOptionButton);
        importImageLayout.startAnimation(hideOptionButton);


        fabRecognise.startAnimation(rotateToPlus);

    }

    private void showOptionMenu() {

        Log.d(TAG, "showOptionMenu: showing the option menu");

        final FloatingActionButton fabRecognise = (FloatingActionButton) findViewById(R.id.fab_recognition);
        final LinearLayout takeImageLayout = (LinearLayout) findViewById(R.id.layout_take_picture);
        final LinearLayout importImageLayout = (LinearLayout) findViewById(R.id.layout_import_picture);

        final Animation rotateToX = AnimationUtils.loadAnimation(HomePage.this, R.anim.hide_button_layout);
        final Animation showOptionButton = AnimationUtils.loadAnimation(HomePage.this, R.anim.show_buttons);



        this.enableLayoutsVisibility();
        this.enableClickable();

        takeImageLayout.startAnimation(showOptionButton);
        importImageLayout.startAnimation(showOptionButton);


        fabRecognise.startAnimation(rotateToX);
    }

    public void disableClickable(){

        importImageLayout.setClickable(false);
        takeImageLayout.setClickable(false);
        importImageLabel.setClickable(false);
        takeImageLabel.setClickable(false);
        fabImportImage.setClickable(false);
        fabTakeImage.setClickable(false);
    }

    public  void enableClickable(){

        importImageLayout.setClickable(true);
        takeImageLayout.setClickable(true);
        importImageLabel.setClickable(true);
        takeImageLabel.setClickable(true);
        fabImportImage.setClickable(true);
        fabTakeImage.setClickable(true);

    }

    public  void enableLayoutsVisibility(){

        takeImageLayout.setVisibility(View.VISIBLE);
        importImageLayout.setVisibility(View.VISIBLE);
        optionMenuBackground.setVisibility(View.VISIBLE);

    }

    public  void disableLayoutsVisibility(){

        takeImageLayout.setVisibility(View.GONE);
        importImageLayout.setVisibility(View.GONE);
        optionMenuBackground.setVisibility(View.GONE);
    }

    public boolean isVisible(){
        if (takeImageLayout.getVisibility() == View.VISIBLE && importImageLayout.getVisibility() == View.VISIBLE) {
            return true ;
        } else {
            return false ;
        }
    }

    /**
     * Created by azeddine on 25/02/17.
     */

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: start");
        super.onResume();

        // accessing the shared prefrences file to get the query submitted by te user
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String queryResult = sharedPreferences.getString(PLANT_QUERY, "");

        // testing if the user had entered non empty query
        if (queryResult.length() > 0)
            Toast.makeText(HomePage.this, queryResult, Toast.LENGTH_LONG).show();
    }


    /**
     * Created by azeddine on 27/02/17.
     */
    private void loadMedicalPlantsListView(String textQueryResult) {
        // this method will recive the text query submitted by the user
        // call the sqlLite database helper to preform the search in the database
        // dispaly the medicalplantslistView from the backgrond using the AsyncTask

        MedicalPlanesListViewHandler medicalPlanesListViewHandler = new MedicalPlanesListViewHandler();
        medicalPlanesListViewHandler.execute("azeddine");

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

                // stating the ImageOptionsActivity activity
                Intent intent = new Intent(HomePage.this, ImageOptionsActivity.class);
                intent.putExtra(LOADED_IMAGE_PATH, this.imagePath);
                intent.putExtra(LOADED_IMAGE_URI, pickedImage.toString());
                startActivity(intent);

            } else {
                Toast.makeText(HomePage.this, ERROR_MESS, Toast.LENGTH_SHORT).show();
            }
        }

    }


}
