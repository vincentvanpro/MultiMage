package com.multimage.sprites;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.multimage.MultiMage;
import com.multimage.item.ItemDef;
import com.multimage.item.items.*;
import com.multimage.screens.MultiPlayer;
import com.multimage.screens.PlayScreen;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Portal extends InteractiveTileObject {
    public Portal(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MultiMage.PORTAL_BIT);
    }

    public Portal(MultiPlayer screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MultiMage.PORTAL_BIT);
    }

    @Override
    public void onBodyHit() {
        Gdx.app.log("Portal", "collision");
        screen.getPlayer().bodyDef.position.set(500 / MultiMage.PPM, 50 / MultiMage.PPM);
        ((Game) Gdx.app.getApplicationListener()).setScreen(new PlayScreen(screen.getGame(), screen.getPlayer(), 2));
    }
}
