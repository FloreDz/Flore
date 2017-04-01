package dz.esi.team.appprototype.utils;

/**
 * Created by azeddine on 21/02/17.
 */

public class MedicalPlant implements Section{
    private final String name;
    private final String familyName;
    private final int imageSource;



    public MedicalPlant(String name,int imageSource,String familyName) {

        this.name = name;
        this.imageSource = imageSource  ;
        this.familyName = familyName ;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return imageSource;
    }

    @Override
    public boolean isViewHeader() {
        return false;
    }




}
