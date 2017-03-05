package dz.esi.team.appprototype;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by azeddine on 21/02/17.
 */

public class MedicalPlantsAdapter extends ArrayAdapter {

    private static final String TAG = "AzeddineAdapter";
    private final int resourceLayout;
    private final LayoutInflater layoutInflater;
    private List<MedicalPlant> medicalPlants;


    public MedicalPlantsAdapter(Context context, int resource, List<MedicalPlant> medicalPlants) {
        super(context, resource);
        this.resourceLayout = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.medicalPlants = medicalPlants;
    }

    @Override
    public int getCount() {
        return this.medicalPlants.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {

            convertView = layoutInflater.inflate(resourceLayout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();

        }


        MedicalPlant plante = this.medicalPlants.get(position);

        viewHolder.plantFamily.setText(plante.getFamilyName());

        return convertView;
    }


    private class ViewHolder {
        // this class is used to tore the widget ID for optimisation
        final TextView plantFamily;

        public ViewHolder(View view) {
            this.plantFamily = (TextView) view.findViewById(R.id.plant_name);
        }
    }


}
