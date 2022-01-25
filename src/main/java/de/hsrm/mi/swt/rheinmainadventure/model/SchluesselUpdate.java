package de.hsrm.mi.swt.rheinmainadventure.model;

/**
 * Klasse die aus der anzahl Schluesseln in einer bestimmten Lobby und den
 * Koordinaten des interagierten Objectes besteht. Dient als Packet, welches
 * allen Subscribern im Frontend geschickt wird.
 */

public class SchluesselUpdate {
    private String ObjectName;
    private int anzSchluessel;
    private String koordinatenArray;

    public SchluesselUpdate(String ObjectName, int anzSchluessel, String koordinatenArray) {
        this.ObjectName = ObjectName;
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

    public String getObjectName() {
        return ObjectName;
    }

    public void setObjectName(String objectName) {
        ObjectName = objectName;
    }

}
