package de.hsrm.mi.swt.rheinmainadventure.spiel.api;

import java.util.Arrays;
import java.util.Objects;

/**
 * Wrapper-Objekt für ein Raum-Entity aus der Datenbank.
 * Da es eine schlechte Idee ist direkt mit einer Datenbank verknüpfte Objekte in das Internet zu senden und ohne
 * Prüfung wieder zu speichern, empfiehlt sich die Kapselung in Plain Old Java Objects (POJOs). Ein RaumPOJO wird
 * aktuell für den Level-Editor bereitgestellt, aus diesem Grund ist auch die Darstellung des Rauminhalts vereinfacht.
 */
public class RaumPOJO {
    private long levelID;

    private String benutzername;

    private String levelName;

    private String levelBeschreibung;

    /**
     * Der Rauminhalt als 2D-Array.
     * Das Format ist [x-Position im Raum][y-Position im Raum] = Mobiliar-ID
     */
    private long[][] levelInhalt;

    public RaumPOJO(long levelID,
                    String benutzername,
                    String levelName,
                    String levelBeschreibung,
                    long[][] levelInhalt) {
        this.levelID = levelID;
        this.benutzername = benutzername;
        this.levelName = levelName;
        this.levelBeschreibung = levelBeschreibung;
        this.levelInhalt = levelInhalt;
    }

    public RaumPOJO() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RaumPOJO raumPOJO = (RaumPOJO) o;

        if (levelID != raumPOJO.levelID) return false;
        if (!Objects.equals(benutzername, raumPOJO.benutzername))
            return false;
        if (!Objects.equals(levelName, raumPOJO.levelName)) return false;
        if (!Objects.equals(levelBeschreibung, raumPOJO.levelBeschreibung))
            return false;
        return Arrays.deepEquals(levelInhalt, raumPOJO.levelInhalt);
    }

    @Override
    public int hashCode() {
        int result = (int) (levelID ^ (levelID >>> 32));
        result = 31 * result + (benutzername != null ? benutzername.hashCode() : 0);
        result = 31 * result + (levelName != null ? levelName.hashCode() : 0);
        result = 31 * result + (levelBeschreibung != null ? levelBeschreibung.hashCode() : 0);
        result = 31 * result + Arrays.deepHashCode(levelInhalt);
        return result;
    }

    public String getLevelBeschreibung() {
        return levelBeschreibung;
    }

    public void setLevelBeschreibung(String levelBeschreibung) {
        this.levelBeschreibung = levelBeschreibung;
    }

    public String getBenutzername() {
        return benutzername;
    }

    public void setBenutzername(String benutzername) {
        this.benutzername = benutzername;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public long[][] getLevelInhalt() {
        return levelInhalt;
    }

    public void setLevelInhalt(long[][] levelInhalt) {
        this.levelInhalt = levelInhalt;
    }

    public long getLevelID() {
        return levelID;
    }

    public void setLevelID(long levelID) {
        this.levelID = levelID;
    }
}
