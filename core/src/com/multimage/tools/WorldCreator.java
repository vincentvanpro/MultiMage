package com.multimage.tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.multimage.MultiMage;
import com.multimage.screens.MultiPlayer;
import com.multimage.screens.PlayScreen;
import com.multimage.sprites.*;

public class WorldCreator {

    private Array<Ghost> ghosts;
    private Demon demon;

    public Array<Ghost> getGhosts() {
        return ghosts;
    }
    public Demon getDemon() { return demon; }

    public WorldCreator(PlayScreen screen) {
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        Body body;

        // create chest
        for (MapObject object : map.getLayers().get(17).getObjects().getByType(RectangleMapObject.class)) {

            new Portal(screen, object);
        }

        // create ground fixtures/bodies
        for (MapObject object : map.getLayers().get(8).getObjects().getByType(RectangleMapObject.class)) {

            new Ground(screen, object);
        }

        // create platforms
        for (MapObject object : map.getLayers().get(13).getObjects().getByType(RectangleMapObject.class)) {

            new Platforms(screen, object);
        }

        // create chest
        for (MapObject object : map.getLayers().get(9).getObjects().getByType(RectangleMapObject.class)) {

            new Chest(screen, object);
        }

        // create bonus
        for (MapObject object : map.getLayers().get(10).getObjects().getByType(RectangleMapObject.class)) {

            new Bonus(screen, object);
        }

        // create openable door
        for (MapObject object : map.getLayers().get(11).getObjects().getByType(RectangleMapObject.class)) {

            new OpenableDoor(screen, object);
        }

        // create levers
        for (MapObject object : map.getLayers().get(12).getObjects().getByType(RectangleMapObject.class)) {

            new Levers(screen, object);
        }

        //create ghosts
        ghosts = new Array<Ghost>();
        for (MapObject object : map.getLayers().get(14).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

            ghosts.add(new Ghost(screen, rectangle.getX() / MultiMage.PPM, rectangle.getY() / MultiMage.PPM));
        }

        //create level1 boss
        for (MapObject object : map.getLayers().get(15).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

            demon = new Demon(screen, rectangle.getX() / MultiMage.PPM, rectangle.getY() / MultiMage.PPM);
        }

    }
    public WorldCreator(MultiPlayer screen) {
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        Body body;

        // create portal between levels
        for (MapObject object : map.getLayers().get(17).getObjects().getByType(RectangleMapObject.class)) {

            new Portal(screen, object);
        }

        // create ground fixtures/bodies
        for (MapObject object : map.getLayers().get(8).getObjects().getByType(RectangleMapObject.class)) {

            new Ground(screen, object);
        }

        // create platforms
        for (MapObject object : map.getLayers().get(13).getObjects().getByType(RectangleMapObject.class)) {

            new Platforms(screen, object);
        }

        // create chest
        for (MapObject object : map.getLayers().get(9).getObjects().getByType(RectangleMapObject.class)) {

            new Chest(screen, object);
        }

        // create bonus
        for (MapObject object : map.getLayers().get(10).getObjects().getByType(RectangleMapObject.class)) {

            new Bonus(screen, object);
        }

        // create openable door
        for (MapObject object : map.getLayers().get(11).getObjects().getByType(RectangleMapObject.class)) {

            new OpenableDoor(screen, object);
        }

        // create levers
        for (MapObject object : map.getLayers().get(12).getObjects().getByType(RectangleMapObject.class)) {

            new Levers(screen, object);
        }

        //create ghosts
        ghosts = new Array<Ghost>();
        for (MapObject object : map.getLayers().get(14).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

            ghosts.add(new Ghost(screen, rectangle.getX() / MultiMage.PPM, rectangle.getY() / MultiMage.PPM));
        }

        //create level1 boss
        for (MapObject object : map.getLayers().get(15).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

            demon = new Demon(screen, rectangle.getX() / MultiMage.PPM, rectangle.getY() / MultiMage.PPM);
        }
    }
}
