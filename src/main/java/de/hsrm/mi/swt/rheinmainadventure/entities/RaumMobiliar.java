package de.hsrm.mi.swt.rheinmainadventure.entities;

import javax.persistence.*;

/**
 * Diese Klasse stellt die N zu M Beziehung zwischen Raum und Mobiliar dar.
 * Da Mobiliar an einer gewissen Stelle im Raum steht, ist diese Klasse vonnöten um das zusätzliche Feld abzudecken.
 * Siehe https://www.baeldung.com/jpa-many-to-many#many-to-many-with-a-new-entity
 */
@Entity
public class RaumMobiliar {

    @Id
    private long id;

    @ManyToOne
    @JoinColumn(name = "raum_id")
    private Raum raum;

    @ManyToOne
    @JoinColumn(name = "mobiliar_id")
    private Mobiliar mobiliar;

    @Column(nullable = false)
    private int positionX;

    @Column(nullable = false)
    private int positionY;



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Raum getRaum() {
        return raum;
    }

    public void setRaum(Raum raum) {
        this.raum = raum;
    }

    public Mobiliar getMobiliar() {
        return mobiliar;
    }

    public void setMobiliar(Mobiliar mobiliar) {
        this.mobiliar = mobiliar;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }
}