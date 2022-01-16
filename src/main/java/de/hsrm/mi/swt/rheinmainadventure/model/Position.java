package de.hsrm.mi.swt.rheinmainadventure.model;

public class Position {

    private float x;
    private float y;
    private float z;

    public Position() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Position(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Position(float x, float y) {
        this.x = x;
        this.y = y;
        this.z = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (Float.compare(position.x, x) != 0) return false;
        if (Float.compare(position.y, y) != 0) return false;
        return Float.compare(position.z, z) == 0;
    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        result = 31 * result + (z != +0.0f ? Float.floatToIntBits(z) : 0);
        return result;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }

    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }


    public String toString() {
        return "pos: [x=" + x + ", y=" + y + ", z=" + z + "]";
    }
}
