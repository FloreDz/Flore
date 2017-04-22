package dz.esi.team.appprototype.utils;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import dz.esi.team.appprototype.R;
import dz.esi.team.appprototype.data.PlantCompactProfile;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


/**
 * Created by The King Mohamed on 22/04/2017.
 */

public class ResultAdapter extends ArrayAdapter<PlantCompactProfile> {
    public ResultAdapter(@NonNull Context context, ArrayList<PlantCompactProfile> plantCompactProfiles) {
        super(context, 0, plantCompactProfiles);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        PlantCompactProfile plantCompactProfile = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_result,parent,false);

        ((TextView) convertView.findViewById(R.id.plant_sci_name_in_result_view)).setText(plantCompactProfile.getSci_name());
        ((TextView) convertView.findViewById(R.id.plant_family_in_result_view)).setText(plantCompactProfile.getFamille());
        Glide
                .with(getContext())
                .load(Uri.parse("file:///android_asset/thumbnails/" + plantCompactProfile.getImage()))
                .asBitmap()
                .override(300, 200)
                .transform(new RoundedCornersTransformation(getContext(), 20, 0))
                .placeholder(R.drawable.thumbnail_placeholder)
                .into((ImageView) convertView.findViewById(R.id.plant_image_in_result_view));

        return convertView;
    }

}
