package dz.esi.team.appprototype;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class ImageRecognitionActivity extends BaseActivity{
    private String imagePath ;
    public static final String LOADED_IMAGE_PATH= "LOADED_IMAGE_PATH";
    private ImageView plantToRecognise ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_recognition);
        this.imagePath = getIntent().getStringExtra(LOADED_IMAGE_PATH);
        this.plantToRecognise = (ImageView) findViewById(R.id.planteToRecognie);

        plantToRecognise.setImageBitmap(BitmapFactory.decodeFile(this.imagePath));
    }
}
