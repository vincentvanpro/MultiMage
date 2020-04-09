package com.multimage.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.multimage.MultiMage;
import com.multimage.screens.PlayScreen;

public class Platforms extends InteractiveTileObject {
    public Platforms(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MultiMage.PLATFORM_BIT);
    }

    @Override
    public void onBodyHit() {
        Gdx.app.log("Platform", "collision");
    }
}