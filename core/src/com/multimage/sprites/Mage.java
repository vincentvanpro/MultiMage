package com.multimage.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.multimage.MultiMage;
import com.multimage.screens.PlayScreen;


// ordinary Mage class
public class Mage extends Sprite {

    public int id = -1;
    String name;
    public float PosX;
    public float PosY;
    public float speed = 300;

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


    public Mage(World world, PlayScreen screen) {
        super(screen.getAtlas().findRegion("standing"));
        this.world = world;
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
        setBounds(0, 80, 78 / MultiMage.PPM, 80 / MultiMage.PPM);
        setRegion(mageStand);
    }

    public Mage(int id, int x) {
        this.id = id;
        PosX = x;
        PosY = 50;
    }

    public Mage() {
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
        setBounds(0, 80, 78 / MultiMage.PPM, 80 / MultiMage.PPM);
        setRegion(mageStand);
    }

    public void update(float delta) {
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 14);
        setRegion(getFrame(delta));
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
        bodyDef.position.set(32 / MultiMage.PPM, 32 / MultiMage.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(5 / MultiMage.PPM);

        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPosX() {
        return PosX;
    }

    public void setPosX(float PostX) {
        this.PosX = PostX;
    }

    public float getPosY() {
        return PosY;
    }

    public void setPosY(float PostY) {
        this.PosY = PostY;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public void setDirection(boolean walkingRight) {
        this.walkingRight = walkingRight;
    }
}
