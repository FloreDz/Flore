package dz.esi.flore;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

public class HelpActivity extends AppCompatActivity{

    ExpandableRelativeLayout expandableLayout1 ,expandableLayout2, expandableLayout3,expandableLayout4 ,expandableLayout5;
    TextView textView2 , textView4 , textView5 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textView2 = (TextView) findViewById(R.id.textView2);
        textView4 = (TextView) findViewById(R.id.textView4);
        textView5 = (TextView) findViewById(R.id.textView5);

        SpannableStringBuilder builder = new SpannableStringBuilder();

        builder.append("Que ce soit vous avez son image dans votre galerie");
        builder.append(", ou vous rencontrez la plante pour la première fois, en cliquant sur le bouton ").append(" .");
        //TODO: change it to camera insted of plus
        builder.setSpan(new ImageSpan(HelpActivity.this, R.drawable.fab_camera),
                builder.length() - 2, builder.length()-1, 0);
        builder.append(" Vous pouvez importer la photo dont vous voulez reconnaitre la plante, ou prendre une photo de cette dernière. Après, l’application va s’occuper de trouver son profil pour vous.");
        textView2.setText(builder);

        SpannableStringBuilder builderFour = new SpannableStringBuilder();
        builderFour.append("Dans ce cas en appuyant sur le bouton ").append(" .");
        builderFour.setSpan(new ImageSpan(HelpActivity.this , R.drawable.search_icone),
                builderFour.length() -2, builderFour.length()-1, 0);
        builderFour.append(" grâce à une recherche vivante et en temps réel, vous aurez tous les résultats possibles, après l’introduction de chaque caractère");

        textView4.setText(builderFour);



        SpannableStringBuilder builderFive = new SpannableStringBuilder();
        builderFive.append("Parce que la plus importante catégorisation est celle des familles, une option est offerte pour faire cela dans la page d’accueil en appuyant sur").append(" .");
        builderFive.setSpan(new ImageSpan(HelpActivity.this, R.drawable.option_menu),
                builderFive.length() - 2, builderFive.length() -1 , 0);
        builderFive.append(" puis « Afficher par familles »");

        textView5.setText(builderFive);

    }



    public void expandableButton1(View view) {
        expandableLayout1 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout1);
        expandableLayout1.toggle(); // toggle expand and collapse
    }

    public void expandableButton2(View view) {
        expandableLayout2 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout2);
        expandableLayout2.toggle(); // toggle expand and collapse
    }

    public void expandableButton3(View view) {
        expandableLayout3 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout3);
        expandableLayout3.toggle(); // toggle expand and collapse
    }

    public void expandableButton4(View view) {
        expandableLayout4 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout4);
        expandableLayout4.toggle(); // toggle expand and collapse
    }

    public void expandableButton5(View view) {
        expandableLayout5 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout5);
        expandableLayout5.toggle(); // toggle expand and collapse
    }



}
/**
 * last verification 28/04/2017
 */

