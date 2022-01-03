package de.hsrm.mi.swt.rheinmainadventure.entities.mobiliar;

import de.hsrm.mi.swt.rheinmainadventure.entities.RaumMobiliar;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

/**
 * Mobiliar sind in Räumen platzierbare Objekte.
 * Die Mobiliar-Klasse enthält neben der URI für das tatsächliche 3D-Modell im gtlf-Format
 * auch noch den Klarnamen für das Mobiliar.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Mobiliar {

    @Id
    @GeneratedValue
    private Long mobiliarId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String modellURI;

    @JsonIgnore
    @OneToMany(mappedBy = "mobiliar")
    private Set<RaumMobiliar> raumMobiliar;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mobiliar mobiliar = (Mobiliar) o;

        if (!getName().equals(mobiliar.getName())) return false;
        return getModellURI().equals(mobiliar.getModellURI());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getModellURI().hashCode();
        return result;
    }

    public Long getMobiliarId() {
        return mobiliarId;
    }

    public void setMobiliarId(Long mobiliarId) {
        this.mobiliarId = mobiliarId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModellURI() {
        return modellURI;
    }

    public void setModellURI(String modell) {
        this.modellURI = modell;
    }

    public Set<RaumMobiliar> getRaumMobiliar() {
        return raumMobiliar;
    }

    public void setRaumMobiliar(Set<RaumMobiliar> raumMobiliar) {
        this.raumMobiliar = raumMobiliar;
    }
}
