package de.hsrm.mi.swt.rheinmainadventure.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import javax.persistence.*;
import java.util.List;

/**
 * Benutzer-Entity für das Benutzer-Repository
 */
@Entity
public class Benutzer {

    //automatisch generierte ID
    @Id
    @GeneratedValue
    private Long benutzerId;

    //Spalte Loginname im Repository
    @Column(nullable = false, unique = true)
    private String benutzername;

    @JsonProperty(access = Access.WRITE_ONLY)
    @Column(nullable = false)
    private String passwort;

    @OneToMany(mappedBy = "ersteller")
    @JsonIgnore
    private List<Level> erstellteLevel;

    //automatisch generierte Versionsnummer
    @Version
    @JsonIgnore
    private Long version;

    public Benutzer(Long benutzerId, String benutzername, String passwort) {
        this.benutzerId = benutzerId;
        this.benutzername = benutzername;
        this.passwort = passwort;
    }

    public Benutzer() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Benutzer benutzer = (Benutzer) o;

        return getBenutzername().equals(benutzer.getBenutzername());
    }

    @Override
    public String toString() {
        return "Benutzer{" +
                "benutzerId=" + benutzerId +
                ", benutzername='" + benutzername + '\'' +
                ", passwort='" + passwort + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return getBenutzername().hashCode();
    }

    public Long getBenutzerId() {
        return benutzerId;
    }

    public void setBenutzerId(Long benutzerId) {
        this.benutzerId = benutzerId;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
