package de.hsrm.mi.swt.rheinmainadventure.model;

/**
 * Die Eingeschaften eines Spielers. Dient dazu, dass man komplette Eigenschaften eines Spielers als ein einziges Objekt verwalten kann.
 */

//Im Moment haben wir nur eine Eigenschaft (Position). Brauchen wir dafür eine extra Klasse?
//  Sind die Attribute eines Spielers nicht seine Eigenschaften?
public class SpielerEigenschaften {


    private Position position;

    public SpielerEigenschaften() {
        this.position = new Position(0, 0, 0);
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

}
