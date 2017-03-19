package dz.esi.team.appprototype.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import dz.esi.team.appprototype.R;

/**
 * Created by azeddine on 21/02/17.
 */

public class MedicalPlantsAdapter extends ArrayAdapter {

    private static final String TAG = "MedicalPlantsAdapter";

    private final int plantResourceLayout;
    private final int sectionHeaderResourceLayout;
    private final LayoutInflater layoutInflater;

    private List<Section> sectionList;
    private Context context;


    public MedicalPlantsAdapter(Context context, int plantResource, int headerResource, List<Section> sections) {
        super(context, 0, sections);

        this.context = context;
        this.plantResourceLayout = plantResource;
        this.sectionHeaderResourceLayout = headerResource;
        this.layoutInflater = LayoutInflater.from(context);
        this.sectionList = sections;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        HeaderSectionViewHolder headerSectionViewHolder;
        PlantViewHolder plantViewHolder;
        ViewHolder viewHolder;

            if(sectionList.get(position).isSection()){
                convertView = layoutInflater.inflate(sectionHeaderResourceLayout, parent, false);
                viewHolder = new HeaderSectionViewHolder(convertView);

            }else{
                convertView = layoutInflater.inflate(plantResourceLayout, parent, false);
                viewHolder = new PlantViewHolder(convertView);

            }
            convertView.setTag(viewHolder);







        Section section = this.sectionList.get(position);
        viewHolder.getTitle().setText(section.getTitle());


        return convertView;
    }


    private class PlantViewHolder extends ViewHolder {


        public PlantViewHolder(View view) {
            super(view, R.id.plant_name);
        }
    }

    private class HeaderSectionViewHolder extends ViewHolder {


        public HeaderSectionViewHolder(View view) {
            super(view, R.id.plants_family);

        }
    }


}
