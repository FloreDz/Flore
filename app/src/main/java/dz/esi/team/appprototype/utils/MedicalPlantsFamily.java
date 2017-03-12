package dz.esi.team.appprototype.utils;

/**
 * Created by azeddine on 06/03/17.
 */

public class MedicalPlantsFamily implements Section{
    private String MedicalPlantsFamilyName;

    public MedicalPlantsFamily(String medicalPlantsFamilyName) {
        this.setMedicalPlantsFamilyName(medicalPlantsFamilyName);
    }

    public String getMedicalPlantsFamilyName() {
        return MedicalPlantsFamilyName;
    }

    public void setMedicalPlantsFamilyName(String medicalPlantsFamilyName) {
        MedicalPlantsFamilyName = medicalPlantsFamilyName;
    }

    @Override
    public boolean isSection() {
        return true;
    }

    @Override
    public String getTitle() {
       return  this.getMedicalPlantsFamilyName();
    }
}
