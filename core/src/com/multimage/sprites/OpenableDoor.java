package com.multimage.sprites;

import com.badlogic.gdx.math.Rectangle;
import com.multimage.MultiMage;
import com.multimage.screens.PlayScreen;

public class OpenableDoor extends InteractiveTileObject {
    public OpenableDoor(PlayScreen screen, Rectangle bounds) {
        super(screen, bounds);
        fixture.setUserData(this);
        setCategoryFilter(MultiMage.OPENABLE_DOOR_BIT);
    }

    @Override
    public void onBodyHit() {
        if (screen.isDoorOpened()) {
            setCategoryFilter(MultiMage.DESTROYED_BIT);
            getCell(6).setTile(null);
            setDoorNull();
        }
    }
}
