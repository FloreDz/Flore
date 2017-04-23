package dz.esi.team.appprototype;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import dz.esi.team.appprototype.data.PlantCompactProfile;
import dz.esi.team.appprototype.utils.ResultAdapter;
import dz.esi.team.appprototype.recognition.ORBRecognition.Couple;

public class RecognitionResult extends AppCompatActivity {

    private static final String TAG = RecognitionResult.class.getSimpleName();

    ListView resultListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition_result);

        resultListView = (ListView) findViewById(R.id.recognition_result_list_view);
        View emptyView = findViewById(R.id.empty_view);

        resultListView.setEmptyView(emptyView);
        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: item clicked ! pos = " + position + ", id = " + id);
                Intent intent = new Intent(RecognitionResult.this, ProfileActivity.class);
                intent.putExtra("PlantID", id);
                Log.d(TAG, "about to start activity 'plantactivity'");
                startActivity(intent);
            }
        });

        ArrayList<Couple> couplesList =  this.getIntent().getParcelableArrayListExtra("couplesArrayList");
        ArrayList<PlantCompactProfile> plantsList = extractPlantsCompactProfilesArrayListFromCouplesArrayList(couplesList);
        ArrayList<Float> percentagesList = extractPercentagesArrayListFromCouplesArrayList(couplesList);

        ResultAdapter resultAdapter = new ResultAdapter(this,plantsList,percentagesList);

        resultListView.setAdapter(resultAdapter);
        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RecognitionResult.this, ProfileActivity.class);
                String plantId = ((PlantCompactProfile)parent.getAdapter().getItem(position)).get_ID();
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
                plantsPercentages.add(couple.percentage);
            }

            return plantsPercentages;
    }
}
