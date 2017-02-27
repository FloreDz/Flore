package dz.esi.team.appprototype;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by azeddine on 27/02/17.
 */

public class IntroActivity  extends AppIntro2{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.


        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        //addSlide(AppIntroFragment.newInstance(title, description, image, backgroundColor));
        CharSequence tot = new String("azeddo,e");
        CharSequence dis = new String("azeddofffffffffffffffffffffffffff,e");

        addSlide(AppIntroFragment.newInstance(tot,dis,R.drawable.leaf, Color.parseColor("#EEEEEE")));
        addSlide(AppIntroFragment.newInstance(tot,dis,R.drawable.app_logo_no_background, Color.parseColor("#EEEEEE")));
        addSlide(AppIntroFragment.newInstance(tot,dis,R.drawable.logo_with_white_background, Color.parseColor("#EEEEEE")));

        setDepthAnimation();
        // OPTIONAL METHODS
        // Override bar/separator color.


        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);
    }
}
