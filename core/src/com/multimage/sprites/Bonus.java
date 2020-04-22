package com.multimage.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.multimage.MultiMage;
import com.multimage.item.ItemDef;
import com.multimage.item.items.*;
import com.multimage.screens.MultiPlayer;
import com.multimage.screens.PlayScreen;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Bonus extends InteractiveTileObject {
    public Bonus(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MultiMage.BONUS_BIT);
    }

    public Bonus(MultiPlayer screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MultiMage.BONUS_BIT);
    }

    @Override
    public void onBodyHit() {
        Random random = new Random();
        List<?> items1 = Arrays.asList(Ambrosia.class, Amulet.class, Book.class, Boots.class,
               Crown.class, Hat.class, Ring.class, Shield.class, Staff.class, Sword.class);
        List<?> items = Arrays.asList(Ring.class,Ring.class,Ring.class,Ring.class,Ring.class,Ring.class,Ring.class,Ring.class,Ring.class,Ring.class);
        setCategoryFilter(MultiMage.DESTROYED_BIT);
        getCell(2).setTile(null);
        screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y),
                (Class<?>) items.get(random.nextInt(10))));
    }
}
