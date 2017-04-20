package dz.esi.team.appprototype;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

public class RecognitionResult extends AppCompatActivity {

    private static final String TAG = RecognitionResult.class.getSimpleName();

    ProgressBar resultProgressBar;
    ListView resultListView;
    FrameLayout resultBackground;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition_result);

        resultBackground = (FrameLayout) findViewById(R.id.result_background);
        resultProgressBar = (ProgressBar) findViewById(R.id.progress_bar_result);
        resultListView = (ListView) findViewById(R.id.recognition_result_list_view);
        View emptyView = findViewById(R.id.empty_view);

        resultListView.setEmptyView(emptyView);
        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RecognitionResult.this, ProfileActivity.class);
                intent.putExtra("PlantID", id);
                Log.d(TAG, "about to start activity 'plantactivity'");
                startActivity(intent);
            }
        });

        
    }
}
