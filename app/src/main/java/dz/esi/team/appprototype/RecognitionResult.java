package dz.esi.team.appprototype;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

public class RecognitionResult extends AppCompatActivity {

    ProgressBar resultProgressBar;
    ListView resultListView;
    FrameLayout resultBackground;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition_result);
    }
}
