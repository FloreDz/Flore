
package dz.esi.team.appprototype.recognition;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.SortedList;
import android.util.Log;
import android.widget.ProgressBar;

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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import dz.esi.team.appprototype.data.PlantRetriever;

import static dz.esi.team.appprototype.data.PlantContract.PlantEntry._ID;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.sci_name;

public class ORBRecognition extends AppCompatActivity {

    final static String TAG = ORBRecognition.class.getSimpleName();

    static {
        if (!OpenCVLoader.initDebug()) Log.d("ERROR", "opencv not loaded");
        else Log.d("SUCCES", "opencv loaded succesfuly");
    }

    Context context;

    public ORBRecognition(Context context) {
        this.context = context;
    }

    public HashMap<Long , Float> Recognize(Bitmap entry) {

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

        HashMap<Long,Float> recognitionResult = new HashMap<>();

//        while (cursor.moveToNext()) {
//            String rep_name = "file:///android_asset/dataset/" + cursor.getString(1).toLowerCase().replaceAll(" ", "_");
//            Log.d(TAG, "Recognize: rep_name == " + rep_name);
//            File directory = new File(rep_name);
//
//            File[] fList = directory.listFiles();
//            Log.d(TAG, "Recognize: fList == null ? : " + (fList == null));
//
//            TreeSet<Float> plantGoodMatches = new TreeSet<>();

        String plantPath = "file:///android_asset/dataset/allium_sativum_l/3-2.png";


        // added from StackOverFlow ///////////////////////////////////////////////////////////////
        File f = new File(this.getCacheDir()+"/allium_sativum_l/3-2.png");
        if (!f.exists()) try {

            InputStream is = getAssets().open("/dataset/allium_sativum_l/3-2.png");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(buffer);
            fos.close();
        } catch (Exception e) { throw new RuntimeException(e); }

        ///////////////////////////////////////////////////////////////////////////////////////////

//            for (int i = 0; i < fList.length; i++) {
//                Bitmap query = BitmapFactory.decodeFile(fList[i].getPath());
                Bitmap query = BitmapFactory.decodeFile(f.getPath());
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
                    if (matchesList.get(j).distance <= (max_dist - min_dist) )
                        good_matches.addLast(matchesList.get(j));
                }

                /*MatOfDMatch mGood_matches = new MatOfDMatch();
                mGood_matches.fromList(good_matches);
                //   mGood_matches.fromList(matchesList);*/
                
                Log.d(TAG, "size of goodmatches is : " + good_matches.size());
                Log.d(TAG, "size of matches is: " + matchesList.size());
//                Log.d(TAG,"distance of " + i + " = " + good_matches.size()
//                        +"  while size of matches is "+ matchesList.size()
//                        +"  min= "+min_dist+" max= "+max_dist +"  taux = "
//                        + (double) good_matches.size()/matchesList.size()  );

//                plantGoodMatches.add((float) (good_matches.size()/matchesList.size()));
//                good_matches.clear();
//            }

//            recognitionResult.put( Long.parseLong(cursor.getString(0)) , plantGoodMatches.last() );
            recognitionResult.put( Long.parseLong(cursor.getString(0)) , (float) (good_matches.size()/matchesList.size()) );
//            plantGoodMatches.clear();

//            Log.d(TAG, "ORBRecognition: plantGoodMatches list size " + plantGoodMatches.size());
//        }

        return recognitionResult;

    }

}

