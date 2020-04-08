package com.multimage.sprites;

import com.badlogic.gdx.maps.MapObject;
import com.multimage.MultiMage;
import com.multimage.screens.PlayScreen;

public class Ground extends InteractiveTileObject {
    public Ground(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MultiMage.GROUND_BIT);
    }

    @Override
    public void onBodyHit() { }
}
