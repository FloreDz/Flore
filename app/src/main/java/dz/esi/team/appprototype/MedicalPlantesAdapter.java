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

public class MedicalPlantesAdapter extends ArrayAdapter {

    private static final String TAG = "AzeddineAdapter";
    private final int resourceLayout;
    private final LayoutInflater layoutInflater;
    private List<MedicalPlante> medicalPlantes;


        public MedicalPlantesAdapter(Context context, int resource, List<MedicalPlante> medicalPlantes) {
            super(context, resource);
            this.resourceLayout = resource;
            this.layoutInflater = LayoutInflater.from(context);
            this.medicalPlantes = medicalPlantes;
        }

       @Override
        public int getCount() {
            return this.medicalPlantes.size();
        }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = layoutInflater.inflate(resourceLayout, parent, false);

        TextView plantFamilly = (TextView) view.findViewById(R.id.familly);

        MedicalPlante plante = this.medicalPlantes.get(position);

        plantFamilly.setText(plante.getFamillyName());
        return view;
    }



}
