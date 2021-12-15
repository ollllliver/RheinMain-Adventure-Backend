package de.hsrm.mi.swt.rheinmainadventure.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import javax.persistence.*;
import java.util.Set;

/**
 * Benutzer-Entity für das Benutzer-Repository
 */
@Entity
@Table(name = "benutzer")
public class Benutzer {

    //automatisch generierte ID
    @Id
    @GeneratedValue
    private Long id;

    //Spalte Loginname im Repository
    @Column(nullable = false, unique = true)
    private String benutzername;

    @JsonProperty(access = Access.WRITE_ONLY)
    @Column(nullable = false)
    private String passwort;

    @OneToMany(mappedBy = "ersteller")
    @JsonIgnore
    private Set<Level> erstellteLevel;

    //automatisch generierte Versionsnummer
    @Version
    @JsonIgnore
    private Long version;

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
            "id=" + id +
            ", benutzername='" + benutzername + '\'' +
            ", passwort='" + passwort + '\'' +
            '}';
    }

    @Override
    public int hashCode() {
        return getBenutzername().hashCode();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
