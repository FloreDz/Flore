package dz.esi.team.appprototype;

/**
 * Created by azeddine on 21/02/17.
 */

public class MedicalPlant {
    private String name;
    private String familyName;
    private String imagePath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public MedicalPlant(String familyName, String name) {
        this.familyName = familyName;
        this.name = name;
    }


}
