package de.hsrm.mi.swt.rheinmainadventure.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty.Access;

/**
 * Benutzer-Entity für das Benutzer-Repository
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name="benutzer")
public class Benutzer {

  //automatisch generierte ID
  @Id
  @GeneratedValue
  private Long id;
  //Spalte Loginname im Repository
  @Column(name="BENUTZERNAME", unique=true)
  private String benutzername;

  private String passwort;

  //automatisch generierte Versionsnummer
  @Version
  @GeneratedValue
  @JsonProperty(access=Access.WRITE_ONLY)
  private Long version;

  //Getter/Setter/hashCode/equals/toString
  public String getBenutzername() {
    return benutzername;
  }

  public void setBenutzername(String loginname) {
    this.benutzername = loginname;
  }

  public String getPasswort() {
    return passwort;
  }

  public void setPasswort(String passwort) {
    this.passwort = passwort;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((benutzername == null) ? 0 : benutzername.hashCode());

    result = prime * result + ((passwort == null) ? 0 : passwort.hashCode());
    result = prime * result + ((version == null) ? 0 : version.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Benutzer other = (Benutzer) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (benutzername == null) {
      if (other.benutzername != null)
        return false;
    } else if (!benutzername.equals(other.benutzername))
    if (passwort == null) {
      if (other.passwort != null)
        return false;
    } else if (!passwort.equals(other.passwort))
      return false;
    if (version == null) {
      if (other.version != null)
        return false;
    } else if (!version.equals(other.version))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Benutzer [id=" + id + ", benutzername=" + benutzername
            + ", passwort=" + passwort + ", version=" + version + "]";
  }
}
