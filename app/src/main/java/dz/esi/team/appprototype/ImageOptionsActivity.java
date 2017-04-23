package dz.esi.team.appprototype;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import dz.esi.team.appprototype.recognition.ORBRecognition;

public class ImageOptionsActivity extends AppCompatActivity {

    public static final String LOADED_IMAGE_PATH = "LOADED_IMAGE_PATH";
    public static final String LOADED_IMAGE_URI = "LOADED_IMAGE_URI";
    public static final String STATE_IMAGE = "STATE_IMAGE";
    private static final String TAG = "ImageOptionsActivity";

    // image proprieties
    private ImageView imageViewUploadedImage;
    private Uri imageViewUri;
    private Uri croppedImageUri;
    private boolean croppedImage = false;
    private int croppedVersion = 0;

    private BottomNavigationView bottomNavigationViewImageOption;
    private Bitmap uploadedBitmap;


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
                                Toast.makeText(ImageOptionsActivity.this, "recognition started", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onNavigationItemSelected: about to create intent");
                                Intent intent = new Intent(ImageOptionsActivity.this,RecognitionResult.class);
                                Log.d(TAG, "onNavigationItemSelected: intent created, about to put array list extra");
                                intent.putParcelableArrayListExtra("couplesArrayList",startRecognition(uploadedBitmap));
                                Log.d(TAG, "onNavigationItemSelected: extra put, about to start activity (RecognitionResult)");
                                startActivity(intent);
                                break;
                            default:
                        }
                        return true;
                    }
                });

        if (!croppedImage) displayImage();
    }

    public ArrayList<ORBRecognition.Couple> startRecognition(Bitmap uploadedBitmap) {
//        Log.d(TAG, "startRecognition: about to start recognition");
//        ArrayList<ORBRecognition.Couple> recognitionResult = ORBRecognition.Recognize(uploadedBitmap);
//        Log.d(TAG, "startRecognition: recognition finished");
//        Log.d(TAG, "startRecognition: recognitionResult : " + recognitionResult.toString());
//        //noinspection Since15
//        recognitionResult.sort(new Comparator<ORBRecognition.Couple>() {
//            @Override
//            public int compare(ORBRecognition.Couple o1, ORBRecognition.Couple o2) {
//                if (o1.percentage > o2.percentage) return 1;
//                else return -1;
//            }
//        });

        ArrayList<ORBRecognition.Couple> recognitionResult = new ArrayList<>();
        recognitionResult.add(new ORBRecognition.Couple(2L,11.2f));
        recognitionResult.add(new ORBRecognition.Couple(9L,91.2f));
        recognitionResult.add(new ORBRecognition.Couple(5L,01.2f));
        recognitionResult.add(new ORBRecognition.Couple(8L,51.52f));

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
        Log.d(TAG, ": activity STOPPED");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, ": activity DESTROYED");
    }

    private void displayImage() {
        String uploadedImagePath = getIntent().getStringExtra(LOADED_IMAGE_PATH);

        if(uploadedImagePath!= null){
            this.imageViewUri = Uri.parse(getIntent().getStringExtra(LOADED_IMAGE_URI));
//            this.imageViewUploadedImage.setImageBitmap(BitmapFactory.decodeFile(uploadedImagePath)); todo:check this comments
        }else{
            this.imageViewUri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
            //uploadedImagePath = new File(this.imageViewUri.toString()).getPath(); todo: and these
            //uploadedImagePath = imageViewUri.getPath();
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

        Crop.of(source, this.croppedImageUri).asSquare().start(this);
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


}
