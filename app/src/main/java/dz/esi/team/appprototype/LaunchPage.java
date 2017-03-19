package dz.esi.team.appprototype;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;

public class LaunchPage extends AppCompatActivity {

    Intent nextPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_page);

        nextPage = new Intent(LaunchPage.this, HomePage.class);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nextPage = new Intent(LaunchPage.this, HomePage.class);
                nextPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(nextPage);
                ActivityCompat.finishAffinity(LaunchPage.this);
            }
        }, 2000);
    }

}
