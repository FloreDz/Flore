package dz.esi.team.appprototype.data;

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

import dz.esi.team.appprototype.R;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static android.view.View.GONE;
import static dz.esi.team.appprototype.HomePage.DISPLAY_STATE;
import static dz.esi.team.appprototype.HomePage.SHOW_PLANTS_DEFAULT;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.famille;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.image;
import static dz.esi.team.appprototype.data.PlantContract.PlantEntry.sci_name;

/**
 * Created by The King Mohamed on 25/03/2017.
 */

public class PlantCursorAdapter extends CursorAdapter {

    private static String previousFamily = null;
    private static String currentFamily = null;
    Context mContext = null;


    public PlantCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mContext = context;
        Log.v("PlantCursorAdapter", "an object just got instantiated");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.v("PlantCursorAdapter", "newView in");
        if (DISPLAY_STATE == SHOW_PLANTS_DEFAULT)
            return LayoutInflater.from(context).inflate(R.layout.item_plant, parent, false);
        else
            return LayoutInflater.from(context).inflate(R.layout.item_family, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Log.v("PlantCursorAdapter", "bindView in");

        TextView tvPlantFamily;
        TextView tvPlantName;
        ImageView ivPlantImage;

        String plantFamily = currentFamily = cursor.getString(cursor.getColumnIndex(famille));
        Log.v("PlantCursorAdapter", "plantFamily ==  " + plantFamily);
        String plantSciName = cursor.getString(cursor.getColumnIndex(sci_name));
        String plantImage = cursor.getString(cursor.getColumnIndex(image));

        if (DISPLAY_STATE == SHOW_PLANTS_DEFAULT) {

            tvPlantName = (TextView) view.findViewById(R.id.plant_sci_name_in_plant_view);
            tvPlantFamily = (TextView) view.findViewById(R.id.plant_family_in_plant_view);
            Log.v("PlantCursorAdapter", "tvPlantFamily == null ? " + (tvPlantFamily == null));
            ivPlantImage = (ImageView) view.findViewById(R.id.plant_image_in_plant_view);

            Log.v("PlantCursorAdapter", "about to set text to the plant family");
            tvPlantFamily.setText("Famille : " + plantFamily);

        } else {

            tvPlantName = (TextView) view.findViewById(R.id.plant_sci_name_in_family_view);
            tvPlantFamily = (TextView) view.findViewById(R.id.plant_family_in_family_view);
            ivPlantImage = (ImageView) view.findViewById(R.id.plant_image_in_family_view);

            if (previousFamily == null || !previousFamily.equals(currentFamily)) {
                Log.v("PlantCursorAdapter", "in else -> if");
                tvPlantFamily.setText(plantFamily);
                previousFamily = currentFamily;
            } else
                tvPlantFamily.setVisibility(GONE);
        }

        Log.v("PlantCursorAdapter", "about to set text to the plant name");
        tvPlantName.setText(plantSciName);
        ivPlantImage.setScaleType(ImageView.ScaleType.FIT_CENTER);

        Glide.with(context)
                .load(Uri.parse("file:///android_asset/thumbnails/" + plantImage))
                .asBitmap()
                .override(300, 200)
                .transform(new RoundedCornersTransformation(context, 20, 0))
                .placeholder(R.drawable.placeholder_image)
                .into(ivPlantImage);

        Log.v("PlantCursorAdapter", "bindView finished");
    }

}