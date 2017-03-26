package dz.esi.team.appprototype.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import dz.esi.team.appprototype.R;

import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.image;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.sci_name;

/**
 * Created by The King Mohamed on 25/03/2017.
 */

public class PlantCursorAdapter extends CursorAdapter {

    Context mContext = null;

    public PlantCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.listview_plantes, parent, false); // TODO: here
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) { // TODO: here

        TextView tvPlantName = (TextView) view.findViewById(R.id.plant_sci_name);
        ImageView tvPlantImage = (ImageView) view.findViewById(R.id.plant_image);

        String plantSciName = cursor.getString(cursor.getColumnIndex(sci_name));
        String plantImage = cursor.getString(cursor.getColumnIndex(image));

        tvPlantName.setText(plantSciName);
        tvPlantImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        tvPlantImage.setImageBitmap(getBitmapFromAssets(plantImage, context));
    }

    private Bitmap getBitmapFromAssets(String imagePath, Context context) {

        AssetManager assetManager = context.getAssets();
        InputStream is = null;
        Bitmap bitmap = null;
        imagePath = imagePath.replaceFirst("./images", "thumbnails");
        try {
            is = assetManager.open(imagePath);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException ioe) {
            Log.e("getBitmap methode->try1", ioe.getMessage());
        } finally {
            try {
                is.close();
                is = null;
            } catch (Exception e) {
                Log.e("getBitmap methode->try2", e.getMessage());
            }
        }
        return bitmap;
    }

}