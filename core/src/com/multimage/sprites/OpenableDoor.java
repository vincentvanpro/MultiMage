package com.multimage.sprites;

import com.badlogic.gdx.maps.MapObject;
import com.multimage.MultiMage;
import com.multimage.screens.MultiPlayer;
import com.multimage.screens.PlayScreen;

public class OpenableDoor extends InteractiveTileObject {
    public OpenableDoor(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MultiMage.OPENABLE_DOOR_BIT);
    }

    public OpenableDoor(MultiPlayer screen, MapObject object) {
        super(screen, object);
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
