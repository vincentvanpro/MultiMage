package com.multimage.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.multimage.MultiMage;
import com.multimage.screens.PlayScreen;
import com.multimage.tools.Character;

import java.util.HashMap;


// ordinary Mage class
public class Mage extends Sprite implements Character {

    @Override
    public int getArmour() {
        return 0;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public float getHealth() {
        return 0;
    }

    @Override
    public void levelUp() { }

    @Override
    public void getBonusesFromItems() { }

    @Override
    public void getPassiveSkillEffect() { }

    @Override
    public void addItem(String item) {
        if (items.containsKey(item)) {
            items.put(item, items.get(item) + 1);
        } else {
            items.put(item, 1);
        }
        System.out.println(items);
    }

    public enum State { JUMPING, FALLING, WALKING, STANDING, ATTACK }

    public int id = -1;
    String name;
    public float PosX;
    public float PosY;
    public float speed = 300;

    public State currentState;
    public State previousState;
    public World world;
    public Body body;

    private TextureRegion mageStand;
    private Animation<TextureRegion> mageWalk;
    private Animation<TextureRegion> mageJump;
    private Animation<TextureRegion> mageAttack;
    private float stateTimer;
    private boolean walkingRight;
    private HashMap<String, Integer> items;
    private float health;
    private float armour;
    private float xp;
    private Texture healthBackground = new Texture("entity/mage/healthBackground.png");
    private Texture healthForeground= new Texture("entity/mage/healthForeground.png");
    private Texture healthBorder = new Texture("entity/mage/healthBorder.png");
    private float healthPercent;

    public Mage(PlayScreen screen) {
        super(screen.getAtlas().findRegion("walk"));
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

        for (int i = 1; i < 9; i++)
            frames.add(new TextureRegion(getTexture(), i * 78, 80, 78, 80));
        mageJump = new Animation<>(0.15f, frames);
        frames.clear();

        for (int i = 1; i < 9; i++)
            frames.add(new TextureRegion(getTexture(), i * 90, 160, 78, 80));
        mageAttack = new Animation<>(0.15f, frames);
        frames.clear();

        mageStand = new TextureRegion(getTexture(), 0, 80, 78, 80);

        defineMage();
        setBounds(0, 40, 110 / MultiMage.PPM, 98 / MultiMage.PPM);
        setRegion(mageStand);

        healthPercent = 1f;

        items = new HashMap<>();
    }

    public void draw(Batch batch) {
        super.draw(batch);
        batch.draw(healthBorder, body.getPosition().x - 4.05f, body.getPosition().y - 2.05f, (310f / MultiMage.PPM) * 1f, 24 / MultiMage.PPM);
        batch.draw(healthBackground, body.getPosition().x - 4f, body.getPosition().y - 2f, (300f / MultiMage.PPM) * 1f, 15 / MultiMage.PPM);
        batch.draw(healthForeground, body.getPosition().x - 4f, body.getPosition().y - 2f, (300f / MultiMage.PPM) * healthPercent, 15 / MultiMage.PPM);// healthBar
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

        for (int i = 1; i < 9; i++)
            frames.add(new TextureRegion(getTexture(), i * 78, 80, 78, 80));
        mageJump = new Animation<>(0.15f, frames);
        frames.clear();

        for (int i = 1; i < 9; i++)
            frames.add(new TextureRegion(getTexture(), i * 90, 160, 78, 80));
        mageAttack = new Animation<>(0.15f, frames);
        frames.clear();

        mageStand = new TextureRegion(getTexture(), 0, 80, 78, 80);

        defineMage();
        setBounds(0, 80, 78 / MultiMage.PPM, 80 / MultiMage.PPM);
        setRegion(mageStand);
    }

    public void update(float delta) {
        setRegion(getFrame(delta));
        if (walkingRight) {
            setPosition(body.getPosition().x - getWidth() / 3, body.getPosition().y - getHeight() / 3.10f);}
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
            case ATTACK:
                region = mageAttack.getKeyFrame(stateTimer);
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
        } else if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            return State.ATTACK;
        } else {
            return State.STANDING;
        }
    }

    public void hit() {
        if (healthPercent >= 0.2f) {
            healthPercent = healthPercent - 0.2f;
        }
    }

    public void defineMage() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(700 / MultiMage.PPM, 1500 / MultiMage.PPM); // 200x 50y - start (cage), 1750x 50y - stairs
        bodyDef.type = BodyDef.BodyType.DynamicBody; //        1000x 1400y - cages, 4050x 50y - boss, 1750x 1100y - long

        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();

        shape.setRadius(31f / MultiMage.PPM);
        fixtureDef.filter.categoryBits = MultiMage.MAGE_BIT;   // Define mage bit
        fixtureDef.filter.maskBits =                // Mage can collide with these objects
                    MultiMage.OBJECT_BIT |
                    MultiMage.CHEST_BIT |
                    MultiMage.LEVERS_BIT |
                    MultiMage.OPENABLE_DOOR_BIT |
                    MultiMage.BONUS_BIT |
                    MultiMage.ITEM_BIT |
                    MultiMage.ENEMY_BIT |
                    MultiMage.GROUND_BIT |
                    MultiMage.ENEMY_BODY_BIT |
                    MultiMage.PLATFORM_BIT;


        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-30 / MultiMage.PPM, 0 / MultiMage.PPM),
                new Vector2(30 / MultiMage.PPM, 0 / MultiMage.PPM));
        fixtureDef.filter.categoryBits = MultiMage.MAGE_HAND_BIT;
        fixtureDef.filter.maskBits =
                MultiMage.CHEST_BIT |
                MultiMage.LEVERS_BIT |
                MultiMage.OPENABLE_DOOR_BIT |
                MultiMage.BONUS_BIT |
                MultiMage.ITEM_BIT;
        fixtureDef.shape = head;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef).setUserData("body");
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
