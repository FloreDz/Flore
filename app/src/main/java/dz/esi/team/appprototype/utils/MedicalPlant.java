package dz.esi.team.appprototype.utils;

/**
 * Created by azeddine on 21/02/17.
 */

public class MedicalPlant implements Section{
    private String name;
    private MedicalPlantsFamily family;



    public MedicalPlant(MedicalPlantsFamily  familyName, String name) {
        this.setFamily(familyName);
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public MedicalPlantsFamily getFamily() {
        return family;
    }
    public void setFamily(MedicalPlantsFamily  family) {
        this.family = family;
    }


    @Override
    public boolean isSection() {
        return false;
    }

    @Override
    public String getTitle() {
        return this.getName();
    }
}
