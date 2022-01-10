package de.hsrm.mi.swt.rheinmainadventure.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.List;

/**
 * Ein Level, das von Benutzern gespielt werden kann.
 * Die Level-Klasse enthält, neben den einzelnen Räumen des Levels, Meta-Informationen zum Spielablauf und wer es erstellt hat.
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long levelId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int minSpieler;

    @Column(nullable = false)
    private int maxSpieler;

    // Durchschnittliche Bewertung aller Nutzer oder so
    @Column
    private byte bewertung;

    @JsonIgnore
    @OneToMany(mappedBy = "level")
    List<Raum> raeume;

    @ManyToOne
    @JoinColumn(name = "benutzer_id")
    private Benutzer ersteller;

    @JsonIgnore
    @Version
    private Long version;

    @Override
    public String toString() {
        return "Level{" + "levelId=" + levelId + ", name='" + name + '\'' + ", minSpieler=" + minSpieler + ", maxSpieler=" + maxSpieler + ", version=" + version + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Level level = (Level) o;

        if (getMinSpieler() != level.getMinSpieler()) return false;
        if (getMaxSpieler() != level.getMaxSpieler()) return false;
        if (!getLevelId().equals(level.getLevelId())) return false;
        return getName().equals(level.getName());
    }

    @Override
    public int hashCode() {
        int result = getLevelId().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getMinSpieler();
        result = 31 * result + getMaxSpieler();
        return result;
    }

    public List<Raum> getRaeume() {
        return raeume;
    }

    public void setRaeume(List<Raum> raeume) {
        this.raeume = raeume;
    }

    public byte getBewertung() {
        return bewertung;
    }

    public void setBewertung(byte bewertung) {
        this.bewertung = bewertung;
    }

    public Long getLevelId() {
        return levelId;
    }

    public void setLevelId(Long levelId) {
        this.levelId = levelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinSpieler() {
        return minSpieler;
    }

    public void setMinSpieler(int minSpieler) {
        this.minSpieler = minSpieler;
    }

    public int getMaxSpieler() {
        return maxSpieler;
    }

    public void setMaxSpieler(int maxSpieler) {
        this.maxSpieler = maxSpieler;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Benutzer getErsteller() {
        return ersteller;
    }

    public void setErsteller(Benutzer ersteller) {
        this.ersteller = ersteller;
    }
}
