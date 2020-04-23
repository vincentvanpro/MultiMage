package com.multimage.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;



public class Fireball {

    public static final int SPEED = 100;
    private static Texture texture;

    float x, y;

    public boolean remove = false;
    public Fireball (float x, float y) {
        this.x = x;
        this.y = y;

        if (texture == null) {
            texture = new Texture("entity/fireball/fireball.png");
        }
    }

    public void update (float deltaTime) {
        x += SPEED * deltaTime;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }
}
