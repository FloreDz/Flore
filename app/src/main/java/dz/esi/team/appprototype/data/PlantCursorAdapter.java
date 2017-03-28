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
        Log.v("PlantCursorAdapter", "an object just got instantiated");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.v("PlantCursorAdapter", "newView in");
        return LayoutInflater.from(context).inflate(R.layout.item_plant, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tvPlantName = (TextView) view.findViewById(R.id.plant_sci_name);
        ImageView tvPlantImage = (ImageView) view.findViewById(R.id.plant_image);

        String plantSciName = cursor.getString(cursor.getColumnIndex(sci_name));
        String plantImage = cursor.getString(cursor.getColumnIndex(image));

        tvPlantName.setText(plantSciName);

        plantImage = plantImage.replace("./images", "thumbnails");

        Glide.with(context)
                .load(Uri.parse("file:///android_asset/" + plantImage))
                .placeholder(R.drawable.placeholder_image)
                .fitCenter()
                .into(tvPlantImage);

        Log.v("PlantCursorAdapter", "bindView finished");
    }

}