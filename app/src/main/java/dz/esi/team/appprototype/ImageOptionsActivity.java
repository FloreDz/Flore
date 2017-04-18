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
                                Toast.makeText(ImageOptionsActivity.this, "recognition", Toast.LENGTH_SHORT).show();

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


        if(uploadedImagePath!= null){
            this.imageViewUri = Uri.parse(getIntent().getStringExtra(LOADED_IMAGE_URI));
//            this.imageViewUploadedImage.setImageBitmap(BitmapFactory.decodeFile(uploadedImagePath));
        }else{
            this.imageViewUri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
            //uploadedImagePath = new File(this.imageViewUri.toString()).getPath();
            //uploadedImagePath = imageViewUri.getPath();
            uploadedImagePath = getRealPathFromURI(this, this.imageViewUri);
        }
        Log.d(TAG, "displayImage: =============================================== " + uploadedImagePath);
        Bitmap bitmap = fixImageRotation(uploadedImagePath);
        this.imageViewUploadedImage.setImageBitmap(bitmap);

    }///storage/emulated/0/Pictures/Screenshots/Screenshot_2017-04-04-22-36-45.png

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
    Bitmap bitmap ;
        if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            this.croppedVersion++;
            this.imageViewUri = Crop.getOutput(result);
            path = imageViewUri.getPath();
            bitmap = fixImageRotation(path);

            this.imageViewUploadedImage.setImageBitmap(bitmap);
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
