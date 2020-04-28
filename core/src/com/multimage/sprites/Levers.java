package com.multimage.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.multimage.MultiMage;
import com.multimage.screens.MultiPlayer;
import com.multimage.screens.PlayScreen;

public class Levers extends InteractiveTileObject {
    private int leverCount = map.getLayers().get(12).getObjects().getCount();

    public Levers(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MultiMage.LEVERS_BIT);
    }

    public Levers(MultiPlayer screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MultiMage.LEVERS_BIT);
    }

    @Override
    public void onBodyHit() {
        System.out.println(leverCount);
        Gdx.app.log("Levers", "collision");
        setCategoryFilter(MultiMage.DESTROYED_BIT);
        getCell(4).setTile(null);
        screen.leverPulled(1);
        System.out.println(screen.leversActivated());
        if (screen.leversActivated() == leverCount) {
            screen.setDoorOpened();
        }

    }
}
