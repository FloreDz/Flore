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

        CharSequence title = "slide title ";
        CharSequence description = "a simple and short discription of the option that our app provide  ";
          addSlide(AppIntroFragment.newInstance(title, description, R.drawable.slide_take_photo, getResources().getColor(R.color.logo_blue_light)));
         addSlide(AppIntroFragment.newInstance(title, description, R.drawable.slide_browse, getResources().getColor(R.color.logo_blue_light)));
         addSlide(AppIntroFragment.newInstance(title, description, R.drawable.slide_profile, getResources().getColor(R.color.logo_blue_light)));
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
