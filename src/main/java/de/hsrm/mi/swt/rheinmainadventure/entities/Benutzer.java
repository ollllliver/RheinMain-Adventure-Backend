package de.hsrm.mi.swt.rheinmainadventure.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.StringJoiner;


/**
 * Dummy Code, um die Verwendung der Data.sql zu zeigen. Nicht Übernehmen!
 */
@Entity
public class Benutzer {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column(unique = true, nullable = false)
  private String benutzername;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Column(nullable = false)
  private String passwort;

  public Benutzer() {
  }

  public Benutzer(String benutzername, String passwort) {
    this.benutzername = benutzername;
    this.passwort = passwort;
    this.id = 3l; //TODO : RAUSLÖSCHEN IST NUR ZUM TESTEN GEDACHT
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

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getPasswort() {
    return passwort;
  }

  public void setPasswort(String passwort) {
    this.passwort = passwort;
  }
}
