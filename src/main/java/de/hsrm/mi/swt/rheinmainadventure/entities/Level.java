package de.hsrm.mi.swt.rheinmainadventure.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

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

    @Column
    private String beschreibung;

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

    public Level(String name, String beschreibung, byte bewertung, List<Raum> raeume, Benutzer ersteller) {
        this.name = name;
        this.beschreibung = beschreibung;
        this.bewertung = bewertung;
        this.raeume = raeume;
        this.ersteller = ersteller;
    }

    public Level() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Level level = (Level) o;

        if (!Objects.equals(name, level.name)) return false;
        if (!Objects.equals(beschreibung, level.beschreibung)) return false;
        if (!Objects.equals(raeume, level.raeume)) return false;
        return Objects.equals(ersteller, level.ersteller);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (beschreibung != null ? beschreibung.hashCode() : 0);
        result = 31 * result + (raeume != null ? raeume.hashCode() : 0);
        result = 31 * result + (ersteller != null ? ersteller.hashCode() : 0);
        return result;
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

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public byte getBewertung() {
        return bewertung;
    }

    public void setBewertung(byte bewertung) {
        this.bewertung = bewertung;
    }

    public List<Raum> getRaeume() {
        return raeume;
    }

    public void setRaeume(List<Raum> raeume) {
        this.raeume = raeume;
    }

    public Benutzer getErsteller() {
        return ersteller;
    }

    public void setErsteller(Benutzer ersteller) {
        this.ersteller = ersteller;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
