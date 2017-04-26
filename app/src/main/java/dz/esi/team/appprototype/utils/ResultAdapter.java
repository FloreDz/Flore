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
import com.github.lzyzsd.circleprogress.ArcProgress;

import java.util.ArrayList;

import dz.esi.team.appprototype.R;
import dz.esi.team.appprototype.data.PlantCompactProfile;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;



/**
 * Created by The King Mohamed on 22/04/2017.
 */

public class ResultAdapter extends ArrayAdapter<PlantCompactProfile> {

    public static class ViewHolder {
        TextView plantSciName;
        TextView plantFamily;
        ArcProgress plantPercentage;
        ImageView plantImage;
    }

    private ArrayList<Float> percentages;


    public ResultAdapter(@NonNull Context context, ArrayList<PlantCompactProfile> plantCompactProfiles, ArrayList<Float> percentages) {
        super(context, 0, plantCompactProfiles);
        this.percentages = percentages;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        PlantCompactProfile plantCompactProfile = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_result, parent, false);
            viewHolder.plantSciName = (TextView) convertView.findViewById(R.id.plant_sci_name_in_result_view);
            viewHolder.plantFamily = (TextView) convertView.findViewById(R.id.plant_family_in_result_view);
            viewHolder.plantPercentage = (ArcProgress) convertView.findViewById(R.id.plant_percentage_in_result_view);
            viewHolder.plantImage = (ImageView) convertView.findViewById(R.id.plant_image_in_result_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.plantSciName.setText(plantCompactProfile.getSci_name());
        viewHolder.plantFamily.setText("Famille : "+plantCompactProfile.getFamille());
        viewHolder.plantPercentage.setProgress(percentages.get(position).intValue());
        Glide
                .with(getContext())
                .load(Uri.parse("file:///android_asset/thumbnails/" + plantCompactProfile.getImage() + ".jpg"))
                .asBitmap()
                .override(300, 200)
                .transform(new RoundedCornersTransformation(getContext(), 20, 0))
                .placeholder(R.drawable.thumbnail_placeholder)
                .into(viewHolder.plantImage);

        return convertView;
    }

}
