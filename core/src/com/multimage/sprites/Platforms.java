package com.multimage.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.multimage.MultiMage;
import com.multimage.item.ItemDef;
import com.multimage.screens.PlayScreen;

public class Platforms extends InteractiveTileObject {
    public Platforms(PlayScreen screen, Rectangle bounds) {
        super(screen, bounds);
        fixture.setUserData(this);
        setCategoryFilter(MultiMage.GROUND_BIT);
    }

    @Override
    public void onBodyHit() {
        Gdx.app.log("Platform", "collision");
    }
}