package com.multimage.sprites;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.multimage.MultiMage;
import com.multimage.screens.PlayScreen;


public abstract class InteractiveTileObject {
    // A class for creating map object easier
    protected World world;
    protected TiledMap map;
    protected TiledMapTile tile;
    protected Rectangle bounds;
    protected Body body;
    protected PlayScreen screen;
    protected MapObject object;

    protected Fixture fixture;


    public InteractiveTileObject(PlayScreen screen, MapObject object) {
        this.screen = screen;
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.object = object;
        this.bounds = ((RectangleMapObject) object).getRectangle();

        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((bounds.getX() + bounds.getWidth() / 2) / MultiMage.PPM,
                (bounds.getY() + bounds.getHeight() / 2) / MultiMage.PPM);

        body = world.createBody(bodyDef);

        shape.setAsBox(bounds.getWidth() / 2 / MultiMage.PPM,
                bounds.getHeight() / 2 / MultiMage.PPM);
        fixtureDef.shape = shape;
        fixture = body.createFixture(fixtureDef);
    }

    public abstract void onBodyHit();

    public void setCategoryFilter(short filterBit) {
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

    public TiledMapTileLayer.Cell getCell(int n) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(n);
        return layer.getCell((int) (body.getPosition().x * MultiMage.PPM / 32),
                (int) (body.getPosition().y * MultiMage.PPM / 32));
    }

    public void setDoorNull() {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(6);
        layer.setVisible(false);
    }

}
