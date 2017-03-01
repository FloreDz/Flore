package dz.esi.team.appprototype;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
        View view = layoutInflater.inflate(resourceLayout, parent, false);

        TextView plantFamily = (TextView) view.findViewById(R.id.plant_name);

        MedicalPlant plante = this.medicalPlants.get(position);

        plantFamily.setText(plante.getFamilyName());
        return view;
    }


}
