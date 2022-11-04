package com.multimage.sprites;

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

public class Chest extends InteractiveTileObject {
    private MultiPlayer multiPlayerScreen;

    public Chest(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MultiMage.CHEST_BIT);
    }

    public Chest(MultiPlayer screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MultiMage.CHEST_BIT);
        this.multiPlayerScreen = screen;
    }

    @Override
    public void onBodyHit() {
        if (multiPlayerScreen == null) {
            Random random = new Random();
            List<?> items = Arrays.asList(Ambrosia.class, Amulet.class, Book.class, Boots.class,
                    Crown.class, Hat.class, Ring.class, Shield.class, Staff.class, Sword.class);
            setCategoryFilter(MultiMage.DESTROYED_BIT);
            getCell(3).setTile(null);
            screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y),
                    (Class<?>) items.get(random.nextInt(10))));
        } else {
            Random random = new Random();
            List<?> items = Arrays.asList(Ambrosia.class, Amulet.class, Book.class, Boots.class,
                    Crown.class, Hat.class, Ring.class, Shield.class, Staff.class, Sword.class);
            setCategoryFilter(MultiMage.DESTROYED_BIT);
            getCell(3).setTile(null);
            multiPlayerScreen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y),
                    (Class<?>) items.get(random.nextInt(10))));
        }

    }
}
