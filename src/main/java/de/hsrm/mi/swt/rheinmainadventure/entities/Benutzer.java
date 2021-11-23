package de.hsrm.mi.swt.rheinmainadventure.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;


import javax.persistence.*;
import java.util.StringJoiner;


/**
 * Dummy Code, um die Verwendung der Data.sql zu zeigen. Nicht Übernehmen!
 */
@Entity
@Getter
@Setter
@ToString
@Table(name = "benutzer")
@DynamicUpdate
public class Benutzer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) /*Sequence For better performance*/
  private Long id;

  @Column(unique = true, nullable = false)
  private String benutzername;

  @Column
  private boolean online;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Column(nullable = false)
  private String passwort;

  public Benutzer(){

  }

  public Benutzer(String benutzername, String passwort) {
    this.benutzername = benutzername;
    this.passwort = passwort;
    this.online = false;
  }

  /**
  @Override
  public String toString() {
    return new StringJoiner(", ", Benutzer.class.getSimpleName() + "[", "]")
        .add("benutzername='" + benutzername + "'")
        .add("passwort='" + passwort + "'")
        .toString();
  }
  **/

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
}
