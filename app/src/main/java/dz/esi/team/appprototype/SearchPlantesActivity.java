package dz.esi.team.appprototype;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dz.esi.team.appprototype.utils.MedicalPlant;
import dz.esi.team.appprototype.utils.MedicalPlantsAdapter;
import dz.esi.team.appprototype.utils.MedicalPlantsFamily;
import dz.esi.team.appprototype.utils.Section;


public class SearchPlantesActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    public static final String PLANT_QUERY = "PLANT_QUERY";
    private static final String TAG = "SearchPlantesActivity";
    ListView searchResultListView;
    private SearchView searchView;
    private String queryText;
    private List<Section> medicalPlantsFamilyList = new ArrayList<>();
    private MedicalPlantsAdapter plantsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_plantes);

        // this method will activate the layout toolBar , it is implemented in the BaseActivity

        activateToolBar(false);
        searchResultListView = (ListView) findViewById(R.id.search_result_list_view);

        List<MedicalPlant> medicalPlantsList = new ArrayList<>();

        /************************************************************************************************/

       /* MedicalPlant plant = new MedicalPlant("Allium sativum L", R.mipmap.ail, "medical plants family");
        medicalPlantsList.add(plant);
        medicalPlantsList.add(plant);
        medicalPlantsList.add(plant);
        medicalPlantsList.add(plant);

        MedicalPlantsFamily family = new MedicalPlantsFamily("medical plantes family 1", medicalPlantsList);
        medicalPlantsFamilyList.add(family);

        medicalPlantsList.add(plant);

        family = new MedicalPlantsFamily("Aedical plantes family 1", medicalPlantsList);
        medicalPlantsFamilyList.add(family);

        medicalPlantsList.add(plant);
        family = new MedicalPlantsFamily("medical plantes family 1", medicalPlantsList);
        medicalPlantsFamilyList.add(family);

        /***********************************************************************************************/


        searchResultListView.setOnItemClickListener(this);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.plante_search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());

        searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setSearchableInfo(searchableInfo);
        searchView.setIconified(false);
        searchView.setQuery(this.queryText,false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: ");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Section> updatedPlantList= new ArrayList<>();
                queryText =  newText  ;
                for (Section section: medicalPlantsFamilyList) {
                    for (MedicalPlant plant:((MedicalPlantsFamily)section).getMedicalPlantList() ) {
                           if(plant.getName().toLowerCase().startsWith(newText.toLowerCase()) && newText.length()>0){
                               updatedPlantList.add(plant);
                           }
                    }
                }

                updateSearchView(updatedPlantList);
                return true;

            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                finish();
                return true;
            }
        });
        return true;

    }

    public void updateSearchView(List<Section> updatedList){
        Collections.sort(updatedList, new Comparator<Section>() {
            @Override
            public int compare(Section o1, Section o2) {
                return ((MedicalPlant)o1).getName().compareTo(((MedicalPlant)o2).getName());
            }
        });

        plantsAdapter = new MedicalPlantsAdapter(SearchPlantesActivity.this,R.layout.listview_plantes,R.layout.plants_family_header,updatedList);
        searchResultListView.setAdapter(plantsAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(SearchPlantesActivity.this,"rendering the plant profile",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(PLANT_QUERY,this.queryText);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
       this.queryText =  savedInstanceState.getString(PLANT_QUERY)  ;
        super.onRestoreInstanceState(savedInstanceState);
    }
}
