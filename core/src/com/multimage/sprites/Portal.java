package com.multimage.sprites;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.multimage.MultiMage;
import com.multimage.screens.MultiPlayer;
import com.multimage.screens.PlayScreen;


public class Portal extends InteractiveTileObject {
    private MultiPlayer multiPlayerScreen;

    public Portal(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MultiMage.PORTAL_BIT);
    }

    public Portal(MultiPlayer screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MultiMage.PORTAL_BIT);
        multiPlayerScreen = screen;
    }

    @Override
    public void onBodyHit() {
        if (multiPlayerScreen == null) {
            if (screen.isDoorOpened()) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new PlayScreen(screen.getGame(), screen.getPlayer(), screen.getNextLevelNumber(), screen.getHud()));
            }
        } else if (multiPlayerScreen != null) {
            if (multiPlayerScreen.isDoorOpened()) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MultiPlayer(multiPlayerScreen.getGame(), multiPlayerScreen.getPlayer(), screen.getNextLevelNumber(), multiPlayerScreen.getHud()));
            }

        }

    }
}
