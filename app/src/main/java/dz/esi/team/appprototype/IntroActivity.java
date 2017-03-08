package dz.esi.team.appprototype;

import android.content.Intent;
import android.os.Bundle;
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

        CharSequence title = "this is the title ";
        CharSequence description = "this is the discription of the app where" +
                "we are goind to speak about some details ";
        addSlide(AppIntroFragment.newInstance(title, description, R.drawable.intro_slide_take_photo, getResources().getColor(R.color.logo_blue_light)));
        addSlide(AppIntroFragment.newInstance(title, description, R.drawable.intro_slide_choose_photo, getResources().getColor(R.color.logo_blue_light)));
        addSlide(AppIntroFragment.newInstance(title, description, R.drawable.intro_slide_search, getResources().getColor(R.color.logo_blue_light)));
        showStatusBar(false);


    }

    private void loadMainActivity() {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }

    @Override
    public void onSkipPressed() {
        loadMainActivity();
        Toast.makeText(getApplicationContext(), "azeddine", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDonePressed() {
        loadMainActivity();
    }


}
