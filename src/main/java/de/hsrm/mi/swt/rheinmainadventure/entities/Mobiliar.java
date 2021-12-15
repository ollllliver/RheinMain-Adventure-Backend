package de.hsrm.mi.swt.rheinmainadventure.entities;

import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Mobiliar {

    @Id
    @GeneratedValue
    private Long mobiliarId;

    @Column(nullable = false)
    private String name;

    // TODO: Später anpassen, wenn Task 32 gemacht wird
    // Feedback von Tim, Julian und/oder Hans einholen
    @Column(nullable = false)
    private String modell;

    @JsonIgnore
    @OneToMany(mappedBy = "mobiliar")
    private Set<RaumMobiliar> raumMobiliar;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mobiliar mobiliar = (Mobiliar) o;

        if (!getName().equals(mobiliar.getName())) return false;
        return getModell().equals(mobiliar.getModell());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getModell().hashCode();
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

    public String getModell() {
        return modell;
    }

    public void setModell(String modell) {
        this.modell = modell;
    }

    public Set<RaumMobiliar> getRaumMobiliar() {
        return raumMobiliar;
    }

    public void setRaumMobiliar(Set<RaumMobiliar> raumMobiliar) {
        this.raumMobiliar = raumMobiliar;
    }
}
