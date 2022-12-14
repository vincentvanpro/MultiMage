package com.multimage.sprites;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
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
import com.multimage.screens.GameOverScreen;
import com.multimage.screens.MultiPlayer;
import com.multimage.screens.PlayScreen;
import com.multimage.tools.Character;

import java.util.HashMap;
import java.util.Random;


// ordinary Mage class
public class Mage extends Sprite implements Character {

    public enum State { JUMPING, FALLING, WALKING, STANDING, ATTACK }

    public int id = -1;
    public String name;
    public float PosX;
    public float PosY;

    public float getSpeed() {
        return speed;
    }

    public float speed = 0.25f;

    public static State currentState;
    public State previousState;
    public World world;
    public Body body;

    private Array<Fireball> fireballs;

    private TextureRegion mageStand;
    private Animation<TextureRegion> mageWalk;
    private Animation<TextureRegion> mageJump;
    private Animation<TextureRegion> mageAttack;
    private float stateTimer;
    private boolean walkingRight;
    private boolean isAttacking = false;

    private HashMap<String, Integer> items;
    private float health;
    private float armour;
    private int level;
    private float xp;
    private float xpBoostPercent;

    private float damage;
    private float jumpPlus = 5.75f;

    private float chanceToInstantKill;

    private Texture healthBackground;
    private Texture healthForeground;
    private Texture healthBorder;
    private float healthPercent;

    private PlayScreen screen;
    private MultiPlayer multiPlayerScreen;

    private boolean setToDestroy;
    private boolean destroyed;

    public Mage(PlayScreen screen) {
        super(screen.getAtlas().findRegion("walk"));
        this.world = screen.getWorld();
        this.screen = screen;
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        walkingRight = true;

        setToDestroy = false;
        destroyed = false;

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

        fireballs = new Array<Fireball>();

        items = new HashMap<>();
        health = 100f;
        healthPercent = 1f;
        armour = 5f;
        damage = 20f;
        chanceToInstantKill = 0;
    }

    /*Class for tests because they wouldn't run -> to run test go to
      Settings > Build, Execution, Deployment > Build Tools > Gradle
      and change Run tests using && Build and Run: from Gradle (Default) to IntelliJ IDEA.*/
    public Mage() {
        items = new HashMap<>();
        health = 100f;
        healthPercent = 1f;
        armour = 5f;
        damage = 20f;
        chanceToInstantKill = 0;
    };

    public Mage(int id, float x, float y) {
        setToDestroy = false;
        destroyed = false;
        this.id = id;
        armour = 5f;
        PosX = x;
        PosY = y;
    }

    public Mage(MultiPlayer screen) {
        super(screen.getAtlas().findRegion("walk"));
        this.world = screen.getWorld();
        this.multiPlayerScreen = screen;
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        walkingRight = true;

        setToDestroy = false;
        destroyed = false;

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

        health = 100f;
        healthPercent = 1f;
        armour = 5f;
        damage = 20f;
        chanceToInstantKill = 0;

        items = new HashMap<>();
        fireballs = new Array<Fireball>();
    }

