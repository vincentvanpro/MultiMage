package com.multimage.sprites;

import com.badlogic.gdx.math.Rectangle;
import com.multimage.MultiMage;
import com.multimage.screens.PlayScreen;

public class Ground extends InteractiveTileObject {
    public Ground(PlayScreen screen, Rectangle bounds) {
        super(screen, bounds);
        fixture.setUserData(this);
        setCategoryFilter(MultiMage.GROUND_BIT);
    }

    @Override
    public void onBodyHit() { }
}
