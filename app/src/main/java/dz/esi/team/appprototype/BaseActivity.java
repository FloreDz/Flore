package dz.esi.team.appprototype;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

/**
 * Created by azeddine on 25/02/17.
 */

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    public static final String PLANT_QUERY = "plant" ;
    public void activateToolBar(boolean enableHome){


        ActionBar actionBar= getSupportActionBar();
        if(actionBar!=null) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);

            if (toolbar == null) {
                setSupportActionBar(toolbar);
                actionBar = getSupportActionBar();
            }
        }

        if(actionBar!=null){
                actionBar.setDefaultDisplayHomeAsUpEnabled(enableHome);
                getSupportActionBar().setDisplayShowHomeEnabled(enableHome);
        }
    }
}
