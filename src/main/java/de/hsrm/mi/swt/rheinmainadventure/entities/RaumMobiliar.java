package de.hsrm.mi.swt.rheinmainadventure.entities;

import javax.persistence.*;

/**
 * Diese Klasse stellt die N zu M Beziehung zwischen Raum und Mobiliar dar.
 * Da Mobiliar an einer gewissen Stelle im Raum steht, ist diese Klasse vonnöten um das zusätzliche Feld abzudecken.
 *
 * @see <a href="https://www.baeldung.com/jpa-many-to-many#many-to-many-with-a-new-entity">Baeldung Tutorial</a>
 */
@Entity
public class RaumMobiliar {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long raumMobiliarId;

    @ManyToOne
    private Raum raum;

    @ManyToOne
    private Mobiliar mobiliar;

    @Column(nullable = false)
    private int positionX;

    @Column(nullable = false)
    private int positionY;

    public RaumMobiliar(Mobiliar mobiliar, Raum raum, int x, int y) {
        this.mobiliar = mobiliar;
        this.raum = raum;
        this.positionX = x;
        this.positionY = y;
    }

    public RaumMobiliar() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RaumMobiliar that = (RaumMobiliar) o;

        return raumMobiliarId == that.raumMobiliarId;
    }

    @Override
    public int hashCode() {
        return (int) (raumMobiliarId ^ (raumMobiliarId >>> 32));
    }

    public long getRaumMobiliarId() {
        return raumMobiliarId;
    }

    public void setRaumMobiliarId(long id) {
        this.raumMobiliarId = id;
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