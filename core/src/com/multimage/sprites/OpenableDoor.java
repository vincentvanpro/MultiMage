package com.multimage.sprites;

import com.badlogic.gdx.math.Rectangle;
import com.multimage.screens.PlayScreen;

public class OpenableDoor extends InteractiveTileObject {
    public OpenableDoor(PlayScreen screen, Rectangle bounds) {
        super(screen, bounds);
    }

    @Override
    public void onBodyHit() {

    }
}
