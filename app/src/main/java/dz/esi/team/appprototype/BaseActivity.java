package dz.esi.team.appprototype;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import dz.esi.team.appprototype.data.PlantDbHelper;

import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.famille;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.sci_name;

/**
 * Created by azeddine on 25/02/17.
 */

public class BaseActivity extends AppCompatActivity {

    public static final String SHOW_PLANTS_BY_SCIENTIFIQUE_NAMES = sci_name + " ASC";
    public static final String SHOW_PLANTS_BY_FAMILIES = famille + " ASC";
    public static PlantDbHelper mDbHelper;

    public void activateToolBar(boolean enableHome) {

        ActionBar actionBar = getSupportActionBar();

        if (actionBar!=null) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);

            if (toolbar == null) {
                setSupportActionBar(toolbar);
                actionBar = getSupportActionBar();
            }
        }

        if (actionBar != null){
            actionBar.setDefaultDisplayHomeAsUpEnabled(enableHome);
            getSupportActionBar().setDisplayShowHomeEnabled(enableHome);
        }
    }

    public void initDatabase(Context context) {

        if (mDbHelper == null) {
            mDbHelper = new PlantDbHelper(context);
            try {
                mDbHelper.createDataBase();
            } catch (Exception e) {
                Log.e("From Main.db creation", e.getMessage());
            }
            mDbHelper.openDataBase();
        }
    }

}
