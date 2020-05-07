package com.multimage.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.multimage.MultiMage;
import com.multimage.screens.MultiPlayer;
import com.multimage.screens.PlayScreen;
import com.multimage.tools.SteeringBehaviourAI;

public class Ghost extends Enemy {

    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> deathAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;
    private boolean walkingRight;

    private float healthPercent;
    private float health;
    private Texture healthBar = new Texture("entity/healthBar/enemyhealthfg.png");

    public SteeringBehaviourAI entity;

    public Ghost(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        stateTime = 0;
        frames = new Array<TextureRegion>();
        walkingRight = false;

        setToDestroy = false;
        destroyed = false;

        for (int i = 1; i < 7; i++) {
            frames.add(new TextureRegion(screen.getAtlasGhost().findRegion("ghost-idle"), i * 64, 0, 78, 80));
        }
        walkAnimation = new Animation<TextureRegion>(0.2f, frames);
        frames.clear();

        for (int i = 1; i < 7; i++) {
            frames.add(new TextureRegion(screen.getAtlasGhost().findRegion("ghost-vanish"), i * 64, 0, 78, 80));
        }
        deathAnimation = new Animation<TextureRegion>(0.15f, frames);
        frames.clear();

        stateTime = 0;
        setBounds(getX(), getY(), 110 / MultiMage.PPM, 98 / MultiMage.PPM);
        health = 100f;
        healthPercent = 1f; // 1f - full, 0f - dead
    }

    public Ghost(MultiPlayer screen, float x, float y) {
        super(screen, x, y);
        stateTime = 0;
        frames = new Array<TextureRegion>();
        walkingRight = false;

        setToDestroy = false;
        destroyed = false;

        for (int i = 1; i < 7; i++) {
            frames.add(new TextureRegion(screen.getAtlasGhost().findRegion("ghost-idle"), i * 64, 0, 78, 80));
        }
        walkAnimation = new Animation<TextureRegion>(0.2f, frames);
        frames.clear();

        for (int i = 1; i < 7; i++) {
            frames.add(new TextureRegion(screen.getAtlasGhost().findRegion("ghost-vanish"), i * 64, 0, 78, 80));
        }
        deathAnimation = new Animation<TextureRegion>(0.15f, frames);
        frames.clear();

        stateTime = 0;
        setBounds(getX(), getY(), 110 / MultiMage.PPM, 98 / MultiMage.PPM);
        health = 100f;
        healthPercent = 1f; // 1f - full, 0f - dead
    }

    public void update(float delta) {
        stateTime += delta;
        if (setToDestroy && !destroyed) {
            setRegion(deathAnimation.getKeyFrame(stateTime, true));
            stateTime = 0;
            destroyed = true;
            world.destroyBody(body);
        } else if (!destroyed) {
            // body.setLinearVelocity(velocity);
            setRegion(getFrame(delta));
            if (walkingRight) {
                setPosition(body.getPosition().x - getWidth() / 2.5f, body.getPosition().y - 0.35f);
            } else {
                setPosition(body.getPosition().x - getWidth() / 1.7f, body.getPosition().y - 0.35f);
            }
        }
    }

    public TextureRegion getFrame(float delta) {
        TextureRegion region = walkAnimation.getKeyFrame(stateTime, true);
        if ((body.getLinearVelocity().x > 0 || !walkingRight) && !region.isFlipX()) {
            region.flip(true, false);
            walkingRight = false;
        } else if ((body.getLinearVelocity().x < 0 || walkingRight) && region.isFlipX()) {
            region.flip(true, false);
            walkingRight = true;
        }
        return region;
    }

    @Override
    protected void defineEnemy() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY()); // 200x 50y - start (cage), 1750x 50y - stairs
        bodyDef.type = BodyDef.BodyType.DynamicBody; //        1000x 1400y - cages, 4050x 50y - boss, 1750x 1100y - long
        bodyDef.gravityScale = 0f;
        body = world.createBody(bodyDef);

        entity = new SteeringBehaviourAI(body, 10);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();

        shape.setRadius(18f / MultiMage.PPM);
        fixtureDef.filter.categoryBits = MultiMage.ENEMY_BIT;
        fixtureDef.filter.maskBits =
                MultiMage.OBJECT_BIT |
                MultiMage.GROUND_BIT |
                MultiMage.OPENABLE_DOOR_BIT |
                MultiMage.FIREBALL_BIT |
                MultiMage.MAGE_BIT |
                MultiMage.ITEM_BIT ;


        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);

        // HitBox
        PolygonShape bd = new PolygonShape();
        Vector2[] vector2s = new Vector2[4];
        vector2s[0] = new Vector2(-9, 36).scl(1 / MultiMage.PPM);
        vector2s[1] = new Vector2(9, 36).scl(1 / MultiMage.PPM);
        vector2s[2] = new Vector2(-18, 0).scl(1 / MultiMage.PPM);
        vector2s[3] = new Vector2(18, 0).scl(1 / MultiMage.PPM);
        bd.set(vector2s);

        fixtureDef.shape = bd;
        fixtureDef.restitution = 0.5f;
        fixtureDef.filter.categoryBits = MultiMage.ENEMY_BODY_BIT;
        body.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public void hitOnHead() {
        // setToDestroy = true;
    }

    public void draw(Batch batch) {
        if (!destroyed || stateTime < 5) {
            super.draw(batch);
            batch.draw(healthBar, body.getPosition().x - 0.15f, body.getPosition().y + 0.4f, (30f / MultiMage.PPM) * healthPercent, 3 / MultiMage.PPM); // healthBar
        }
    }

    public void updateHeathPercent() {
        if (health != 0) {
            healthPercent = health / 100f ;
        } else {
            health = 0;
        }
    }

    @Override
    public void damage(Fireball fireball) {
        health -= fireball.getDamage();
        if (health <= 0) {
            setToDestroy = true;
        }
        updateHeathPercent();
    }

}
