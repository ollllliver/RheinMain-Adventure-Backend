package de.hsrm.mi.swt.rheinmainadventure.entities;

import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Raum {

    @Id
    @GeneratedValue
    private Long raumId;

    @Column(nullable = false)
    private int folgeImLevel;

    @ManyToOne
    @JsonIgnore
    private Level level;

    @OneToMany(mappedBy = "raum")
    @JsonIgnore
    private Set<RaumMobiliar> raumMobiliar;

    @Override
    public String toString() {
        return "Raum{" +
                "raumId=" + raumId +
                ", folgeImLevel=" + folgeImLevel +
                ", level=" + level +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Raum raum = (Raum) o;

        return getRaumId().equals(raum.getRaumId());
    }

    @Override
    public int hashCode() {
        return getRaumId().hashCode();
    }

    public Long getRaumId() {
        return raumId;
    }

    public void setRaumId(Long raumId) {
        this.raumId = raumId;
    }

    public int getFolgeImLevel() {
        return folgeImLevel;
    }

    public void setFolgeImLevel(int folgeImLevel) {
        this.folgeImLevel = folgeImLevel;
    }
}
