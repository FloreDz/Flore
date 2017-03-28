package dz.esi.team.appprototype.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dz.esi.team.appprototype.R;

/**
 * Created by azeddine on 21/02/17.
 */

public class MedicalPlantsAdapter extends ArrayAdapter {

    private static final String TAG = "MedicalPlantsAdapter";

    private final int plantResourceLayout;
    private final int familyResourceLayout;
    private final LayoutInflater layoutInflater;

    private  List<Section> sectionsList = new ArrayList<>();
    private  Context context;
    private  Section section;


    public MedicalPlantsAdapter(Context context, int plantResource, int headerResource, List<Section> sections) {
        super(context, 0, sections);

        this.context = context;
        this.plantResourceLayout = plantResource;
        this.familyResourceLayout = headerResource;
        this.layoutInflater = LayoutInflater.from(context);
        sectionsList= sections ;

    }


    @Override
    public long getItemId(int position) {
        return position;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        MedicalPlant plant ;
        MedicalPlantsFamily family ;
        PlantViewHolder plantViewHolder ;
        FamilyViewHolder familyViewHolder ;
        ViewHolder viewHolder;


       section = this.sectionsList.get(position);

         if(section.isViewHeader()){

             if(convertView==null){}else{}
                convertView = layoutInflater.inflate(familyResourceLayout, parent, false);
                viewHolder = familyViewHolder = new FamilyViewHolder(convertView);
                family = (MedicalPlantsFamily) section ;
                  convertView.setTag(viewHolder);




                familyViewHolder.getTitle().setText(family.getMedicalPlantsFamilyName());
                familyViewHolder.getPlantsFamilySize().setText(""+family.getFamilySize());


            }else{

                 Log.d(TAG, "getView: ===============================================");
                 convertView = layoutInflater.inflate(plantResourceLayout, parent, false);
                 viewHolder =plantViewHolder = new PlantViewHolder(convertView);
                 convertView.setTag(viewHolder);
                 plant = (MedicalPlant) section ;




                 // setting the main information of the plant on the resource Layout
                  plantViewHolder.getImage().setImageResource(plant.getImage());
                  plantViewHolder.getTitle().setText(plant.getName());

            }





        return convertView;
    }



    private class PlantViewHolder extends ViewHolder {
        private final ImageView image ;

        public PlantViewHolder(View view) {
            super(view, R.id.plant_sci_name);
            image = (ImageView) view.findViewById(R.id.plant_image);
        }

        public ImageView getImage() {
            return image;
        }
    }
    private class FamilyViewHolder extends ViewHolder {

       TextView plantsFamilySize ;
        public FamilyViewHolder(View view) {
            super(view, R.id.plants_family_name);
            plantsFamilySize = (TextView) view.findViewById(R.id.plants_family_size);

        }

        public TextView getPlantsFamilySize() {
            return plantsFamilySize;
        }
    }


}
