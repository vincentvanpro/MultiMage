package com.multimage.network.packets;

import com.multimage.sprites.Mage;

public class Moving {

    public Mage.State state = Mage.State.WALKING;
    public boolean walkingRight;
    public Position post = new Position();

}
