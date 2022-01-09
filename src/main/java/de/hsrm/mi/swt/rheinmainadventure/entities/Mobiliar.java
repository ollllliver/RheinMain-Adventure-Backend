package de.hsrm.mi.swt.rheinmainadventure.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

/**
 * Mobiliar sind in Räumen platzierbare Objekte.
 * Die Mobiliar-Klasse enthält neben der URI für das tatsächliche 3D-Modell im gtlf-Format
 * auch noch den Klarnamen für das Mobiliar und seinen Typ.
 */
@Entity
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

    @Column
    private Mobiliartyp mobiliartyp;

    public Mobiliar(String name, String modellURI, Mobiliartyp mobiliartyp) {
        this.name = name;
        this.modellURI = modellURI;
        this.mobiliartyp = mobiliartyp;
    }

    public Mobiliar() {}
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mobiliar mobiliar = (Mobiliar) o;

        if (!Objects.equals(mobiliarId, mobiliar.mobiliarId)) return false;
        if (!Objects.equals(name, mobiliar.name)) return false;
        if (!Objects.equals(modellURI, mobiliar.modellURI)) return false;
        return mobiliartyp == mobiliar.mobiliartyp;
    }

    @Override
    public int hashCode() {
        int result = mobiliarId != null ? mobiliarId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (modellURI != null ? modellURI.hashCode() : 0);
        result = 31 * result + (mobiliartyp != null ? mobiliartyp.hashCode() : 0);
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

    public void setModellURI(String modellURI) {
        this.modellURI = modellURI;
    }

    public Set<RaumMobiliar> getRaumMobiliar() {
        return raumMobiliar;
    }

    public void setRaumMobiliar(Set<RaumMobiliar> raumMobiliar) {
        this.raumMobiliar = raumMobiliar;
    }

    public Mobiliartyp getMobiliartyp() {
        return mobiliartyp;
    }

    public void setMobiliartyp(Mobiliartyp mobiliartyp) {
        this.mobiliartyp = mobiliartyp;
    }
}
