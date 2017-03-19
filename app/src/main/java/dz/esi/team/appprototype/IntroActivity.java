package dz.esi.team.appprototype;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.IntentCompat;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by azeddine on 27/02/17.
 */

public class IntroActivity extends AppIntro {
    private static final String TAG = "IntroActivity";

    @Override
    public void init(Bundle savedInstanceState) {

        CharSequence title = "Explorer";
        CharSequence description = "Différentes méthodes pour accéder aux plantes";
        addSlide(AppIntroFragment.newInstance(title, description, R.drawable.slide_browse, getResources().getColor(R.color.logo_blue_light)));

        title = "Reconnaissez votre plante";
        description = "Prendre/Importer la photo de votre plante, pour avoir des détails";
        addSlide(AppIntroFragment.newInstance(title, description, R.drawable.slide_take_photo, getResources().getColor(R.color.logo_blue_light)));

        title = "Un riche profil";
        description = "Organisé, clair et bien détaillé";
        addSlide(AppIntroFragment.newInstance(title, description, R.drawable.slide_profile, getResources().getColor(R.color.logo_blue_light)));
        showStatusBar(false);


    }

    private void loadMainActivity() {
        Intent nextPage = new Intent(IntroActivity.this, HomePage.class);
        nextPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(nextPage);
        ActivityCompat.finishAffinity(this);
    }

    @Override
    public void onSkipPressed() {
        loadMainActivity();
        Toast.makeText(getApplicationContext(), "Flore", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDonePressed() {
        loadMainActivity();
    }


}
