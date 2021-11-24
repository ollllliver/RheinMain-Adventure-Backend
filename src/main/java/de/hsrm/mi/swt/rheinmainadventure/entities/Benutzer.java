package de.hsrm.mi.swt.rheinmainadventure.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.StringJoiner;

@Entity

@Table(name = "benutzer")
@DynamicUpdate
public class Benutzer {

  @Column(unique = true, nullable = false)
  @Id
  private String benutzername;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Column(nullable = false)
  private String passwort;

  public Benutzer(){

  }

  public Benutzer(String benutzername, String passwort) {
    this.benutzername = benutzername;
    this.passwort = passwort;

  }


  @Override
  public String toString() {
    return new StringJoiner(", ", Benutzer.class.getSimpleName() + "[", "]")
        .add("benutzername='" + benutzername + "'")
        .add("passwort='" + passwort + "'")
        .toString();
  }


  /**
   * Zwei Nutzer sind dann gleich, wenn Sie den gleichen Nutzernamen haben.
   *
   * @param o
   * @return
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Benutzer benutzer = (Benutzer) o;

    return benutzername.equals(benutzer.benutzername);
  }

  @Override
  public int hashCode() {
    return benutzername.hashCode();
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
