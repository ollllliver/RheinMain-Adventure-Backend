package de.hsrm.mi.swt.rheinmainadventure.model;

public class Element {
    int y;
    int x;
    int e;

    public Element(Object ele) throws NoSuchFieldException {
        this.y = Integer.parseInt(ele.getClass().getField("y").toString());
        this.x = Integer.parseInt(ele.getClass().getField("x").toString());
        this.e = Integer.parseInt(ele.getClass().getField("e").toString());

    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public int getE() {
        return e;
    }

    @Override
    public String toString() {
        return "Element{" +
                "y=" + y +
                ", x=" + x +
                ", e=" + e +
                '}';
    }
}


