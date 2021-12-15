package de.hsrm.mi.swt.rheinmainadventure.spiel;

public class SpielerKlasse {
    private String name;
    private String avatar;
    private int positionX;
    private int positionY;

    public SpielerKlasse(String name, String avatar, int positionX, int positionY) {
        this.name = name;
        this.avatar = avatar;
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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
