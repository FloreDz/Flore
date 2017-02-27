package dz.esi.team.appprototype;

/**
 * Created by azeddine on 21/02/17.
 */

public class MedicalPlante {
    private  String name;
    private  String famillyName;
    private  String imagePath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamillyName() {
        return famillyName;
    }

    public void setFamillyName(String famillyName) {
        this.famillyName = famillyName;
    }

    public MedicalPlante(String famillyName, String name) {
        this.famillyName = famillyName;
        this.name = name;
    }


}
