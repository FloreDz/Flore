package dz.esi.team.appprototype;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import dz.esi.team.appprototype.data.PlantCompactProfile;
import dz.esi.team.appprototype.utils.ResultAdapter;
import dz.esi.team.appprototype.recognition.ORBRecognition.Couple;

import static android.view.View.*;

public class RecognitionResult extends AppCompatActivity {

    private static final String TAG = RecognitionResult.class.getSimpleName();

    public static int i = 0;

    private ListView resultListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition_result);

        resultListView = (ListView) findViewById(R.id.recognition_result_list_view);
        View emptyView = findViewById(R.id.empty_view);

        ArrayList<Couple> couplesList =  this.getIntent().getParcelableArrayListExtra("couplesArrayList");
        ArrayList<PlantCompactProfile> plantsList = extractPlantsCompactProfilesArrayListFromCouplesArrayList(couplesList);
        ArrayList<Float> percentagesList = extractPercentagesArrayListFromCouplesArrayList(couplesList);

        ResultAdapter resultAdapter = new ResultAdapter(this,plantsList,percentagesList);

        resultListView.setEmptyView(emptyView);
        resultListView.setAdapter(resultAdapter);
        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(RecognitionResult.this, ProfileActivity.class);
                    String plantId = ((PlantCompactProfile) parent.getAdapter().getItem(position)).get_ID();
                    intent.putExtra("PlantID", Long.parseLong(plantId));
                    Log.d(TAG, "about to start activity 'plantactivity'");
                    startActivity(intent);
                }
        });


    }


    private ArrayList<PlantCompactProfile>
        extractPlantsCompactProfilesArrayListFromCouplesArrayList(ArrayList<Couple> recognitionResult) {

            ArrayList<PlantCompactProfile> plants = new ArrayList<>();

            for (Couple couple : recognitionResult) {
                plants.add(new PlantCompactProfile(couple.plantId));
            }

            return plants;
    }

    private ArrayList<Float>
        extractPercentagesArrayListFromCouplesArrayList(ArrayList<Couple> recognitionResult) {

            ArrayList<Float> plantsPercentages = new ArrayList<>();

            for (Couple couple : recognitionResult) {
                plantsPercentages.add(100*couple.percentage);
            }

            return plantsPercentages;
    }

}