    public void update(float delta) {
        if (setToDestroy && !destroyed) {
            destroyed = true;
            world.destroyBody(body);
            if (multiPlayerScreen == null) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new GameOverScreen(screen.getGame(), screen.getHud().getTime()));
            } else {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new GameOverScreen(multiPlayerScreen.getGame(), multiPlayerScreen.getHud().getTime()));
            }

        } else if (!destroyed) {
            setRegion(getFrame(delta));
            if (walkingRight) {
                setPosition(body.getPosition().x - getWidth() / 3, body.getPosition().y - getHeight() / 3.10f);}
            else {
                setPosition(body.getPosition().x - getWidth() / 1.5f, body.getPosition().y - getHeight() / 3.10f);
            }
            PosX = body.getPosition().x;
            PosY = body.getPosition().y;

            for (Fireball  ball : fireballs) {
                ball.update(delta);
                if(ball.isDestroyed())
                    fireballs.removeValue(ball, true);
            }
        }
    }

    public void draw(Batch batch) {
        if (!destroyed) {
            super.draw(batch);
            for(Fireball ball : fireballs)
                ball.draw(batch);
            if (multiPlayerScreen == null) {
                batch.draw(healthBorder, PlayScreen.getCamPositionX() - 4.05f, PlayScreen.getCamPositionY() - 2.15f, (310f / MultiMage.PPM) * 1f, 24 / MultiMage.PPM);
                batch.draw(healthBackground, PlayScreen.getCamPositionX() - 4f, PlayScreen.getCamPositionY() - 2.1f, (300f / MultiMage.PPM) * 1f, 15 / MultiMage.PPM);
                batch.draw(healthForeground, PlayScreen.getCamPositionX() - 4f, PlayScreen.getCamPositionY() - 2.1f, (300f / MultiMage.PPM) * healthPercent, 15 / MultiMage.PPM);// healthBar
            } else {
                batch.draw(healthBorder, MultiPlayer.getCamPositionX() - 4.05f, MultiPlayer.getCamPositionY() - 2.15f, (310f / MultiMage.PPM) * 1f, 24 / MultiMage.PPM);
                batch.draw(healthBackground, MultiPlayer.getCamPositionX() - 4f, MultiPlayer.getCamPositionY() - 2.1f, (300f / MultiMage.PPM) * 1f, 15 / MultiMage.PPM);
                batch.draw(healthForeground, MultiPlayer.getCamPositionX() - 4f, MultiPlayer.getCamPositionY() - 2.1f, (300f / MultiMage.PPM) * healthPercent, 15 / MultiMage.PPM);// healthBar
            }

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

    BodyDef bodyDef = new BodyDef();

    public void defineMage() {
        healthBackground = new Texture("entity/mage/healthBackground.png");
        healthForeground = new Texture("entity/mage/healthForeground.png");
        healthBorder = new Texture("entity/mage/healthBorder.png");

        PosX = 500;
        PosY = 50;
        bodyDef.position.set(PosX / MultiMage.PPM, PosY / MultiMage.PPM); // 500x 50y - start (cage), 1750x 50y - stairs
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
                    MultiMage.PLATFORM_BIT |
                    MultiMage.PORTAL_BIT;


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
                MultiMage.ITEM_BIT |
                MultiMage.PORTAL_BIT;
        fixtureDef.shape = head;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef).setUserData("body");
    }

    // Item Realisation

    @Override
    public float getArmour() {
        return armour;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public float getHealth() {
        return health;
    }

    public float getXpBoostPercent() {
        return xpBoostPercent;
    }

    public float getDamage() {
        return damage;
    }

    public float getChanceToInstantKill() {
        return chanceToInstantKill;
    }

    @Override
    public void levelUp() { }

    @Override
    public void getBonusesFromItems(String item) {
        if (item.equalsIgnoreCase("Ambrosia")) {
            health = (float) (health + (health * (items.get(item) * 0.02)));
        } else if (item.equalsIgnoreCase("Amulet")) {
            health =  (1 - healthPercent + healthPercent) * 100f;
            updateHeathPercent();
        } else if (item.equalsIgnoreCase("Book")) {
            xpBoostPercent += 0.15f;
        } else if (item.equalsIgnoreCase("Boots")) {
            if (items.get(item) > 1) {
                speed += speed * (0.05 * (items.get(item) - 1));
            } else {
                speed += speed * 0.1;
            }
        } else if (item.equalsIgnoreCase("Crown")) {
            health += health * (items.get(item) * 0.05);
            armour += armour * (items.get(item) * 0.05);
        } else if (item.equalsIgnoreCase("Hat")) {
            damage += damage * (items.get(item) * 0.1);
        } else if (item.equalsIgnoreCase("Ring")) {
            jumpPlus += 0.25f;
        } else if (item.equalsIgnoreCase("Shield")) {
            if (items.get(item) > 1) {
                armour += 5 * (items.get(item) - 1);
            } else {
                armour += 10;
            }
        } else if (item.equalsIgnoreCase("Sword")) {
            if (items.get(item) < 7) {
                chanceToInstantKill += 0.5f;
            }
        }
    }

    public void hit() {
        health -= 20f - (armour / 10);
        if (health <= 0) {
            setToDestroy = true;
        }
        updateHeathPercent();
    }

    public void updateHeathPercent() {
        if (health != 0) {
            healthPercent = health / 100f ;
        } else {
            health = 0;
        }

    }


    @Override
    public void getPassiveSkillEffect() { }

    @Override
    public void addItem(String item) {
        if (items.containsKey(item)) {
            items.put(item, items.get(item) + 1);
        } else {
            items.put(item, 1);
        }
        getBonusesFromItems(item);
    }

    // Return bonus velocity to add for jump
    public float jump() {
        return jumpPlus;
    }

    public HashMap<String, Integer> getItems() {
        return items;
    }

    public void setItemsAndGetBonus(HashMap<String, Integer> items) {
        this.items = items;
        for (String item : items.keySet()) {
            getBonusesFromItems(item);
        }
    }

    public void fire() {
        Random random = new Random();
        int randomInt = random.nextInt((int) (100 - chanceToInstantKill));

        float damageNow = damage;
        if (randomInt == 1) {
            damageNow = 1000000f;
        }
        if (multiPlayerScreen == null) {
            if (walkingRight) {
                fireballs.add(new Fireball(screen, body.getPosition().x + 24 / MultiMage.PPM, body.getPosition().y + 12 / MultiMage.PPM, walkingRight ? true : false, damageNow));
            } else {
                fireballs.add(new Fireball(screen, body.getPosition().x - 24 / MultiMage.PPM, body.getPosition().y + 12 / MultiMage.PPM, walkingRight ? true : false, damageNow));
            }
        } else {
            if (walkingRight) {
                fireballs.add(new Fireball(multiPlayerScreen, body.getPosition().x + 24 / MultiMage.PPM, body.getPosition().y + 12 / MultiMage.PPM, walkingRight ? true : false, damageNow));
            } else {
                fireballs.add(new Fireball(multiPlayerScreen, body.getPosition().x - 24 / MultiMage.PPM, body.getPosition().y + 12 / MultiMage.PPM, walkingRight ? true : false, damageNow));
            }
        }
    }

    public boolean isWalkingRight() {
        return walkingRight;
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    public void setAttacking(boolean isAttacking) {
        this.isAttacking = isAttacking;
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

    public boolean isDestroyed() {
        return destroyed;
    }
}
