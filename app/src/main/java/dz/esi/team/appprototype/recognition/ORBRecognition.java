package dz.esi.team.appprototype.recognition;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import dz.esi.team.appprototype.data.PlantRetriever;
import jp.co.cyberagent.android.gpuimage.PixelBuffer;

import static dz.esi.team.appprototype.RecognitionResult.i;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry._ID;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.sci_name;

public class ORBRecognition {

    final static String TAG = ORBRecognition.class.getSimpleName();

    static {
        if (!OpenCVLoader.initDebug()) Log.d("ERROR", "opencv not loaded");
        else Log.d("SUCCESS", "opencv loaded successfully");
    }

    public static class Couple implements Parcelable {
        public long plantId;
        public float percentage;

        public Couple () {}

        public Couple(long plantId, float percentage) {
            this.plantId = plantId;
            this.percentage = percentage;
        }

        protected Couple(Parcel in) {
            plantId = in.readLong();
            percentage = in.readFloat();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(plantId);
            dest.writeFloat(percentage);
        }

        public static final Creator<Couple> CREATOR = new Creator<Couple>() {
            @Override
            public Couple createFromParcel(Parcel in) {
                return new Couple(in);
            }

            @Override
            public Couple[] newArray(int size) {
                return new Couple[size];
            }
        };
    }


    Context context;

    public ORBRecognition (Context context) {
        this.context = context;
        Log.d(TAG, "ORBRecognition: an instance was just created");
    }

    public ArrayList<Couple> Recognize(Bitmap entry) {

        if (entry == null) Log.d(TAG, "bitmap null");
        else Log.d(TAG, "bitmap not null!");

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

        ArrayList<Couple> recognitionResult = new ArrayList<>();


        while (cursor.moveToNext()) {
            String rep_name = "dataset/" + cursor.getString(1).toLowerCase().replaceAll(" ", "_");
            Log.d(TAG, "Recognize: rep_name == " + rep_name);

            TreeSet<Float> plantGoodMatches = new TreeSet<>();

            List<String> imagesList = getDirectoryContent(rep_name);

            for (int i = 0; i < imagesList.size() ; i++) {
                Bitmap query = getBitmapFromAssets(rep_name + "/" + imagesList.get(i));
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
                    if (matchesList.get(j).distance <= (max_dist - min_dist) ) // todo: a change
                        good_matches.addLast(matchesList.get(j));
                }

                Log.d(TAG, "size of goodmatches is : " + good_matches.size());
                Log.d(TAG, "size of matches is: " + matchesList.size());
                Log.d(TAG,"distance of " + i + " = " + good_matches.size()
                        +"  while size of matches is "+ matchesList.size()
                        +"  min= "+min_dist+" max= "+max_dist +"  taux = "
                        + (double) good_matches.size()/matchesList.size()  );

                plantGoodMatches.add((float)good_matches.size()/matchesList.size());
                good_matches.clear();
            }

            recognitionResult.add(new Couple (Long.parseLong(cursor.getString(0)), plantGoodMatches.last()));

            Log.d(TAG, "Recognize: all the plantGoodMatches : " + plantGoodMatches.toString());
            Log.d(TAG, "Recognize: last (biggest) = " + plantGoodMatches.last());
            Log.d(TAG, "Recognize: first (smallest) = " + plantGoodMatches.first());

            Log.d(TAG, "ORBRecognition: plantGoodMatches list size before clear : " + plantGoodMatches.size());

            plantGoodMatches.clear();

            Log.d(TAG, "ORBRecognition: plantGoodMatches list size after clear : " + plantGoodMatches.size());
        }

        return recognitionResult;

    }

    private Bitmap getBitmapFromAssets(String path) {

        AssetManager assetManager = this.context.getAssets();
        Bitmap bitmap = null;
        InputStream str = null;

        try {
            str = assetManager.open(path);
            bitmap = BitmapFactory.decodeStream(str);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                str.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        return bitmap;
    }

    private List<String> getDirectoryContent(String directoryPath) {
        AssetManager assetManager = this.context.getAssets();
        List<String> list = new ArrayList<>();
        try {
            String[] files = assetManager.list(directoryPath);
            list = Arrays.asList(files);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

}

