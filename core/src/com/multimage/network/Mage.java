package com.multimage.network;

// ordinary Mage class
public class Mage {

    public enum State { JUMPING, FALLING, WALKING, STANDING, ATTACK }

    public int id = -1;
    public String name;
    public float PosX;
    public float PosY;


    private boolean setToDestroy;
    private boolean destroyed;


    public Mage(int id, float x, float y) {
        setToDestroy = false;
        destroyed = false;
        this.id = id;
        PosX = x;
        PosY = y;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPosX() {
        return PosX;
    }

    public void setPosX(float PostX) {
        this.PosX = PostX;
    }

    public float getPosY() {
        return PosY;
    }

    public void setPosY(float PostY) {
        this.PosY = PostY;
    }
}
