package com.multimage.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.multimage.MultiMage;
import com.multimage.item.Item;
import com.multimage.item.ItemDef;
import com.multimage.item.items.*;
import com.multimage.scenes.Hud;
import com.multimage.sprites.Enemy;
import com.multimage.sprites.Fireball;
import com.multimage.sprites.Mage;
import com.multimage.tools.WorldContactListener;
import com.multimage.tools.WorldCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class PlayScreen implements Screen {

    private MultiMage game;
    private TextureAtlas atlas;
    private TextureAtlas atlasEnemy;
    private List<Integer> levers;
    private boolean isDoorOpened;

    private Music music;

    // sprites
    private Mage player;
    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;

    // tiled map
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //box 2d
    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;
    private WorldCreator creator;

    // fireballs
    ArrayList<Fireball> fireballs;

    public TextureAtlas getAtlasEnemy() {
        return atlasEnemy;
    }

    public PlayScreen(MultiMage game) {
        this.game = game;
        atlas = new TextureAtlas("entity/mage/mage.pack");
        atlasEnemy = new TextureAtlas("entity/enemies/ghost.pack");

        //fireballs list
        fireballs = new ArrayList<>();

        // cam that follows you
        gameCam = new OrthographicCamera();
        // maintain virtual aspect ratio despite screen size
        gamePort = new FitViewport(MultiMage.V_WIDTH / MultiMage.PPM, MultiMage.V_HEIGHT / MultiMage.PPM, gameCam);
        // create hud
        hud = new Hud(game.batch);

        // load and setup map
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("levels/level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MultiMage.PPM);

        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, -10), true);
        box2DDebugRenderer = new Box2DDebugRenderer();
        creator = new WorldCreator(this);

        player = new Mage(this);

        music = MultiMage.manager.get("audio/music/main_menu_music.ogg", Music.class);
        music.stop();

        world.setContactListener(new WorldContactListener());

        items = new Array<>();
        itemsToSpawn = new LinkedBlockingQueue<>();
        levers = new ArrayList<>();

    }

    public TextureAtlas getAtlas(){
        return atlas;
    }

    public void spawnItem(ItemDef itemDef) {
        itemsToSpawn.add(itemDef);
    }

    public void handleSpawningItems() {
        if(!itemsToSpawn.isEmpty()) {
            ItemDef itemDef = itemsToSpawn.poll();
            if (itemDef.type == Ambrosia.class) {
                items.add(new Ambrosia(this, itemDef.position.x, itemDef.position.y));
            } else if (itemDef.type == Amulet.class) {
                items.add(new Amulet(this, itemDef.position.x, itemDef.position.y));
            } else if (itemDef.type == Book.class) {
                items.add(new Book(this, itemDef.position.x, itemDef.position.y));
            } else if (itemDef.type == Boots.class) {
                items.add(new Boots(this, itemDef.position.x, itemDef.position.y));
            } else if (itemDef.type == Crown.class) {
                items.add(new Crown(this, itemDef.position.x, itemDef.position.y));
            } else if (itemDef.type == Hat.class) {
                items.add(new Hat(this, itemDef.position.x, itemDef.position.y));
            } else if (itemDef.type == Ring.class) {
                items.add(new Ring(this, itemDef.position.x, itemDef.position.y));
            } else if (itemDef.type == Shield.class) {
                items.add(new Shield(this, itemDef.position.x, itemDef.position.y));
            } else if (itemDef.type == Staff.class) {
                items.add(new Staff(this, itemDef.position.x, itemDef.position.y));
            } else if (itemDef.type == Sword.class) {
                items.add(new Sword(this, itemDef.position.x, itemDef.position.y));
            }
        }
    }

    public void update(float delta) {
       handleInput(delta);
       // item creation //
       handleSpawningItems();

       world.step(1/60f, 6, 2);

       player.update(delta);
       hud.update(delta);

       for (Enemy enemy: creator.getGhosts()) {
           enemy.update(delta);
       }

       for (Item item : items) {
            item.update(delta);
       }

       gameCam.position.x = player.body.getPosition().x;
       gameCam.position.y = player.body.getPosition().y;

       // update cam
       gameCam.update();
       // render only what camera sees
       renderer.setView(gameCam);

    }

    private void handleInput(float delta) { //
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) && player.body.getLinearVelocity().y == 0.0f) {
            player.body.applyLinearImpulse(new Vector2(0, 5.75f), player.body.getWorldCenter(), true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.D) && player.body.getLinearVelocity().x <= 2.4f) {
            player.body.applyLinearImpulse(new Vector2(0.25f, 0), player.body.getWorldCenter(), true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.A) && player.body.getLinearVelocity().x >= -2.4f) {
            player.body.applyLinearImpulse(new Vector2(-0.25f, 0), player.body.getWorldCenter(), true);
        }
    }

    public World getWorld() {
        return world;
    }

    public TiledMap getMap() {
        return map;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        update(delta);


        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // render game map
        renderer.render();

        // render box2dDebugLines
        box2DDebugRenderer.render(world, gameCam.combined);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.draw(game.batch);

        for (Enemy enemy: creator.getGhosts()) {
            enemy.draw(game.batch);
        }

        // Fireball shoot
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            fireballs.add(new Fireball(player.body.getPosition().x, player.body.getPosition().y));
        }

        //Update fireball
        ArrayList<Fireball> fireballsToRemove = new ArrayList<>();
        for (Fireball fireball : fireballs) {
            fireball.update(delta);
            if (fireball.remove) {
                fireballsToRemove.add(fireball);
            }
        }
        fireballs.removeAll(fireballsToRemove);

        //render fireball
        for (Fireball fireball : fireballs) {
            fireball.render(game.batch);
        }

        // item creation
        for (Item item : items) {
            item.draw(game.batch);
        }
        game.batch.end();

        // PROTOTYPE HUD
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    public int leversActivated() {
        return levers.size();
    }

    public void leverPulled(int lever) {
        levers.add(lever);
    }

    public boolean isDoorOpened() {
        return isDoorOpened;
    }

    public void setDoorOpened() {
        isDoorOpened = true;
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        box2DDebugRenderer.dispose();
        hud.dispose();
    }
}
