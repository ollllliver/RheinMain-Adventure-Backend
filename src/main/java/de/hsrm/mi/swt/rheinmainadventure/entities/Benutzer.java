package de.hsrm.mi.swt.rheinmainadventure.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import javax.persistence.*;
import java.util.Collections;
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

    @Column
    private boolean active;

    @Column
    private String roles;

    public Benutzer(Long benutzerId, String benutzername, String passwort, boolean active, String roles) {
        this.benutzerId = benutzerId;
        this.benutzername = benutzername;
        this.passwort = passwort;
        this.active = active;
        this.roles = roles;
    }
    public Benutzer(String benutzername, String passwort, List<Level> erstellteLevel) {
        this.benutzername = benutzername;
        this.passwort = passwort;
        this.erstellteLevel = erstellteLevel;
    }

    public Benutzer(String benutzername, String passwort) {
        this.benutzername = benutzername;
        this.passwort = passwort;
        this.erstellteLevel = Collections.emptyList();
    }

    public Benutzer() {
        this.erstellteLevel = Collections.emptyList();
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

    public List<Level> getErstellteLevel() {
        return erstellteLevel;
    }

    public void setErstellteLevel(List<Level> erstellteLevel) {
        this.erstellteLevel = erstellteLevel;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
