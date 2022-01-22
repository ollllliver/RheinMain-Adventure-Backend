package de.hsrm.mi.swt.rheinmainadventure.model;

public class SchluesselUpdate {
    private int anzSchluessel;
    private String koordinatenArray;


    public SchluesselUpdate(int anzSchluessel, String koordinatenArray){
        this.anzSchluessel = anzSchluessel;
        this.koordinatenArray = koordinatenArray;
    }


    public int getAnzSchluessel() {
        return anzSchluessel;
    }


    public void setAnzSchluessel(int anzSchluessel) {
        this.anzSchluessel = anzSchluessel;
    }


    public String getKoordinatenArray() {
        return koordinatenArray;
    }


    public void setKoordinatenArray(String koordinatenArray) {
        this.koordinatenArray = koordinatenArray;
    }


}
