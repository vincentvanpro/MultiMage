package com.multimage.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.multimage.MultiMage;
import com.multimage.screens.MultiPlayer;
import com.multimage.screens.PlayScreen;

public class Fireball extends Sprite {

    private PlayScreen screen;
    private MultiPlayer multiPlayerScreen;
    private World world;
    private Array<TextureRegion> frames;
    private Animation<TextureRegion> fireAnimation;
    private float stateTime;
    private boolean destroyed;
    private boolean setToDestroy;
    private boolean fireRight;

    private float damage;

    private Body b2body;

    public Fireball(PlayScreen screen, float x, float y, boolean fireRight, float damage){
        this.fireRight = fireRight;
        this.screen = screen;
        this.damage = damage;
        this.world = screen.getWorld();
        frames = new Array<TextureRegion>();
        for(int i = 0; i < 4; i++){
            frames.add(new TextureRegion(screen.getAtlas().findRegion("fireball"), i * 8, 0, 8, 8));
        }
        fireAnimation = new Animation<TextureRegion>(0.2f, frames);
        setRegion(fireAnimation.getKeyFrame(0));
        setBounds(x, y, 35 / MultiMage.PPM, 35 / MultiMage.PPM);
        defineFireBall();
    }

    public Fireball(MultiPlayer screen, float x, float y, boolean fireRight, float damage){
        this.fireRight = fireRight;
        this.multiPlayerScreen = screen;
        this.world = screen.getWorld();
        this.damage = damage;
        frames = new Array<TextureRegion>();
        for(int i = 0; i < 4; i++){
            frames.add(new TextureRegion(screen.getAtlas().findRegion("fireball"), i * 8, 0, 8, 8));
        }
        fireAnimation = new Animation<TextureRegion>(0.2f, frames);
        setRegion(fireAnimation.getKeyFrame(0));
        setBounds(x, y, 35 / MultiMage.PPM, 35 / MultiMage.PPM);
        defineFireBall();
    }

    // Class for TEST
    public Fireball(float damage) {
        this.damage = damage;
    }

    public void defineFireBall(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(fireRight ? getX() + 12 / MultiMage.PPM : getX() - 12 / MultiMage.PPM, getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.gravityScale = 0f;
        if (!world.isLocked())
            b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(11 / MultiMage.PPM);
        fdef.filter.categoryBits = MultiMage.FIREBALL_BIT;
        fdef.filter.maskBits = MultiMage.GROUND_BIT |
                MultiMage.ENEMY_BIT |
                MultiMage.ENEMY_BODY_BIT |
                MultiMage.OBJECT_BIT;

        fdef.shape = shape;
        fdef.restitution = 1;
        fdef.friction = 0;
        b2body.createFixture(fdef).setUserData(this);
        b2body.setLinearVelocity(new Vector2(fireRight ? 4 : -4, 0f));
    }

    public void update(float dt){
        stateTime += dt;
        setRegion(fireAnimation.getKeyFrame(stateTime, true));
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        if((stateTime > 3 || setToDestroy) && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
        }
        if(b2body.getLinearVelocity().y > 2f)
            b2body.setLinearVelocity(b2body.getLinearVelocity().x, 2f);
        if((fireRight && b2body.getLinearVelocity().x < 0) || (!fireRight && b2body.getLinearVelocity().x > 0))
            setToDestroy();
    }

    public void setToDestroy(){
        setToDestroy = true;
    }

    public boolean isDestroyed(){
        return destroyed;
    }

    public float getDamage() {
        return damage;
    }


}
