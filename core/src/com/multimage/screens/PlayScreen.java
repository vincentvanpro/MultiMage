package com.multimage.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
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
import com.multimage.sprites.Ghost;
import com.multimage.sprites.Mage;
import com.multimage.tools.SteeringBehaviourAI;
import com.multimage.tools.WorldContactListener;
import com.multimage.tools.WorldCreator;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class PlayScreen implements Screen {

    private MultiMage game;
    private TextureAtlas atlas;
    private TextureAtlas atlasGhost;
    private TextureAtlas atlasDemon;
    private List<Integer> levers;
    public static boolean isDoorOpened;

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

    private SteeringBehaviourAI target;

    public TextureAtlas getAtlasGhost() {
        return atlasGhost;
    }

    public TextureAtlas getAtlasDemon() {
        return atlasDemon;
    }

    private int xMaxCord;
    private int yMaxCord;
    private float xMaxCamCord;
    private float yMaxCamCord;

    public PlayScreen(MultiMage game) {
        String levelPath = "levels/level1.tmx";  //change 1 to 2 to change level

        MultiMage.music.stop();
        if (levelPath.equals("levels/level1.tmx")) {
            MultiMage.music = MultiMage.manager.get("audio/music/first_level_music.ogg", Music.class);
            xMaxCord = 4690;
            yMaxCord = 1675;
            xMaxCamCord = 38.239f;
            yMaxCamCord = 11.923f;
        }
        else if (levelPath.equals("levels/level2.tmx")) {
            MultiMage.music = MultiMage.manager.get("audio/music/second_level_music.ogg", Music.class);
            xMaxCord = 3086;
            yMaxCord = 1035;
            xMaxCamCord = 22.24f;
            yMaxCamCord = 5.52f;
        }
        else if (levelPath.equals("levels/level3.tmx")) {
            MultiMage.music = MultiMage.manager.get("audio/music/third_level_music.ogg", Music.class);
            xMaxCord = 3086;
            yMaxCord = 1035;
            xMaxCamCord = 22.24f;
            yMaxCamCord = 5.52f;
        }
        MultiMage.music.setVolume(Gdx.app.getPreferences("com.multimage.settings")
                .getFloat("volume", 0.5f));
        MultiMage.music.setLooping(true);
        if (Gdx.app.getPreferences("com.multimage.settings")
                .getBoolean("music.enabled", true)) {
            MultiMage.music.play();
        }

        this.game = game;
        atlas = new TextureAtlas("entity/mage/MageTextures.pack");
        atlasGhost = new TextureAtlas("entity/enemies/ghost.pack");
        atlasDemon = new TextureAtlas("entity/enemies/demon.pack");

        // cam that follows you
        gameCam = new OrthographicCamera();
        // maintain virtual aspect ratio despite screen size
        gamePort = new FitViewport(MultiMage.V_WIDTH / MultiMage.PPM, MultiMage.V_HEIGHT / MultiMage.PPM, gameCam);
        // create hud
        hud = new Hud(game.batch);

        // load and setup map
        mapLoader = new TmxMapLoader();
        map = mapLoader.load(levelPath);
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MultiMage.PPM);

        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, -10), true);
        box2DDebugRenderer = new Box2DDebugRenderer();
        creator = new WorldCreator(this);

        player = new Mage(this);

        world.setContactListener(new WorldContactListener());

        items = new Array<>();
        itemsToSpawn = new LinkedBlockingQueue<>();
        levers = new ArrayList<>();

        // AI behaviour
        target = new SteeringBehaviourAI(player.body, 10);
        for (Ghost g : creator.getGhosts()) {
            Arrive<Vector2> arriveSB = new Arrive<Vector2>(g.entity, target)
                    .setTimeToTarget(0.03f)
                    .setArrivalTolerance(2f)
                    .setDecelerationRadius(0);
            g.entity.setBehaviour(arriveSB);
        }
        Arrive<Vector2> arriveSB = new Arrive<Vector2>(creator.getDemon().entity, target)
                .setTimeToTarget(0.1f)
                .setArrivalTolerance(1.5f)
                .setDecelerationRadius(0);
        creator.getDemon().entity.setBehaviour(arriveSB);
    }

    /// SECOND CONSTRUCTOR FOR TRANSITION BETWEEN LEVELS
    /// SECOND CONSTRUCTOR FOR TRANSITION BETWEEN LEVELS
    /// SECOND CONSTRUCTOR FOR TRANSITION BETWEEN LEVELS
    /// SECOND CONSTRUCTOR FOR TRANSITION BETWEEN LEVELS
    /// SECOND CONSTRUCTOR FOR TRANSITION BETWEEN LEVELS
    /// SECOND CONSTRUCTOR FOR TRANSITION BETWEEN LEVELS
    /// SECOND CONSTRUCTOR FOR TRANSITION BETWEEN LEVELS
    public PlayScreen(MultiMage game, Mage mage, int whatLevelToSet) {
        mapLoader = new TmxMapLoader();
        // HERE LOAD LEVEL and all further thing
        player = mage; // MAGE ALREADY EXISTS, BUT MAYBE NEEDS TO BE REDEFINED BECAUSE OF IT'S PHYSICAL BODY
        // MAYBE CREATE NEW MAGE AND PASS HIM ITEMS, LEVEL ETC
        mapLoader = new TmxMapLoader();
        if (whatLevelToSet == 1) {
            map = mapLoader.load("levels/level1.tmx");
            MultiMage.music = MultiMage.manager.get("audio/music/first_level_music.ogg", Music.class);
            xMaxCord = 4690;
            yMaxCord = 1675;
            xMaxCamCord = 38.239f;
            yMaxCamCord = 11.923f;
        } else if (whatLevelToSet == 2) {
            map = mapLoader.load("levels/level2.tmx");
            MultiMage.music = MultiMage.manager.get("audio/music/second_level_music.ogg", Music.class);
            xMaxCord = 3086;
            yMaxCord = 1035;
            xMaxCamCord = 22.24f;
            yMaxCamCord = 5.52f;
        } else if (whatLevelToSet == 3) {
            map = mapLoader.load("levels/level3.tmx");
            MultiMage.music = MultiMage.manager.get("audio/music/third_level_music.ogg", Music.class);
            xMaxCord = 3086;
            yMaxCord = 1035;
            xMaxCamCord = 22.24f;
            yMaxCamCord = 5.52f;
        }
        MultiMage.music.setVolume(Gdx.app.getPreferences("com.multimage.settings")
                .getFloat("volume", 0.5f));
        MultiMage.music.setLooping(true);
        if (Gdx.app.getPreferences("com.multimage.settings")
                .getBoolean("music.enabled", true)) {
            MultiMage.music.play();
        }
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MultiMage.PPM);

        this.game = game;
        atlas = new TextureAtlas("entity/mage/MageTextures.pack");
        atlasGhost = new TextureAtlas("entity/enemies/ghost.pack");
        atlasDemon = new TextureAtlas("entity/enemies/demon.pack");

        // cam that follows you
        gameCam = new OrthographicCamera();
        // maintain virtual aspect ratio despite screen size
        gamePort = new FitViewport(MultiMage.V_WIDTH / MultiMage.PPM, MultiMage.V_HEIGHT / MultiMage.PPM, gameCam);
        // create hud
        hud = new Hud(game.batch);

        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, -10), true);
        box2DDebugRenderer = new Box2DDebugRenderer();
        creator = new WorldCreator(this);

        player = new Mage(this);

        world.setContactListener(new WorldContactListener());

        items = new Array<>();
        itemsToSpawn = new LinkedBlockingQueue<>();
        levers = new ArrayList<>();

        // AI behaviour
        target = new SteeringBehaviourAI(player.body, 10);
        for (Ghost g : creator.getGhosts()) {
            Arrive<Vector2> arriveSB = new Arrive<Vector2>(g.entity, target)
                    .setTimeToTarget(0.03f)
                    .setArrivalTolerance(2f)
                    .setDecelerationRadius(0);
            g.entity.setBehaviour(arriveSB);
        }
        Arrive<Vector2> arriveSB = new Arrive<Vector2>(creator.getDemon().entity, target)
                .setTimeToTarget(0.1f)
                .setArrivalTolerance(1.5f)
                .setDecelerationRadius(0);
        creator.getDemon().entity.setBehaviour(arriveSB);
    }
    /// SECOND CONSTRUCTOR FOR TRANSITION BETWEEN LEVELS ABOVE
    /// SECOND CONSTRUCTOR FOR TRANSITION BETWEEN LEVELS ABOVE
    /// SECOND CONSTRUCTOR FOR TRANSITION BETWEEN LEVELS ABOVE
    /// SECOND CONSTRUCTOR FOR TRANSITION BETWEEN LEVELS ABOVE
    /// SECOND CONSTRUCTOR FOR TRANSITION BETWEEN LEVELS ABOVE

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

       for (Ghost enemy: creator.getGhosts()) {
           enemy.update(delta);
           enemy.entity.update(delta);
       }

       //if (isDoorOpened) {
       creator.getDemon().update(delta);
       creator.getDemon().entity.update(delta);


       for (Item item : items) {
            item.update(delta);
       }

       if (player.body.getPosition().x < 435 / MultiMage.PPM) {
           gameCam.position.x = gamePort.getWorldWidth() / 2;
       } else if (player.body.getPosition().x > xMaxCord / MultiMage.PPM) {
           gameCam.position.x = gamePort.getWorldWidth() + xMaxCamCord;
       } else { gameCam.position.x = player.body.getPosition().x; }

       if (player.body.getPosition().y < 245 / MultiMage.PPM) {
           gameCam.position.y = gamePort.getWorldHeight() / 2;
       } else if (player.body.getPosition().y > yMaxCord / MultiMage.PPM) {
           gameCam.position.y = gamePort.getWorldHeight() + yMaxCamCord;
       } else {
           gameCam.position.y = player.body.getPosition().y;
       }

       // update cam
       gameCam.update();
       // render only what camera sees
       renderer.setView(gameCam);

    }

    private void handleInput(float delta) { //
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && player.body.getLinearVelocity().y == 0.0f) {
            player.body.applyLinearImpulse(new Vector2(0, 5.75f), player.body.getWorldCenter(), true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.body.getLinearVelocity().x <= 2.4f) {
            player.body.applyLinearImpulse(new Vector2(0.25f, 0), player.body.getWorldCenter(), true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.body.getLinearVelocity().x >= -2.4f) {
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

        creator.getDemon().draw(game.batch);

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
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        box2DDebugRenderer.dispose();
        hud.dispose();
    }
}
