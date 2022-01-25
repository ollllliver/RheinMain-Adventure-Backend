package de.hsrm.mi.swt.rheinmainadventure.controller;

/**
 * Benutzer-POJO als Spiegelung zum Benutzer-Entity, um den Benutzercontroller abzusichern
 */
public class BenutzerPOJO {

    private String benutzername;

    private String passwort;


    public BenutzerPOJO(String benutzername, String passwort) {
        this.benutzername = benutzername;
        this.passwort = passwort;
    }

    public BenutzerPOJO() {
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BenutzerPOJO benutzer = (BenutzerPOJO) o;

        return getBenutzername().equals(benutzer.getBenutzername());
    }

    @Override
    public String toString() {
        return "Benutzer{" +
                ", benutzername='" + benutzername + '\'' +
                ", passwort='" + passwort + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return getBenutzername().hashCode();
    }


    public String getBenutzername() {
        return benutzername;
    }

    public void setBenutzername(String benutzername) {
        this.benutzername = benutzername;
    }

    public String getPasswort() {
        return passwort;
    }

    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }
}
