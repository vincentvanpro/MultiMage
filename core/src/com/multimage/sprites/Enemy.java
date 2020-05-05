package com.multimage.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.multimage.screens.MultiPlayer;
import com.multimage.screens.PlayScreen;

public abstract class Enemy extends Sprite {
    protected World world;
    protected PlayScreen screen;
    protected MultiPlayer screen2;
    public Body body;
    public Vector2 velocity;

    public Enemy(PlayScreen screen, float x, float y) {
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        defineEnemy();
        velocity = new Vector2(-1f, -1);
    }

    public Enemy(MultiPlayer screen, float x, float y) {
        this.world = screen.getWorld();
        this.screen2 = screen;
        setPosition(x, y);
        defineEnemy();
        velocity = new Vector2(-1f, -1);
    }

    protected abstract void defineEnemy();

    public abstract void hitOnHead();

    public abstract void update(float delta);

    public void reverseVelocity(boolean x, boolean y) {
        if (x) {
            velocity.x = -velocity.x;
        } if (y) {
            velocity.y = -velocity.y;
        }
    }

    public abstract void damage(Fireball fireball);
}
