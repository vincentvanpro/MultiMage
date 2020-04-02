package com.multimage.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.multimage.MultiMage;
import com.multimage.screens.PlayScreen;


// ordinary Mage class
public class Mage extends Sprite {

    public enum State { JUMPING, FALLING, WALKING, STANDING }
    public State currentState;
    public State previousState;
    public World world;
    public Body body;
    private TextureRegion mageStand;
    private Animation<TextureRegion> mageWalk;
    private Animation<TextureRegion> mageJump;
    private float stateTimer;
    private boolean walkingRight;


    public Mage(PlayScreen screen) {
        super(screen.getAtlas().findRegion("standing"));
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        walkingRight = true;

        Array<TextureRegion> frames = new Array<>();
        for (int i = 1; i < 6; i++)
            frames.add(new TextureRegion(getTexture(), i * 78, 0, 78, 80));
        mageWalk = new Animation<>(0.15f, frames);
        frames.clear();

        for (int i = 1; i < 8; i++)
            frames.add(new TextureRegion(getTexture(), i * 78, 160, 78, 80));
        mageJump = new Animation<>(0.15f, frames);
        frames.clear();


        mageStand = new TextureRegion(getTexture(), 0, 80, 78, 80);

        defineMage();
        setBounds(0, 40, 110 / MultiMage.PPM, 98 / MultiMage.PPM);
        setRegion(mageStand);
    }

    public void update(float delta) {
        setRegion(getFrame(delta));
        if (walkingRight) {setPosition(body.getPosition().x - getWidth() / 3,
                body.getPosition().y - getHeight() / 3.10f);}
        else {
            setPosition(body.getPosition().x - getWidth() / 1.5f, body.getPosition().y - getHeight() / 3.10f);
        }
    }

    public TextureRegion getFrame(float delta) {
        currentState = getState();

        TextureRegion region;
        switch(currentState){
            case JUMPING:
                region = mageJump.getKeyFrame(stateTimer);
                break;
            case WALKING:
                region = mageWalk.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = mageStand;
                break;
        }
        if ((body.getLinearVelocity().x < 0 || !walkingRight) && !region.isFlipX()) {
            region.flip(true, false);
            walkingRight = false;
        } else if ((body.getLinearVelocity().x > 0 || walkingRight) && region.isFlipX()) {
            region.flip(true, false);
            walkingRight = true;
        }

        stateTimer = currentState == previousState ? stateTimer + delta : 0;
        previousState = currentState;
        return region;

    }

    public State getState() {
        if (body.getLinearVelocity().y > 0 || (body.getLinearVelocity().y < 0 && previousState == State.JUMPING)) {
            return State.JUMPING;
        } else if (body.getLinearVelocity().y < 0) {
            return State.FALLING;
        } else if (body.getLinearVelocity().x != 0) {
            return State.WALKING;
        } else {
            return State.STANDING;
        }
    }

    public void defineMage() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(200 / MultiMage.PPM, 50 / MultiMage.PPM); // 200x 50y - start (cage), 1750x 50y - stairs
        bodyDef.type = BodyDef.BodyType.DynamicBody; //        1000x 1400y - cages, 4050x 50y - boss, 1750x 1100y - long

        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();

        shape.setRadius(31f / MultiMage.PPM);
        fixtureDef.filter.categoryBits = MultiMage.MAGE_BIT;   // Define mage bit
        fixtureDef.filter.maskBits =                // Mage can collide with these objects
                    MultiMage.DEFAULT_BIT |
                    MultiMage.CHEST_BIT |
                    MultiMage.GROUND_BIT |
                    MultiMage.LEVERS_BIT |
                    MultiMage.OPENABLE_DOOR_BIT |
                    MultiMage.BONUS_BIT |
                    MultiMage.ITEM_BIT;


        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-30 / MultiMage.PPM, 0 / MultiMage.PPM),
                new Vector2(30 / MultiMage.PPM, 0 / MultiMage.PPM));
        fixtureDef.shape = head;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef).setUserData("body");
    }
}
