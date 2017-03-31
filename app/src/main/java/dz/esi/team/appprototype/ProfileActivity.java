package dz.esi.team.appprototype;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ProfileActivity extends AppCompatActivity {


    TextView collapseConstituent;
    LinearLayout Constituents;

    TextView sci_name,
            resume;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);


        collapseConstituent = (TextView) findViewById(R.id.plant_constituent_title);
        Constituents = (LinearLayout) findViewById(R.id.plant_constituents);

        sci_name = (TextView) findViewById(R.id.plant_name_sci);
        resume = (TextView) findViewById(R.id.plant_resume);

//        PlantProfile profile = new PlantProfile(1L);
//
//        sci_name.setText(profile.getSci_name());
//        resume.setText(profile.getResume());

    }


    public void onClickConstituents(View v) {
        if (v == collapseConstituent) {
            Constituents.setVisibility( (Constituents.getVisibility() == VISIBLE) ? GONE : VISIBLE);
        }
    }

}
