package dz.esi.team.appprototype;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;

public class ImageOptionsActivity extends AppCompatActivity {
    private static final String TAG = "ImageOptionsActivity";

    // image proprieties
    private ImageView imageViewUploadedImage;
    private Uri imageViewUri;
    private Uri croppedImageUri;

    private boolean croppedImage = false;

    public static final String LOADED_IMAGE_PATH = "LOADED_IMAGE_PATH";
    public static final String LOADED_IMAGE_URI = "LOADED_IMAGE_URI";
    public static final String STATE_IMAGE = "STATE_IMAGE";
    private int croppedVersion = 0;


    private BottomNavigationView bottomNavigationViewImageOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_options);
        Toolbar toolbar = (Toolbar) findViewById(R.id.imageOption_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageViewUploadedImage = (ImageView) findViewById(R.id.imageView_uploadedImage);
        bottomNavigationViewImageOption = (BottomNavigationView) findViewById(R.id.bottomNavigationView_ImageOption);

        bottomNavigationViewImageOption.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId();

                        switch (id) {
                            case R.id.btn_crop:
                                croppedImage = true;
                                beginCrop(imageViewUri);


                                break;
                            case R.id.btn_image_process:
                                Toast.makeText(ImageOptionsActivity.this, "next", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                        }
                        return true;
                    }
                });

        if (!croppedImage) displayImage();
    }

    private void displayImage() {
        String uploadedImagePath = getIntent().getStringExtra(LOADED_IMAGE_PATH);

        this.imageViewUri = Uri.parse(getIntent().getStringExtra(LOADED_IMAGE_URI));
        this.imageViewUploadedImage.setImageBitmap(BitmapFactory.decodeFile(uploadedImagePath));
    }

    private void beginCrop(Uri source) {
        this.croppedImageUri = Uri.fromFile(new File(getCacheDir(), "cropped" + croppedVersion));


        Crop.of(source, this.croppedImageUri).start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {

        if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            this.croppedVersion++;
            this.imageViewUri = Crop.getOutput(result);
            this.imageViewUploadedImage.setImageURI(this.imageViewUri);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        croppedImage = savedInstanceState.getBoolean(STATE_IMAGE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_IMAGE, croppedImage);
        Log.d(TAG, "onSaveInstanceState:  the cropped image " + croppedImage);
    }

    public void startRecognition() {
        // convert the bitmap to mat using openCv
    }


}
