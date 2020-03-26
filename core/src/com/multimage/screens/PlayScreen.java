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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.multimage.MultiMage;
import com.multimage.scenes.Hud;
import com.multimage.sprites.Mage;
import com.multimage.tools.WorldCreator;

public class PlayScreen implements Screen {
    private MultiMage game;
    private TextureAtlas atlas;

    private Music music;

    // sprites
    private Mage player;
    // for further item creation //
    // private Array<Item> items;
    // private PriorityQueue<ItemDef> itemsToSpawn;

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

    public PlayScreen(MultiMage game) {
        this.game = game;
        atlas = new TextureAtlas("MageTextures.pack");

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

        new WorldCreator(world, map);

        player = new Mage(world, this);

        music = MultiMage.manager.get("audio/music/main_menu_music.ogg", Music.class);
        music.stop();

        // for further item creation //
        // items = new Array<Item>();
        // itemsToSpawn = new PriorityQueue<ItemDef>();
    }

    public TextureAtlas getAtlas(){
        return atlas;
    }

    // for further item creation //
    // public void spawnItem(ItemDef itemDef) {
    //     itemsToSpawn.add(itemDef);
    // }

    // for further item creation //
    // public void handleSpawningItems() {
    //     if(!itemsToSpawn.isEmpty()) {
    //         ItemDef itemDef = itemsToSpawn.poll();
    //         if (itemDef.type == Ambrosia.class) {
    //             items.add(new Ambrosia(this, itemDef.position.x, itemDef.position.y));
    //         }
    //     }
    // }

    public void update(float delta) {
       handleInput(delta);
        // for further item creation //
       // handleSpawningItems(); //

       world.step(1/60f, 6, 2);

       player.update(delta);

       gameCam.position.x = player.body.getPosition().x;
       gameCam.position.y = player.body.getPosition().y;

       // update cam
       gameCam.update();
       // render only what camera sees
       renderer.setView(gameCam);

       // for further item creation
       // for (Item item : items) {
       //     item.update(delta);
       // }
    }

    private void handleInput(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && player.getState() != Mage.State.JUMPING) {
            player.body.applyLinearImpulse(new Vector2(0, 4f), player.body.getWorldCenter(), true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.body.getLinearVelocity().x <= 2) {
            player.body.applyLinearImpulse(new Vector2(0.1f, 0), player.body.getWorldCenter(), true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.body.getLinearVelocity().x >= -2) {
            player.body.applyLinearImpulse(new Vector2(-0.1f, 0), player.body.getWorldCenter(), true);
        }
    }

    public World getWorld() {
        return world;
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
        game.batch.end();

        // PROTOTYPE HUD
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        // for further item creation
        // for (Item item : items) {
        //     item.draw(game.batch);
        // }

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
