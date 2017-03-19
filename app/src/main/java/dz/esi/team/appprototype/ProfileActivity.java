package dz.esi.team.appprototype;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ProfileActivity extends AppCompatActivity {


    TextView collapseConstituent;
    LinearLayout Constituents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);


        collapseConstituent = (TextView) findViewById(R.id.plant_constituent_title);
        Constituents = (LinearLayout) findViewById(R.id.plant_constituents);

    }


    public void onClickConstituents(View v) {
        if (v == collapseConstituent) {
            Constituents.setVisibility( (Constituents.getVisibility() == VISIBLE) ? GONE : VISIBLE);
        }
    }


}
