package com.multimage.item.items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.multimage.MultiMage;
import com.multimage.item.Item;
import com.multimage.screens.PlayScreen;

public class Ambrosia extends Item {

    /*The amount of this item in inventory will increase the health of it's owner by 2% of max health.
    Stacks +2%.
     */
    public Ambrosia(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        TextureAtlas atlas = new TextureAtlas("ui/items.pack");
        setRegion(atlas.findRegion("ambrosia"), 0, 0, 32, 32);
        velocity = new Vector2(0, 0);
    }

    @Override
    public void defineItem() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MultiMage.PPM);

        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public void use() {
        destroy();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        body.setLinearVelocity(velocity);
    }
}
