package de.hsrm.mi.swt.rheinmainadventure.model;

public class SchluesselUpdate {
    private int anzSchluessel;
    private int id;


    public SchluesselUpdate(int anzSchluessel, int id){
        this.anzSchluessel = anzSchluessel;
        this.id = id;
    }


    public int getAnzSchluessel() {
        return anzSchluessel;
    }


    public void setAnzSchluessel(int anzSchluessel) {
        this.anzSchluessel = anzSchluessel;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


}
