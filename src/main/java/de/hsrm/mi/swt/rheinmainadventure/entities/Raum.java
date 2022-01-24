package de.hsrm.mi.swt.rheinmainadventure.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.List;

/**
 * Ein einzelner Raum in einem Level.
 * Ein Raum enthält Mobiliar und kommt an einer bestimmten Stelle im Level dran. Räume stehen für sich.
 */
@Entity
public class Raum {

    @Id
    @GeneratedValue
    private Long raumId;

    @Column(nullable = false)
    private int raumIndex;

    @ManyToOne
    @JsonIgnore
    private Level level;

    @OneToMany(mappedBy = "raum")
    @JsonIgnore
    private List<RaumMobiliar> raumMobiliar;

    public Raum(int raumIndex, List<RaumMobiliar> raumMobiliar) {
        this.raumIndex = raumIndex;
        this.raumMobiliar = raumMobiliar;
    }

    public Raum() {
    }

    @Override
    public String toString() {
        return "Raum{" +
                "raumId=" + raumId +
                ", folgeImLevel=" + raumIndex +
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

    public int getRaumIndex() {
        return raumIndex;
    }

    public void setRaumIndex(int folgeImLevel) {
        this.raumIndex = folgeImLevel;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public List<RaumMobiliar> getRaumMobiliar() {
        return raumMobiliar;
    }

    @Transactional
    public void setRaumMobiliar(List<RaumMobiliar> raumMobiliar) {
        this.raumMobiliar = raumMobiliar;
    }
}
