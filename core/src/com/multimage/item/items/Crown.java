package com.multimage.item.items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.multimage.MultiMage;
import com.multimage.item.Item;
import com.multimage.screens.PlayScreen;
import com.multimage.sprites.Mage;

public class Crown extends Item {

    /*This item gives a 5% bonus to all the stats.
    Stacks +5%.
    */
    public Crown(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        TextureAtlas atlas = new TextureAtlas("ui/items.pack");
        setRegion(atlas.findRegion("crown"), 0, 0, 32, 32);
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

        fixtureDef.filter.categoryBits = MultiMage.ITEM_BIT;
        fixtureDef.filter.maskBits =
                MultiMage.MAGE_BIT |
                        MultiMage.OBJECT_BIT |
                        MultiMage.OPENABLE_DOOR_BIT |
                        MultiMage.BONUS_BIT |
                        MultiMage.LEVERS_BIT |
                        MultiMage.CHEST_BIT |
                        MultiMage.GROUND_BIT;

        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public void use(Mage mage) {
        mage.addItem("Crown");
        destroy();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        body.setLinearVelocity(velocity);
    }
}
