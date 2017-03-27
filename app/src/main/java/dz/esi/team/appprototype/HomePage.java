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
import java.util.Collections;
import java.util.Comparator;
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
    public static final String DISPLAY_TYPE = "DISPLAY_TYPE";
    private static final String TAG = HomePage.class.getSimpleName();
    private static final String ERROR_MESS = "Something went wrong";
    private static final String SHOW_PLANTS_DEFAULT = "SHOW_PLANTS_DEFAULT";
    private static final String SHOW_PLANTS_BY_FAMILIES = "SHOW_PLANTS_BY_FAMILIRS";
    public static String DISPLAY_STATE = SHOW_PLANTS_DEFAULT;

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

    //image path
    private String imagePath;

    //costum components
    private ListView plantListView;
    private NavigationView navigationView;
    private Toolbar toolbar_search_access;
    private ActionBarDrawerToggle toggle;
    private Menu optionMenu;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        introPageHandler();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        widgetHydration();

        // data handling
        // loading medical plant list view in the background
        loadMedicalPlantsListView(DISPLAY_STATE);

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
    }

    public void widgetHydration() { // widgets hydration

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
        getMenuInflater().inflate(R.menu.search_menu, menu);
        menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        if(DISPLAY_STATE.equals(SHOW_PLANTS_BY_FAMILIES))menu.getItem(1).setChecked(true);
        this.optionMenu = menu;

        return super.onCreateOptionsMenu(menu);
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
            intent = new Intent(HomePage.this,HelpActivity.class);
            startActivity(intent);
        }
        if (isVisible()) hideOptionMenu();
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
        MenuItem optionMenuItem;
        int id = item.getItemId();
        // stating the searh activity
        switch (id) {
            case R.id.app_search_bar:
                intent = new Intent(HomePage.this,SearchPlantesActivity.class);
                startActivity(intent);
                break;
            case R.id.option_show_by_familly:
                this.setDisplayState(item, item.isChecked());
                break;
            case R.id.mohamed:   // ADDED BY MOHAMED
                Log.v(TAG, "about to go to mohamed activity");
                startActivity(new Intent(HomePage.this, DatabaseTest.class));
                Log.v(TAG, "just went to mohamed activity");
                break;
            default:
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

        } else if (isVisible()) {

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

    // optionMenu display handling

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

        takeImageLayout.setVisibility(View.VISIBLE);
        importImageLayout.setVisibility(View.VISIBLE);
        optionMenuBackground.setVisibility(View.VISIBLE);

    }

    public void disableLayoutsVisibility() {

        takeImageLayout.setVisibility(View.GONE);
        importImageLayout.setVisibility(View.GONE);
        optionMenuBackground.setVisibility(View.GONE);
    }

    public boolean isVisible() {
        if (takeImageLayout.getVisibility() == View.VISIBLE && importImageLayout.getVisibility() == View.VISIBLE) {
            return true;
        } else {
            return false;
        }
    }

    public void setDisplayState(MenuItem item, boolean state) {
        if (state) {

            item.setChecked(false);
            loadMedicalPlantsListView(SHOW_PLANTS_DEFAULT);
            DISPLAY_STATE = SHOW_PLANTS_DEFAULT;

        } else {

            item.setChecked(true);
            loadMedicalPlantsListView(SHOW_PLANTS_BY_FAMILIES);
            DISPLAY_STATE = SHOW_PLANTS_BY_FAMILIES;
        }

    }

    private void loadMedicalPlantsListView(String textQueryResult) {
        // this method will recieve the text query submitted by the user
        // call the sqlLite database helper to preform the search in the database
        // dispaly the medicalplantslistView from the backgrond using the AsyncTask

        MedicalPlanesListViewHandler medicalPlanesListViewHandler = new MedicalPlanesListViewHandler();
        medicalPlanesListViewHandler.execute(textQueryResult);

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


    private class MedicalPlanesListViewHandler extends AsyncTask<String, Void,
            List<Section>> implements AdapterView.OnItemClickListener {
        // this nested class extend AsyncTask , it use to load data in the background using multiThreding
        // after we implement the data base the signature will change to <String,Void,Cursor> the cursor will
        // point to the first element of the data that match our search query sent from the search activity

        @Override
        protected List<Section> doInBackground(String... params) {

            String option = params[0];

            List<MedicalPlant> medicalPlantsList = new ArrayList<>();
            List<MedicalPlantsFamily> medicalPlantsFamilyList = new ArrayList<>();


            /************************************************************************************************/
            MedicalPlant plant = new MedicalPlant("Allium sativum L", R.mipmap.ail, "medical plants family");
            medicalPlantsList.add(plant);
            medicalPlantsList.add(plant);
            medicalPlantsList.add(plant);
            medicalPlantsList.add(plant);

            MedicalPlantsFamily family = new MedicalPlantsFamily("medical plantes family 1", medicalPlantsList);
            medicalPlantsFamilyList.add(family);

            medicalPlantsList.add(plant);

            family = new MedicalPlantsFamily("Aedical plantes family 1", medicalPlantsList);
            medicalPlantsFamilyList.add(family);

            medicalPlantsList.add(plant);
            family = new MedicalPlantsFamily("medical plantes family 1", medicalPlantsList);
            medicalPlantsFamilyList.add(family);
            /***********************************************************************************************/

            for (MedicalPlantsFamily medicalPlantsFamily : medicalPlantsFamilyList) {
                Collections.sort(medicalPlantsFamily.getMedicalPlantList(), new Comparator<MedicalPlant>() {
                    @Override
                    public int compare(MedicalPlant o1, MedicalPlant o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
            }

            return spreadFamilyList(medicalPlantsFamilyList, option);


        }

        @Override
        protected void onPostExecute(List<Section> listViewSections) {

            MedicalPlantsAdapter plantsAdapter = new MedicalPlantsAdapter(HomePage.this,
                    R.layout.listview_plantes, R.layout.plants_family_header, listViewSections);
            plantListView.setAdapter(plantsAdapter);
            plantListView.setOnItemClickListener(this);
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "onItemClick: the item with the position " + position + " and the id" + id);

            String remarque = ((ViewHolder) view.getTag()).getTitle().getText().toString();
            Toast.makeText(HomePage.this, remarque, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(HomePage.this, ProfileActivity.class);
            startActivity(intent);
        }

        private List<Section> spreadFamilyList(List<MedicalPlantsFamily> medicalPlantsFamilyList, String option) {
            List<MedicalPlant> medicalPlantList;
            List<Section> updatedList = new ArrayList<>();

            if (option.equals(SHOW_PLANTS_BY_FAMILIES)) {
                Collections.sort(medicalPlantsFamilyList, new Comparator<MedicalPlantsFamily>() {
                    @Override
                    public int compare(MedicalPlantsFamily o1, MedicalPlantsFamily o2) {
                        return o1.getMedicalPlantsFamilyName().compareTo(o2.getMedicalPlantsFamilyName());
                    }
                });
            }


            for (MedicalPlantsFamily family : medicalPlantsFamilyList) {
                if (option.equals(SHOW_PLANTS_BY_FAMILIES)) updatedList.add(family);
                medicalPlantList = family.getMedicalPlantList();
                for (MedicalPlant medicalPlant : medicalPlantList) {
                    updatedList.add(medicalPlant);
                }
            }


            return updatedList;
        }

    }


}
