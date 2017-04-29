package dz.esi.flore.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import dz.esi.b.flore.R;
import dz.esi.flore.HomePage;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static dz.esi.flore.HomePage.SHOW_PLANTS_BY_SCIENTIFIQUE_NAMES;
import static dz.esi.flore.data.PlantContract.PlantEntry.famille;
import static dz.esi.flore.data.PlantContract.PlantEntry.sci_name;

/**
 * Created on 25/03/2017.
 */

public class PlantCursorAdapter extends CursorAdapter {

    private final String TAG = this.getClass().getSimpleName();
    
    Context mContext = null;
    String DISPLAY_STATE;


    public PlantCursorAdapter(Context context, Cursor c, String DISPLAY_STATE) {
        super(context, c, 0);
        this.mContext = context;
        this.DISPLAY_STATE = DISPLAY_STATE;
        Log.d(TAG, "an object just got instantiated");
    }

    public void setDISPLAY_STATE(String DISPLAY_STATE) {
        this.DISPLAY_STATE = DISPLAY_STATE;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        if (DISPLAY_STATE.equals(SHOW_PLANTS_BY_SCIENTIFIQUE_NAMES))
            return LayoutInflater.from(context).inflate(R.layout.item_plant, parent, false);
        else
            return LayoutInflater.from(context).inflate(R.layout.item_family, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tvPlantFamily;
        TextView tvPlantName;
        ImageView ivPlantImage;

        String plantFamily = cursor.getString(cursor.getColumnIndex(famille));
        String plantSciName = cursor.getString(cursor.getColumnIndex(sci_name));
        String plantImage = plantSciName.toLowerCase() + ".jpg";

        if (DISPLAY_STATE.equals(SHOW_PLANTS_BY_SCIENTIFIQUE_NAMES)) {

            tvPlantName = (TextView) view.findViewById(R.id.plant_sci_name_in_plant_view);
            tvPlantFamily = (TextView) view.findViewById(R.id.plant_family_in_plant_view);
            ivPlantImage = (ImageView) view.findViewById(R.id.plant_image_in_plant_view);

            tvPlantFamily.setText("Famille : " + plantFamily);

        } else {

            tvPlantName = (TextView) view.findViewById(R.id.plant_sci_name_in_family_view);
            tvPlantFamily = (TextView) view.findViewById(R.id.plant_family_in_family_view);
            ivPlantImage = (ImageView) view.findViewById(R.id.plant_image_in_family_view);

            if (HomePage.plantsHeaders.contains(plantSciName)) {
                tvPlantFamily.setVisibility(VISIBLE);
                Log.d(TAG, "in else -> if");
                tvPlantFamily.setText(plantFamily);
            } else {
                tvPlantFamily.setVisibility(GONE);
                Log.d(TAG, "in else -> else");
            }
        }

        tvPlantName.setText(plantSciName);
        ivPlantImage.setScaleType(ImageView.ScaleType.FIT_CENTER);

        Glide.with(context)
                .load(Uri.parse("file:///android_asset/thumbnails/" + plantImage))
                .asBitmap()
                .override(300, 200)
                .transform(new RoundedCornersTransformation(context, 20, 0))
                .placeholder(R.drawable.thumbnail_placeholder)
                .into(ivPlantImage);

        Log.d(TAG, "bindView finished");
    }
}
/**
 * last verification 28/04/2017
 */