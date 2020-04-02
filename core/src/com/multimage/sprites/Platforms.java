package com.multimage.sprites;

import com.badlogic.gdx.math.Rectangle;
import com.multimage.screens.PlayScreen;

public class Platforms extends InteractiveTileObject {
    public Platforms(PlayScreen screen, Rectangle bounds) {
        super(screen, bounds);
        fixture.setUserData(this);
    }

    @Override
    public void onBodyHit() { }
}