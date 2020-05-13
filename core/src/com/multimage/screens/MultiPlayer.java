package com.multimage.screens;

import com.badlogic.gdx.ApplicationAdapter;
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
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
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
// import network.packets.*;
import com.multimage.network.packets.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiPlayer extends ApplicationAdapter implements Screen {

    private MultiMage game;
    private TextureAtlas atlas;
    private TextureAtlas atlasGhost;
    private TextureAtlas atlasDemon;
    private List<Integer> levers;
    public static boolean isDoorOpened;

    private static OrthographicCamera gameCam;

    private Music music;

    // sprites
    private Mage player;
    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

    private Viewport gamePort;
    private Hud hud;

    // tiled map
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //box 2d
    private World world;
    private WorldCreator creator;

    private SteeringBehaviourAI target;

    Client GameClient= new Client();
    Kryo kryo = new Kryo();
    private boolean serverAnswer;

    Mage[] otherPlayer = new Mage[20];
    int index = 0;

    private int xMaxCord;
    private int yMaxCord;
    private float xMaxCamCord;
    private float yMaxCamCord;

    /// SECOND CONSTRUCTOR FOR TRANSITION BETWEEN LEVELS
    public MultiPlayer(MultiMage game, Mage mage, int whatLevelToSet, Hud oldHud) {
        MultiMage.music.stop();
        this.game = game;
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
        atlas = new TextureAtlas("entity/mage/mage.pack");
        atlasGhost = new TextureAtlas("entity/enemies/ghost.pack");
        atlasDemon = new TextureAtlas("entity/enemies/demon.pack");

        // cam that follows you
        gameCam = new OrthographicCamera();
        // maintain virtual aspect ratio despite screen size
        gamePort = new FitViewport(MultiMage.V_WIDTH / MultiMage.PPM, MultiMage.V_HEIGHT / MultiMage.PPM, gameCam);
        // create hud
        hud = oldHud;

        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, -10), true);
        creator = new WorldCreator(this);

        otherPlayer = new Mage[20];
        for (int i = 0; i < 10; i++) {
            otherPlayer[i] = new Mage(this);
        }
        player = new Mage(this);
        player.setItemsAndGetBonus(mage.getItems());

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

        kryo = GameClient.getKryo();
        kryo.register(FirstPacket.class);
        kryo.register(Request.class);
        kryo.register(RequestAnswer.class);
        kryo.register(Disconnect.class);
        kryo.register(Moving.class);
        kryo.register(Position.class);

        GameClient.addListener(new Listener() {

            @Override
            public void connected(Connection connection) {
                // GameClient.sendTCP(new Request());
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof RequestAnswer) {
                    serverAnswer = ((RequestAnswer) object).accepted;
                } else if (object instanceof FirstPacket) {
                    FirstPacket firstPacket = (FirstPacket) object;
                    player.id = firstPacket.id;
                    Position pos = new Position();
                    pos.playerID = firstPacket.id;
                    pos.posX = (float) player.body.getPosition().x;
                    pos.posY = (float) player.body.getPosition().y;
                    GameClient.sendTCP(pos);
                } else if (object instanceof Position) {
                    otherPlayer[index].id = ((Position) object).playerID;
                    otherPlayer[index].PosX = ((Position) object).posX;
                    otherPlayer[index].PosY = ((Position) object).posY;
                    otherPlayer[index].body.setTransform(new Vector2(otherPlayer[index].PosX, otherPlayer[index].PosY), 0);
                    index++;
                } else if (object instanceof Moving) {
                    for (int i = 0; i < index; i++) {
                        if (otherPlayer[i].id == ((Moving) object).post.playerID) {
                            otherPlayer[i].PosX = ((Moving) object).post.posX;
                            otherPlayer[i].PosY = ((Moving) object).post.posY;
                            otherPlayer[i].body.setTransform(new Vector2(otherPlayer[i].PosX, otherPlayer[i].PosY), 0);
                            otherPlayer[i].setDirection(((Moving) object).walkingRight);
                            break;
                        }
                    }
                } else if (object instanceof Disconnect) {
                    otherPlayer[((Disconnect) object).playerID] = null;
                    index--;
                }
            }
        });

        GameClient.start();

        try {
            GameClient.connect(5000, "193.40.255.37", 5200, 5201);
        } catch (IOException ex) {
            Logger.getLogger(MultiPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }

        FirstPacket fp = new FirstPacket();
        fp.x = player.body.getPosition().x;
        fp.y = player.body.getPosition().y;
        GameClient.sendTCP(fp); ////////////////////// NETWORK
    }
    /// SECOND CONSTRUCTOR FOR TRANSITION BETWEEN LEVELS ABOVE

    public MultiPlayer(MultiMage game) {
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

        atlas = new TextureAtlas("entity/mage/mage.pack");
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
        creator = new WorldCreator(this);

        for (int i = 0; i < 10; i++) {
            otherPlayer[i] = new Mage(this);
        }

        player = new Mage(this);

        music = MultiMage.manager.get("audio/music/main_menu_music.ogg", Music.class);
        music.stop();

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

        kryo = GameClient.getKryo();
        kryo.register(FirstPacket.class);
        kryo.register(Request.class);
        kryo.register(RequestAnswer.class);
        kryo.register(Disconnect.class);
        kryo.register(Moving.class);
        kryo.register(Position.class);
        kryo.register(Mage.State.class);

        GameClient.addListener(new Listener() {

            @Override
            public void connected(Connection connection) {
                // GameClient.sendTCP(new Request());
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof RequestAnswer) {
                    serverAnswer = ((RequestAnswer) object).accepted;
                } else if (object instanceof FirstPacket) {
                    FirstPacket firstPacket = (FirstPacket) object;
                    player.id = firstPacket.id;
                    Position pos = new Position();
                    pos.playerID = firstPacket.id;
                    pos.posX = (float) player.body.getPosition().x;
                    pos.posY = (float) player.body.getPosition().y;
                    GameClient.sendTCP(pos);
                } else if (object instanceof Position) {
                    otherPlayer[index].id = ((Position) object).playerID;
                    otherPlayer[index].PosX = ((Position) object).posX;
                    otherPlayer[index].PosY = ((Position) object).posY;
                    otherPlayer[index].body.setTransform(new Vector2(otherPlayer[index].PosX, otherPlayer[index].PosY), 0);
                    index++;
                } else if (object instanceof Moving) {
                    for (int i = 0; i < index; i++) {
                        if (otherPlayer[i].id == ((Moving) object).post.playerID) {
                            otherPlayer[i].PosX = ((Moving) object).post.posX;
                            otherPlayer[i].PosY = ((Moving) object).post.posY;
                            otherPlayer[i].body.setTransform(new Vector2(otherPlayer[i].PosX, otherPlayer[i].PosY), 0);
                            otherPlayer[i].setDirection(((Moving) object).walkingRight);
                            break;
                        }
                    }
                } else if (object instanceof Disconnect) {
                    otherPlayer[((Disconnect) object).playerID] = null;
                    index--;
                }
            }
        });

        GameClient.start();

        try {
            GameClient.connect(5000, "193.40.255.37", 5200, 5201);
        } catch (IOException ex) {
            Logger.getLogger(MultiPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }

        FirstPacket fp = new FirstPacket();
        fp.x = player.body.getPosition().x;
        fp.y = player.body.getPosition().y;
        GameClient.sendTCP(fp); ////////////////////// NETWORK

    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void show() { }

    public void update(float delta) {
        handleInput(delta);
        // item creation //
        handleSpawningItems();

        world.step(1/60f, 6, 2);

        player.update(delta);
        hud.update(delta);

        if (player.isDestroyed()) {
            GameClient.stop();
        }

        // doesn't draw because index is always 0
        if (index > 0) {
            for (int i = 0; i < index; i++) {
                otherPlayer[i].update(delta);
            }
        }

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
            player.body.applyLinearImpulse(new Vector2(0, player.jump()), player.body.getWorldCenter(), true);
            player.setCurrentState(Mage.State.JUMPING);
            Moving mv = new Moving();
            mv.walkingRight = player.isWalkingRight();
            mv.post.playerID = player.id;
            mv.post.posX = player.body.getPosition().x;
            mv.post.posY = player.body.getPosition().y;
            GameClient.sendUDP(mv);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.body.getLinearVelocity().x <= 2.4f) {
            player.body.applyLinearImpulse(new Vector2(player.speed, 0), player.body.getWorldCenter(), true);
            player.setCurrentState(Mage.State.WALKING);
            //player.setPosX(player.getPosX() - delta * player.getSpeed());
            Moving mv = new Moving();
            mv.walkingRight = true;
            mv.post.playerID = player.id;
            mv.post.posX = player.body.getPosition().x;
            mv.post.posY = player.body.getPosition().y;
            GameClient.sendUDP(mv);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.body.getLinearVelocity().x >= -2.4f) {
            player.body.applyLinearImpulse(new Vector2(-player.speed, 0), player.body.getWorldCenter(), true);
            //player.setPosX(player.getPosX() - delta * player.getSpeed());
            player.setCurrentState(Mage.State.WALKING);
            Moving mv = new Moving();
            mv.walkingRight = false;
            mv.post.playerID = player.id;
            mv.post.posX = player.body.getPosition().x;
            mv.post.posY = player.body.getPosition().y;
            GameClient.sendUDP(mv);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            player.setAttacking(true);
            player.fire();
            player.setAttacking(false);
        } else if(!Gdx.input.isKeyPressed(Input.Keys.D) && !Gdx.input.isKeyPressed(Input.Keys.S) && player.isAttacking()){
            player.setCurrentState(Mage.State.STANDING);
            player.setAttacking(false);
            Moving mv = new Moving();
            mv.post.playerID = player.id;
            mv.post.posX = player.body.getPosition().x;
            mv.post.posY = player.body.getPosition().y;
            GameClient.sendUDP(mv);
        } else if (!player.isAttacking() && !(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT))){
            player.setCurrentState(Mage.State.WALKING);
            Moving mv = new Moving();
            mv.walkingRight = true;
            mv.post.playerID = player.id;
            mv.post.posX = player.body.getPosition().x;
            mv.post.posY = player.body.getPosition().y;
            GameClient.sendUDP(mv);
        }
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // render game map
        renderer.render();

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();

        // DRAW myPLAYER
        player.draw(game.batch);

        // DRAW OTHER PLAYER
        if (index > 0){
            for (int i = 0; i < index; i++) {
                otherPlayer[i].draw(game.batch);
            }
        }

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

    @Override
    public void hide() { }

    @Override
    public void dispose () {
        map.dispose();
        renderer.dispose();
        world.dispose();
        hud.dispose();
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

    public World getWorld() {
        return world;
    }

    public TiledMap getMap() {
        return map;
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

    public TextureAtlas getAtlas(){
        return atlas;
    }

    public TextureAtlas getAtlasGhost() {
        return atlasGhost;
    }

    public TextureAtlas getAtlasDemon() {
        return atlasDemon;
    }

    public MultiMage getGame() {
        return game;
    }

    public Mage getPlayer() {
        return player;
    }

    public static float getCamPositionX() {
        return gameCam.position.x;
    }

    public static float getCamPositionY() {
        return gameCam.position.y;
    }

    public Hud getHud() {
        return hud;
    }

}
