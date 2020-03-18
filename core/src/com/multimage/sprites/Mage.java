package com.multimage.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.multimage.MultiMage;

// ordinary Mage class
public class Mage extends Sprite {

    public World world;
    public Body body;

    public Mage(World world) {
        this.world = world;
        defineMage();
    }

    public void defineMage() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(32 / MultiMage.PPM, 32 / MultiMage.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(5 / MultiMage.PPM);

        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
    }
}
