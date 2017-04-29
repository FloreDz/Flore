package dz.esi.team.appprototype;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import dz.esi.team.appprototype.recognition.ORBRecognition;
import dz.esi.team.appprototype.recognition.ORBRecognition.Couple;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ImageOptionsActivity extends BaseActivity {

    public static final String LOADED_IMAGE_PATH = "LOADED_IMAGE_PATH";
    public static final String LOADED_IMAGE_URI = "LOADED_IMAGE_URI";
    public static final String STATE_IMAGE = "STATE_IMAGE";
    public static boolean threadEnabled = false ;
    private static final String TAG = "ImageOptionsActivity";

    // image proprieties
    private ImageView imageViewUploadedImage;
    private Uri imageViewUri;
    private Uri croppedImageUri;
    private boolean croppedImage = false;
    private int croppedVersion = 0;

    private BottomNavigationView bottomNavigationViewImageOption;
    private Bitmap uploadedBitmap;
    private Recognition recognition ;




    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, ": activity CREATED");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_options);
        Toolbar toolbar = (Toolbar) findViewById(R.id.imageOption_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Modifier");
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
                               recognition = new Recognition();
                                recognition.execute(uploadedBitmap);
                                break;
                            default:
                        }
                        return true;
                    }
                });
        if (!croppedImage) displayImage();
        initDatabase(this);
    }

    public ArrayList<Couple> startRecognition(Bitmap uploadedBitmap) {
        ORBRecognition orbRecognition = new ORBRecognition(this);
        Log.d(TAG, "startRecognition: about to start recognition");
        ArrayList<Couple> recognitionResult = orbRecognition.Recognize(uploadedBitmap);
        Log.d(TAG, "startRecognition: recognition finished");

        Collections.sort(recognitionResult, new Comparator<Couple>() {
            @Override
            public int compare(Couple o1, Couple o2) {
                if (o1.percentage < o2.percentage) return 1;
                else return -1;
            }
        });

        return recognitionResult;
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, ": activity PAUSED");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: checking the state of the thread");
        if(threadEnabled) {
            threadEnabled = false ;
            this.recognition.cancel(true);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, ": activity DESTROYED");
    }

    // todo : checking compatibility with marshmallow android version
    private void displayImage() {
        String uploadedImagePath = getIntent().getStringExtra(LOADED_IMAGE_PATH);

        if(uploadedImagePath!= null){
            this.imageViewUri = Uri.parse(getIntent().getStringExtra(LOADED_IMAGE_URI));
        }else{
            this.imageViewUri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
            uploadedImagePath = getRealPathFromURI(this, this.imageViewUri);
        }
        this.uploadedBitmap = fixImageRotation(uploadedImagePath);
        this.imageViewUploadedImage.setImageBitmap(this.uploadedBitmap);

    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();

        return path;
    }

    private Bitmap fixImageRotation(String path){
        Bitmap bitmap;
        int rotation ;
        int rotationInDegrees ;
        bitmap = BitmapFactory.decodeFile(path);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        rotationInDegrees = exifToDegrees(rotation);
        Matrix matrix = new Matrix();

        if (rotation != 0f) {
            matrix.preRotate(rotationInDegrees);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return  bitmap ;

    }

    private void beginCrop(Uri source) {
        this.croppedImageUri = Uri.fromFile(new File(getFilesDir(), "cropped" + croppedVersion));

        Crop.of(source, this.croppedImageUri).start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
    String path ;

        if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            this.croppedVersion++;
            this.imageViewUri = Crop.getOutput(result);
            path = imageViewUri.getPath();
            this.uploadedBitmap = fixImageRotation(path);
            this.imageViewUploadedImage.setImageBitmap(this.uploadedBitmap);
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


    public  class Recognition extends AsyncTask<Bitmap,Void,ArrayList<Couple>> {

        private ProgressBar progressBar = (ProgressBar) findViewById(R.id.image_options_progress_bar);
        private FrameLayout imageOptionsBackground = (FrameLayout) findViewById(R.id.image_options_background);
        private TextView recognitionProgress = (TextView) findViewById(R.id.image_options_recognition_progress);


        @Override
        protected void onPreExecute() {
            imageOptionsBackground.setVisibility(VISIBLE);
            progressBar.setVisibility(VISIBLE);
            recognitionProgress.setVisibility(VISIBLE);
            threadEnabled = true ;
        }

        @Override
        protected ArrayList<Couple> doInBackground(Bitmap... params) {
            Log.d(TAG, "onNavigationItemSelected , doInBackground: about to start recognition ");
                return startRecognition(params[0]);


        }

        @Override
        protected void onPostExecute(ArrayList<Couple> couples) {
            Log.d(TAG, "onNavigationItemSelected , onPostExecute : about to create intent");
            Intent intent = new Intent(ImageOptionsActivity.this,RecognitionResult.class);
            Log.d(TAG, "onNavigationItemSelected , onPostExecute : intent created, about to put array list extra");
            intent.putParcelableArrayListExtra("couplesArrayList",couples);
            Log.d(TAG, "onNavigationItemSelected , onPostExecute : extra put, about to start activity (RecognitionResult)");
            startActivity(intent);

            imageOptionsBackground.setVisibility(GONE);
            progressBar.setVisibility(GONE);
            recognitionProgress.setVisibility(GONE);

        }

        @Override
        protected void onCancelled() {
            Log.d(TAG, "onCancelled:API < 3");
            super.onCancelled();
        }

        @Override
        protected void onCancelled(ArrayList<Couple> couples) {
            Log.d(TAG, "onCancelled: API > 3 ");
            super.onCancelled(couples);
        }
    }


}
/**
 * last verification 29/04/2017
 */

