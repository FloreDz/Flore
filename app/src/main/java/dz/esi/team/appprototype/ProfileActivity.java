package dz.esi.team.appprototype;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;

import dz.esi.team.appprototype.data.PlantProfile;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ProfileActivity extends AppCompatActivity {

    static private PlantProfile profile = null;
    static private boolean PLANT_IMAGE_POPUP_STATE = false;
    private final String TAG = this.getClass().getSimpleName();
    private  SharedPreferences sharedPref ;


    // TODO : Mohamed aded :
    TextView sci_name;
    ImageView image;
    TextView noms;
    TextView famille;
    TextView resume;
    TextView constituants;
    TextView partiesUtilisees;
    TextView effets;
    TextView effetsSecondaires;
    TextView indications;
    TextView contreIndications;
    TextView preparation;
    TextView lieu;
    TextView interactions;
    TextView periodeRecolte;
    TextView remarques;
    TextView source;
    TextView liens;
    CollapsingToolbarLayout plantProfileAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (PLANT_IMAGE_POPUP_STATE) {
            Log.v(TAG, "about to show plant image");
            showPlantImagePopuop();
        }

        Log.v(TAG, "ACTIVITY CREATED");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);

        profile = new PlantProfile(getIntent().getLongExtra("PlantID", 0L));
        sharedPref=PreferenceManager.getDefaultSharedPreferences(this);


    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "ACTIVITY STARTED");
    }
    @Override
    protected void onResume() {
        super.onResume();

        Log.v(TAG, "ACTIVITY RESUMED");

        widgetsPopulation(profile);

        if (liens != null && liens.getText().toString().contains("http"))
            liens.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(liens.getText().toString()));
                    if (browserIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(browserIntent);
                    }
                }
            });

        if (source != null && source.getText().toString().contains("http"))
            source.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(source.getText().toString()));
                    if (browserIntent.resolveActivity(getPackageManager()) != null)
                        startActivity(browserIntent);
                }
            });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlantImagePopuop();
            }
        });

    }
    private void showPlantImagePopuop() {
        PLANT_IMAGE_POPUP_STATE = true;
        LayoutInflater inflater = ProfileActivity.this.getLayoutInflater();
        View imageDialog = inflater.inflate(R.layout.dialog_image, null);
        Dialog dialog = new Dialog(ProfileActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(imageDialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.d("ProfileActivty", "in on dismiss listener,about to check to false");
                PLANT_IMAGE_POPUP_STATE = false;
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView plantImageDialog = (ImageView) imageDialog.findViewById(R.id.dialog_plant_image);
        Glide.with(ProfileActivity.this)
                .load("file:///android_asset/images/" + profile.getImage())
                .placeholder(R.drawable.loading)
                .crossFade()
                .into(plantImageDialog);

        dialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "ACTIVITY PAUSED");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "ACTIVITY STOPPED");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG, "ACTIVITY RESTARTED");
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        PLANT_IMAGE_POPUP_STATE = savedInstanceState.getBoolean("PLANT_IMAGE_POPUP_STATE");
        super.onRestoreInstanceState(savedInstanceState);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("PLANT_IMAGE_POPUP_STATE", PLANT_IMAGE_POPUP_STATE);
        super.onSaveInstanceState(outState);

    }

    private  void  setField(TextView textView,String key,int id,String value,int containerId){

        boolean state = sharedPref.getBoolean(key,true);
        if(state){
            textView = (TextView) findViewById(id);
            textView.setText(value);
        }else{
            findViewById(containerId).setVisibility(View.GONE);
        }

    }
    private void widgetsPopulation(PlantProfile profile) {

        Log.v("PlantProfile", "in widgets population");

        plantProfileAppBar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout_profile);
        plantProfileAppBar.setTitle(profile.getSci_name());


        sci_name = (TextView) findViewById(R.id.plant_name_sci);
        sci_name.setText(profile.getSci_name());

        noms = (TextView) findViewById(R.id.plant_noms);
        noms.setText(profile.getNom());


        famille = (TextView) findViewById(R.id.plant_family);
        famille.setText("Famille : "+profile.getFamille());



        resume = (TextView) findViewById(R.id.plant_resume);
        resume.setText(profile.getResume());

        effets = (TextView) findViewById(R.id.plant_effects);
        effets.setText(profile.getEffets());




        setField(constituants,"CONSTITUANTS",R.id.plant_constituents,profile.getConstituants(),R.id.container_constituants);
        setField(partiesUtilisees,"PARTIES_UTILISEES",R.id.plant_used_parts,profile.getPartiesUtilisees(),R.id.container_parties_utilisees);
        setField(effetsSecondaires,"LES_EFFETS_SECONDAIRE",R.id.plant_2nd_effects,profile.getEffetsSecondaires(),R.id.container_effets_secondaires);
        setField(indications,"LES_INDECATION",R.id.plant_indications,profile.getIndications(),R.id.container_indications);
        setField(contreIndications,"LES_CONTRE_INDECATION",R.id.plant_contreIndications,profile.getContreIndication(),R.id.container_contre_indications);
        setField(preparation,"LES_PREPARATION",R.id.plant_preparation,profile.getContreIndication(),R.id.container_preparation);
        setField(lieu,"HABITAT",R.id.plant_habitat,profile.getLieu(),R.id.container_habitat);
        setField(periodeRecolte,"TEMPS_DE_RECOLTE",R.id.plant_harvest,profile.getPeriodeRecolte(),R.id.container_temps_recolte);
        setField(remarques,"LES_REMARQUES",R.id.plant_remarks,profile.getRemarques(),R.id.container_remarks);
        setField(interactions,"LES_INTERACTION",R.id.plant_interactions,profile.getInteraction(),R.id.container_interactions);



        if(profile.getSource() == null ){
            findViewById(R.id.container_source).setVisibility(View.GONE);
        }else{
            source = (TextView) findViewById(R.id.plant_source);
            source.setText(profile.getSource());
        }
        
        if (profile.getLiens() == null ) {
            findViewById(R.id.container_liens).setVisibility(View.GONE);
        }else {
            setField((TextView) findViewById(R.id.plant_liens),"LES_LIENS",R.id.plant_liens,profile.getLiens(),R.id.container_liens);
        }
        image = (ImageView) findViewById(R.id.plant_image);
        Glide.with(this)
                .load("file:///android_asset/thumbnails/" + profile.getImage())
                .asBitmap()
                .transform(new RoundedCornersTransformation(this, 20, 0))
                 .override(300, 200)
                .into(image);

    }
}


