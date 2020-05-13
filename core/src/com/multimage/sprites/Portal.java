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
            if (screen.getLevelNumber() == 1) {
                if (screen.isBossDead()) {
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new PlayScreen(screen.getGame(), screen.getPlayer(), screen.getNextLevelNumber(), screen.getHud()));
                }
            } else if (screen.getLevelNumber() > 1 || screen.isDoorOpened()) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new PlayScreen(screen.getGame(), screen.getPlayer(), screen.getNextLevelNumber(), screen.getHud()));
            }
        } else if (multiPlayerScreen != null) {
            if (multiPlayerScreen.getLevelNumber() == 1) {
                if (multiPlayerScreen.isBossDead()) {
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new PlayScreen(multiPlayerScreen.getGame(), multiPlayerScreen.getPlayer(), multiPlayerScreen.getNextLevelNumber(), multiPlayerScreen.getHud()));
                }
            } else if (multiPlayerScreen.getLevelNumber() > 1 || multiPlayerScreen.isDoorOpened()) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new PlayScreen(multiPlayerScreen.getGame(), multiPlayerScreen.getPlayer(), multiPlayerScreen.getNextLevelNumber(), multiPlayerScreen.getHud()));
            }
        }
    }
}
