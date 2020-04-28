package com.multimage.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.multimage.MultiMage;
import com.multimage.screens.MultiPlayer;
import com.multimage.screens.PlayScreen;

public class Levers extends InteractiveTileObject {
    private int leverCount = map.getLayers().get(12).getObjects().getCount();
    private MultiPlayer multiPlayerScreen;

    public Levers(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MultiMage.LEVERS_BIT);
    }

    public Levers(MultiPlayer screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MultiMage.LEVERS_BIT);
        this.multiPlayerScreen = screen;
    }

    @Override
    public void onBodyHit() {
        if (multiPlayerScreen == null) {
            setCategoryFilter(MultiMage.DESTROYED_BIT);
            getCell(4).setTile(null);
            screen.leverPulled(1);
            if (screen.leversActivated() == leverCount) {
                screen.setDoorOpened();
            }
        } else {
            setCategoryFilter(MultiMage.DESTROYED_BIT);
            getCell(4).setTile(null);
            multiPlayerScreen.leverPulled(1);
            if (multiPlayerScreen.leversActivated() == leverCount) {
                multiPlayerScreen.setDoorOpened();
            }
        }


    }
}
