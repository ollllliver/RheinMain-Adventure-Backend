package de.hsrm.mi.swt.rheinmainadventure.benutzer;

public class Benutzer2 {

    private final Integer benutzerId;
    private final String benutzerName;

    public Benutzer2(Integer benutzerId, String benutzerName){
        this.benutzerId = benutzerId;
        this.benutzerName = benutzerName;
    }

    public Integer getBenutzerId() {
        return benutzerId;
    }

    public String getBenutzerName() {
        return benutzerName;
    }

    @Override
    public String toString() {
        return "Benutzer2{" +
                "benutzerId=" + benutzerId +
                ", benutzerName='" + benutzerName + '\'' +
                '}';
    }
}
