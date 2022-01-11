package de.hsrm.mi.swt.rheinmainadventure.model;

public class Position {

    private float x,y,z;

    public Position() {
        this.x=0;
        this.y=0;
        this.z=0;
    }

    public Position(float x, float y, float z) {
        this.x=x;
        this.y=y;
        this.z=z;
    }

    public Position(float x, float y) {
        this.x=x;
        this.y=y;
        this.z=0;
    }

    public float getX(){
        return this.x;
    }

    public float getY(){
        return this.y;
    }

    public float getZ(){
        return this.z;
    }

    public void setPosition(float x, float y, float z) {
        this.x=x;
        this.y=y;
        this.z=z;
    }

    public void setPosition(float x, float y) {
        this.x=x;
        this.y=y;
    }


    public String toString(){
        return "pos: [x=" + x+ ", y=" + y + ", z=" + z + "]";
    }
}
