package com.multimage.sprites;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

public class Levers extends InteractiveTileObject {
    public Levers(World world, TiledMap map, Rectangle bounds) {
        super(world, map, bounds);
    }
}
