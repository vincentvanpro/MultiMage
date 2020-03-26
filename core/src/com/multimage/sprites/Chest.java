package com.multimage.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.multimage.MultiMage;
import com.multimage.screens.PlayScreen;

public class Chest extends InteractiveTileObject {
    public Chest(PlayScreen screen, Rectangle bounds) {
        super(screen, bounds);
        fixture.setUserData(this);
        setCategoryFilter(MultiMage.CHEST_BIT);
    }

    @Override
    public void onBodyHit() {
        Gdx.app.log("Chest", "collision");
        setCategoryFilter(MultiMage.DESTROYED_BIT);
        getCell().setTile(null);
    }
}
