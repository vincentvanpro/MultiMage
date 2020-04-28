package com.multimage.sprites;

import com.badlogic.gdx.maps.MapObject;
import com.multimage.MultiMage;
import com.multimage.screens.MultiPlayer;
import com.multimage.screens.PlayScreen;

public class OpenableDoor extends InteractiveTileObject {
    private MultiPlayer multiPlayerScreen;

    public OpenableDoor(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MultiMage.OPENABLE_DOOR_BIT);
    }

    public OpenableDoor(MultiPlayer screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MultiMage.OPENABLE_DOOR_BIT);
        this.multiPlayerScreen =screen;
    }

    @Override
    public void onBodyHit() {
        if (multiPlayerScreen == null) {
            if (screen.isDoorOpened()) {
                setCategoryFilter(MultiMage.DESTROYED_BIT);
                getCell(6).setTile(null);
                setDoorNull();
            }
        } else {
            if (multiPlayerScreen.isDoorOpened()) {
                setCategoryFilter(MultiMage.DESTROYED_BIT);
                getCell(6).setTile(null);
                setDoorNull();
            }
        }
    }
}
