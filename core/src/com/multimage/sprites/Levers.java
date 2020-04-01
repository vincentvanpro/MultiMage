package com.multimage.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.multimage.MultiMage;
import com.multimage.item.ItemDef;
import com.multimage.screens.PlayScreen;

public class Levers extends InteractiveTileObject {
    private int leverCount = map.getLayers().get(12).getObjects().getCount();
    private int count = 2;

    public Levers(PlayScreen screen, Rectangle bounds) {
        super(screen, bounds);
        fixture.setUserData(this);
        setCategoryFilter(MultiMage.LEVERS_BIT);
    }

    @Override
    public void onBodyHit() {
        System.out.println(count);
        Gdx.app.log("Levers", "collision");
        setCategoryFilter(MultiMage.DESTROYED_BIT);
        getCell(4).setTile(null);
        count--;
        System.out.println(count);
        if (count == 1) {
            screen.setDoorOpened();
        }

    }
}
