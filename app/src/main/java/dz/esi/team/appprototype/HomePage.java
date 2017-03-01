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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    public static final int LOAD_IMAGE_RESULT= 1;
    public static final String LOADED_IMAGE_PATH= "LOADED_IMAGE_PATH";
    private static final String TAG = "HomePage";
    public static final String ERROR_MESS = "Something went wrong";

    //image path
    private  String imagePath;

    //costum components
    private ListView plantListView;
    private NavigationView navigationView ;
    private Toolbar toolbar_search_access;
    private  ActionBarDrawerToggle toggle;

    //layouts
    LinearLayout  takeImageLayout;
    LinearLayout  importImageLayout;
    DrawerLayout  drawer;
    FrameLayout   optionMenuBackground;


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
                Log.d(TAG, "run: this is the first start "+isFirstStart);
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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);



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


            // buttons
        fabRecognise = (FloatingActionButton) findViewById(R.id.fab_recognition);
        fabImportImage=(FloatingActionButton) findViewById(R.id.fab_import_picture);
        fabTakeImage=(FloatingActionButton) findViewById(R.id.fab_take_picture);

            //labels
        importImageLabel = (TextView)findViewById(R.id.label_import_picture) ;
        takeImageLabel = (TextView)findViewById(R.id.label_take_picture) ;


        //handling events
            //costum components
        navigationView.setNavigationItemSelectedListener(this);
        setSupportActionBar(toolbar_search_access);

            //layout
        optionMenuBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(takeImageLayout.getVisibility()==View.VISIBLE && importImageLayout.getVisibility()==View.VISIBLE) {
                    hideOptionMenu();
                }
            }
        });



        //data handling
        // loading medical plant list view in the background
        loadMedicalPlantsListView("all");



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
                Cursor cursor = getContentResolver().query(pickedImage,filePath,null,null,null);
                cursor.moveToFirst();
                this.imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

                // stating the image recognition activity
                /*Intent intent = new Intent(HomePage.this,ImageProcess.class);
                intent.putExtra(LOADED_IMAGE_PATH,this.imagePath);
                startActivity(intent);
                */
            }else{
                Toast.makeText(HomePage.this,ERROR_MESS,Toast.LENGTH_SHORT).show();
            }
        }

    }

    // pressing the back button event handling
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
             drawer.closeDrawer(GravityCompat.START);
        } else if(takeImageLayout.getVisibility()==View.VISIBLE && importImageLayout.getVisibility()==View.VISIBLE){
                hideOptionMenu();
        }else   super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: creating the option menu");
        getMenuInflater().inflate(R.menu.search_menu,menu);
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        return super.onCreateOptionsMenu(menu);
    }

    //slecting a navigation item from the menu drawer event handling
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.content_search_menu) {
            // Handle the home action
            Log.d(TAG, "onNavigationItemSelected: navigate to the home activity");
        } else if (id == R.id.nav_preferences) {
            Log.d(TAG, "onNavigationItemSelected: navigate to the preferences activity ");
        } else if (id == R.id.nav_aboutus) {
            Log.d(TAG, "onNavigationItemSelected: navigate to the about us activity ");
        } else if (id == R.id.nav_user_guide) {
            Log.d(TAG, "onNavigationItemSelected: navigate to the user guide activity");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        Log.d(TAG, "onOptionsItemSelected:  "+item.getItemId()+" "+R.id.toolbar_search_access);

        int id = item.getItemId();

        // stating the searh activity
        if (id == R.id.app_search_bar) {
            intent = new Intent(HomePage.this,SearchPlantesActivity.class);
            startActivity(intent);
            return true ;
        }

         return toggle.onOptionsItemSelected(item);
    }




    // one of the floating action button is being clicked
    // this method had been implemented in the XML file content_main.xml and linked to each one of the three FAB 
    public void fabOnClick(View v) {
        Log.d(TAG, "fabOnClick: ");
        int id = v.getId();
        
        if (id == fabRecognise.getId()) {
            // the main FAB + is being clicked 
            if(takeImageLayout.getVisibility()==View.VISIBLE && importImageLayout.getVisibility()==View.VISIBLE) {
                hideOptionMenu();
            }else{
                showOptionMenu();
            }

        } else if (id == fabImportImage.getId()) {
            this.loadImageFromGallery();


        } else if (id == fabTakeImage.getId()) {
          this.phoneCameraAccess();
        }
    }

    // one of the label of floating action button is being clicked
    // this method had been implemented in the XML file content_main.xml and linked to each one of the three label of FAB 
    public void labelOnClick(View v){
        Log.d(TAG, "labelOnClick: ");

        int id= v.getId();

        if (id == importImageLabel.getId()) {
            this.loadImageFromGallery();
          

        } else if (id == takeImageLabel.getId()) {
            this.phoneCameraAccess();
        }

    }

    // this nested class extend AsyncTask , it use to load data in the background using multiThreding
    // after we implment the data base the signature will change to <String,Void,Cursor> the cursor will 
    // point to the first element of the data that match our search query sent from the search activity 
    private  class MedicalPlanesListViewHandler extends AsyncTask<String,Void,String>{
        
        @Override
        protected String doInBackground(String... params) {
            
            return "azeddine";
        }


        // SETTING THE ADAPTER
        @Override
        protected void onPostExecute(String s) {
            
            List<MedicalPlant> plantes = new ArrayList<>();
            MedicalPlant plante = new MedicalPlant("a3chabe","za3ter");
            plantes.add(plante);
            plante = new MedicalPlant("a3chabe","za3ter");
            plantes.add(plante);
            plante = new MedicalPlant("a3chabe","za3ter");
            plantes.add(plante);
            plante = new MedicalPlant("a3chabe","za3ter");
            plantes.add(plante);
            plante = new MedicalPlant("a3chabe","za3ter");
            plantes.add(plante);
            plante = new MedicalPlant("a3chabe","za3ter");
            plantes.add(plante);
           MedicalPlantsAdapter plantesAdapter = new MedicalPlantsAdapter(HomePage.this,R.layout.listview_plantes,plantes);
            plantListView.setAdapter(plantesAdapter);


        }
    }
    
    // accessing the phone gallery 
    private void loadImageFromGallery(){
        Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,LOAD_IMAGE_RESULT);
    }

    // accessing the phone Camera 
    private void phoneCameraAccess(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,LOAD_IMAGE_RESULT);
    }

    // optionMenu display handling
    private void hideOptionMenu(){
        Log.d(TAG, "hideOptionMenu: hiding the option menu");
        final FloatingActionButton fabRecognise = (FloatingActionButton) findViewById(R.id.fab_recognition);


        takeImageLayout = (LinearLayout) findViewById(R.id.layout_take_picture);
        importImageLayout = (LinearLayout) findViewById(R.id.layout_import_picture);


        final Animation rotateToPlus = AnimationUtils.loadAnimation(HomePage.this,R.anim.show_button_layout);
        final Animation hideOptionButton = AnimationUtils.loadAnimation(HomePage.this,R.anim.hide_buttons);


        takeImageLayout.setVisibility(View.GONE);
        importImageLayout.setVisibility(View.GONE);
        optionMenuBackground.setVisibility(View.GONE);


        takeImageLayout.startAnimation(hideOptionButton);
        importImageLayout.startAnimation(hideOptionButton);


        fabRecognise.startAnimation(rotateToPlus);

    }
    private  void showOptionMenu(){

        Log.d(TAG, "showOptionMenu: showing the option menu");

        final FloatingActionButton fabRecognise = (FloatingActionButton) findViewById(R.id.fab_recognition);
        final LinearLayout  takeImageLayout = (LinearLayout) findViewById(R.id.layout_take_picture);
        final LinearLayout  importImageLayout = (LinearLayout) findViewById(R.id.layout_import_picture);

        final Animation rotateToX = AnimationUtils.loadAnimation(HomePage.this,R.anim.hide_button_layout);
        final Animation showOptionButton = AnimationUtils.loadAnimation(HomePage.this,R.anim.show_buttons);


        takeImageLayout.setVisibility(View.VISIBLE);
        importImageLayout.setVisibility(View.VISIBLE);

        optionMenuBackground.setVisibility(View.VISIBLE);

        takeImageLayout.startAnimation(showOptionButton);
        importImageLayout.startAnimation(showOptionButton);


        fabRecognise.startAnimation(rotateToX);
    }
    
    // this method will recive the query of 
    // the search activity after the search field had being submitted

    /**
     * Created by azeddine on 25/02/17.
     */
    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: start");
        super.onResume();

        // accessing the shared prefrences file to get the query submitted by te user
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String queryResult = sharedPreferences.getString(PLANT_QUERY,"");

        // testing if the user had entered non empty query
        if(queryResult.length()>0) Toast.makeText(HomePage.this,queryResult,Toast.LENGTH_LONG).show();
    }

    // this method will recive the text query submitted by the user
    // call the sqlLite database helper to preform the search in the database
    // dispaly the medicalplantslistView from the backgrond using the AsyncTask

    /**
     * Created by azeddine on 27/02/17.
     */
    private void loadMedicalPlantsListView(String textQueryResult){
        MedicalPlanesListViewHandler medicalPlanesListViewHandler = new MedicalPlanesListViewHandler();
        medicalPlanesListViewHandler.execute("azeddine");

    }

}
