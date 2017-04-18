
package dz.esi.team.appprototype.recognition;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/*import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;*/

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import dz.esi.team.appprototype.data.PlantRetriever;

import static dz.esi.team.appprototype.data.PlantContract.PlantEntry._ID;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.sci_name;

public class ORBRecognition extends AppCompatActivity {

    final static String TAG = ORBRecognition.class.getSimpleName();

    static {
        if (!OpenCVLoader.initDebug()) Log.d("ERROR", "opencv not loaded");
        else Log.d("SUCCES", "opencv loaded succesfuly");
    }



    public ORBRecognition(Bitmap entry) {

        if (entry == null) Log.d(TAG, "null");
        else Log.d(TAG, "not null!");

        Mat enrtyImg = new Mat(entry.getWidth(), entry.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(entry, enrtyImg);
        
        if (enrtyImg == null) Log.d(TAG, "img1 is null!");
        else Log.d(TAG, " img1 is not null!");
        
        Imgproc.cvtColor(enrtyImg, enrtyImg, Imgproc.COLOR_RGB2GRAY);

        FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
        DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

        Mat descriptors1 = new Mat();
        MatOfKeyPoint keyPoints1 = new MatOfKeyPoint();

        detector.detect(enrtyImg, keyPoints1);
        descriptor.compute(enrtyImg, keyPoints1, descriptors1);

        Cursor cursor = PlantRetriever.RetrievePlants(new String[]{_ID,sci_name},null,null,null);

        while (cursor.moveToNext()) {
            File directory = new File(cursor.getString(1));
            File[] fList = directory.listFiles();

            for (int i = 0; i < fList.length; i++) {
                Bitmap query = BitmapFactory.decodeFile(fList[i].getPath());
                Mat queryImg = new Mat(query.getWidth(), query.getHeight(), CvType.CV_8UC1);
                Utils.bitmapToMat(query, queryImg);

                Imgproc.cvtColor(queryImg, queryImg, Imgproc.COLOR_RGB2GRAY);

                Mat descriptors2 = new Mat();
                MatOfKeyPoint keyPoints2 = new MatOfKeyPoint();

                detector.detect(queryImg, keyPoints2);
                descriptor.compute(queryImg, keyPoints2, descriptors2);
                
                // Matching
                MatOfDMatch matches = new MatOfDMatch();

                matcher.match(descriptors1, descriptors2, matches);
                Log.d(TAG, "size of matches = " + matches.size());
                                
                List<DMatch> matchesList = matches.toList();
                
                double max_dist = 0.0;
                double min_dist = 100.0;

                for (int j = 0; j < matchesList.size(); j++) {
                    double dist = (double) matchesList.get(j).distance;
                    if (dist < min_dist) min_dist = dist;
                    if (dist > max_dist) max_dist = dist;
                }

                LinkedList<DMatch> good_matches = new LinkedList<>();
                for (int j = 0; j < matchesList.size(); j++) {
                    if (matchesList.get(j).distance <= (max_dist-min_dist) )
                        good_matches.addLast(matchesList.get(j));
                }

                MatOfDMatch mGood_matches = new MatOfDMatch();
                mGood_matches.fromList(good_matches);
                //   mGood_matches.fromList(matchesList);
                
                Log.d(TAG, "size of goodmatches is : " + good_matches.size());
                Log.d(TAG, "size of matches is: " + matchesList.size());
                Log.d(TAG,"distance of "+i+" = " + good_matches.size()+"  while size of matches is "+ matchesList.size()+"  min= "+min_dist+" max= "+max_dist +"  taux = "+ (double) good_matches.size()/matchesList.size()  );

            }

        }


    }
}


//package com.example.hakoo.myapplication;
//
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.MenuItem;
//import android.view.SurfaceView;
//import android.view.WindowManager;
//
//import org.opencv.android.BaseLoaderCallback;
//import org.opencv.android.CameraBridgeViewBase;
//import org.opencv.android.LoaderCallbackInterface;
//import org.opencv.android.OpenCVLoader;
//import org.opencv.core.Core;
//import org.opencv.core.CvType;
//import org.opencv.core.Mat;
//import org.opencv.highgui.Highgui;
//import org.opencv.imgproc.Imgproc;
//
//public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{
//
//    public static final String TAG = MainActivity.class.getSimpleName();
//    private CameraBridgeViewBase cameraBridgeViewBase;
//    private boolean mIsJavaCamera=true;
//    private MenuItem mItemSwuitchCamera = null ;
//    Mat mRgba;
//    Mat mRgbaF;
//    Mat mRgbaT;
//
//
//
//
//    static{
//        if(OpenCVLoader.initDebug()){
//            Log.d(TAG,"opencv is loaded!");
//        }
//        else{
//            Log.d(TAG,"opencv is not loaded!");
//        }
//    }
//
//    BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
//        @Override
//        public void onManagerConnected(int status) {
//            switch(status){
//                case BaseLoaderCallback.SUCCESS:{
//                    cameraBridgeViewBase.enableView();
//                    break;
//                }
//                default:{
//                    super.onManagerConnected(status);
//                    break;
//                }
//            }
//
//        }
//    };
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        cameraBridgeViewBase = (CameraBridgeViewBase) findViewById(R.id.java_camera_view);
//        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
//        cameraBridgeViewBase.setCvCameraViewListener(this);
//
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//            if(cameraBridgeViewBase!=null)
//            cameraBridgeViewBase.disableView();
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if(cameraBridgeViewBase!=null)
//            cameraBridgeViewBase.disableView();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if(OpenCVLoader.initDebug()){
//            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
//        }
//        else{
//            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11,this,baseLoaderCallback);
//
//        }
//    }
//
//    @Override
//    public void onCameraViewStarted(int width, int height) {
//        mRgba =new Mat(height,width, CvType.CV_8UC4);
//        mRgbaF=new Mat(height,width, CvType.CV_8UC4);
//        mRgbaT=new Mat(height,width, CvType.CV_8UC4);
//        Mat mGray= new Mat(height,width, CvType.CV_8UC1);
//
//    }
//
//    @Override
//    public void onCameraViewStopped() {
//      mRgba.release();
//    }
//
//    @Override
//    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//        mRgba=inputFrame.rgba();
//        Core.transpose(mRgba,mRgbaT);
//
//        Imgproc.resize(mRgbaT,mRgbaF,mRgbaF.size(),0,0,0);
//        Core.flip(mRgbaT,mRgba,1);
//
//        return mRgba;
//    }
//
//}