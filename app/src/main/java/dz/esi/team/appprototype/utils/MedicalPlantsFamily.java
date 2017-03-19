package dz.esi.team.appprototype.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by azeddine on 06/03/17.
 */

public class MedicalPlantsFamily implements Section{
    private String medicalPlantsFamilyName;
    private List<MedicalPlant> medicalPlantList;
    private  int familySize ;

    public MedicalPlantsFamily(){}
    public MedicalPlantsFamily(String medicalPlantsFamilyName) {
        this.setMedicalPlantsFamilyName(medicalPlantsFamilyName );
    }
    public MedicalPlantsFamily(String medicalPlantsFamilyName, List medicalPlantList) {
        this.setMedicalPlantsFamilyName(medicalPlantsFamilyName);
        this.setMedicalPlantList(medicalPlantList);
        this.setFamilySize(medicalPlantList.size());
    }

    public String getMedicalPlantsFamilyName() {
        return medicalPlantsFamilyName;
    }
    public void setMedicalPlantsFamilyName(String medicalPlantsFamilyName) {
         this.medicalPlantsFamilyName=medicalPlantsFamilyName;


    }

    public List<MedicalPlant> getMedicalPlantList() {
        return this.medicalPlantList;
    }
    public void setMedicalPlantList(List<MedicalPlant> medicalPlantList) {
        this.medicalPlantList = medicalPlantList;
    }

    public int getFamilySize() {
        return familySize;
    }
    public void setFamilySize(int familySize) {
        this.familySize = familySize;
    }

    public void addPlants(MedicalPlant o){
        this.medicalPlantList.add(o);
    }


    @Override
    public boolean isViewHeader() {
        return true;
    }


}
