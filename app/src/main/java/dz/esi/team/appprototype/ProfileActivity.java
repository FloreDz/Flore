package dz.esi.team.appprototype;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
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

import dz.esi.team.appprototype.data.PlantProfile;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ProfileActivity extends AppCompatActivity {

    static private PlantProfile profile = null;
    TextView collapseConstituent;
    LinearLayout Constituents;
    // TODO : Mohamed aded :
    TextView sci_name;
    ImageView image;
    TextView noms;
    TextView famille;
    TextView resume;
    TextView constituants;
    TextView partiesUtilitees;
    TextView effets;
    TextView effetsSecondaires;
    TextView indications;
    TextView contreIndications;
    TextView interaction;
    TextView preparation;
    TextView lieu;
    TextView periodeRecolte;
    TextView remarques;
    TextView source;
    TextView liens;
    CollapsingToolbarLayout plantProfileAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Log.v("TEXT", "ACTIVITY CREATED");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);

        profile = new PlantProfile(getIntent().getLongExtra("PlantID", 0L));


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v("TEXT", "ACTIVITY STARTED");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.v("TEXT", "ACTIVITY RESUMED");

        widgetsPopulation(profile);

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
                LayoutInflater inflater = ProfileActivity.this.getLayoutInflater();
                View imageDialog = inflater.inflate(R.layout.dialog_image, null);
                Dialog dialog = new Dialog(ProfileActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(imageDialog);
                dialog.setCanceledOnTouchOutside(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                TextView plantNameDialog = (TextView) imageDialog.findViewById(R.id.dialog_plant_name);
                ImageView plantImageDialog = (ImageView) imageDialog.findViewById(R.id.dialog_plant_image);

                plantNameDialog.setText(profile.getSci_name());
                Glide.with(ProfileActivity.this)
                        .load("file:///android_asset/images/" + profile.getImage())
                        .placeholder(R.drawable.loading)
                        .crossFade()
                        .into(plantImageDialog);

                dialog.show();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("TEXT", "ACTIVITY PAUSEED");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("TEXT", "ACTIVITY STOPPED");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v("TEXT", "ACTIVITY RESTARTED");
    }


    public void onClickConstituents(View v) {
        if (v == collapseConstituent) {
            Constituents.setVisibility( (Constituents.getVisibility() == VISIBLE) ? GONE : VISIBLE);
        }
    }

    private void widgetsPopulation(PlantProfile profile) {

        Log.v("PlantProfile", "in widgets population");

        plantProfileAppBar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout_profile);
        sci_name = (TextView) findViewById(R.id.plant_name_sci);
        noms = (TextView) findViewById(R.id.plant_noms);
        famille = (TextView) findViewById(R.id.plant_family);
        resume = (TextView) findViewById(R.id.plant_resume);
        constituants = (TextView) findViewById(R.id.plant_constituents);
        partiesUtilitees = (TextView) findViewById(R.id.plant_used_parts);
        effets = (TextView) findViewById(R.id.plant_effects);
        effetsSecondaires = (TextView) findViewById(R.id.plant_2nd_effects);
        indications = (TextView) findViewById(R.id.plant_interactions);
        contreIndications = (TextView) findViewById(R.id.plant_contreIndications);
        interaction = (TextView) findViewById(R.id.plant_interactions);
        preparation = (TextView) findViewById(R.id.plant_preparation);
        lieu = (TextView) findViewById(R.id.plant_habitat);
        periodeRecolte = (TextView) findViewById(R.id.plant_harvest);
        remarques = (TextView) findViewById(R.id.plant_remarks);
        source = (TextView) findViewById(R.id.plant_source);
        liens = (TextView) findViewById(R.id.plant_liens);
        image = (ImageView) findViewById(R.id.plant_image);

        plantProfileAppBar.setTitle(profile.getSci_name());
        sci_name.setText(profile.getSci_name());
        noms.setText(profile.getNom());
        famille.setText(profile.getFamille());
        resume.setText(profile.getResume());
        constituants.setText(profile.getConstituants());
        partiesUtilitees.setText(profile.getPartiesUtilitees());
        effets.setText(profile.getEffets());
        effetsSecondaires.setText(profile.getEffetsSecondaires());
        indications.setText(profile.getIndications());
        contreIndications.setText(profile.getContreIndication());
        interaction.setText(profile.getInteraction());
        preparation.setText(profile.getPreparation());
        lieu.setText(profile.getLieu());
        periodeRecolte.setText(profile.getPeriodeRecolte());
        remarques.setText(profile.getRemarques());
        source.setText(profile.getSource());
        liens.setText(profile.getLiens());
        Glide.with(this)
                .load("file:///android_asset/thumbnails/" + profile.getImage())
                .asBitmap()
                .transform(new RoundedCornersTransformation(this, 20, 0))
                .override(300, 200)
                .into(image);
    }

}
