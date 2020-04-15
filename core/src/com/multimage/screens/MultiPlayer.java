package com.multimage.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.multimage.MultiMage;
import com.multimage.item.Item;
import com.multimage.item.ItemDef;
import com.multimage.item.items.*;
import com.multimage.network.packets.*;
import com.multimage.scenes.Hud;
import com.multimage.sprites.Enemy;
import com.multimage.sprites.Mage;
import com.multimage.tools.WorldContactListener;
import com.multimage.tools.WorldCreator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiPlayer extends ApplicationAdapter implements Screen {

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

    Client GameClient= new Client();
    Kryo kryo = new Kryo();
    private boolean serverAnswer;

    Mage[] otherPlayer = new Mage[20];
    int index = 0;

    public MultiPlayer(MultiMage game) {
        this.game = game;

        atlas = new TextureAtlas("entity/mage/MageTextures.pack");
        atlasEnemy = new TextureAtlas("entity/enemies/ghost.pack");

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

        kryo = GameClient.getKryo();
        kryo.register(FirstPacket.class);
        kryo.register(Request.class);
        kryo.register(RequestAnswer.class);
        kryo.register(Disconnect.class);
        kryo.register(Moving.class);
        kryo.register(Position.class);
        kryo.register(Mage.State.class);

        GameClient.addListener(new Listener() {

            // @Override
            // public void connected(Connection connection) {
            //     // GameClient.sendTCP(new Request());
            //     index++;
            // }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof RequestAnswer) {
                    serverAnswer = ((RequestAnswer) object).accepted;
                    if (serverAnswer) {
                        GameClient.sendTCP(new FirstPacket());
                        System.out.println("Joined server");
                    } else {
                        System.out.println("Rejected");
                    }
                } else if (object instanceof FirstPacket) {
                    FirstPacket firstPacket = (FirstPacket) object;
                    System.out.println(firstPacket.id);
                    System.out.println(player);
                    player.id = firstPacket.id;
                    Position pos = new Position();
                    pos.playerID = firstPacket.id;
                    pos.posX = (int) player.getPosX();
                    pos.posY = (int) player.getPosY();
                    GameClient.sendTCP(pos);
                } else if (object instanceof Position) {
                    otherPlayer[index].id = ((Position) object).playerID;
                    otherPlayer[index].PosX = ((Position) object).posX;
                    otherPlayer[index].PosY = ((Position) object).posY;
                    index++;
                } else if (object instanceof Moving) {
                    for (int i = 0; i < index; i++) {
                        if (otherPlayer[i].id == ((Moving) object).post.playerID) {
                            otherPlayer[i].PosX = ((Moving) object).post.posX;
                            otherPlayer[i].PosY = ((Moving) object).post.posY;
                            otherPlayer[i].setCurrentState(((Moving) object).state);
                            otherPlayer[i].setDirection(((Moving) object).walkingRight);
                            break;
                        }
                    }
                } else if (object instanceof Disconnect) {
                    otherPlayer[((Disconnect) object).playerID] = null;
                    index--;
                    System.out.println(index + "DISCONNECTED");
                }
            }
        });

        GameClient.start();

        try {
            GameClient.connect(5000, "localhost", 5200, 5201);
        } catch (IOException ex) {
            Logger.getLogger(MultiPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }

        FirstPacket fp = new FirstPacket();
        GameClient.sendTCP(fp); ////////////////////// NETWORK

    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void show() {

    }

    public void update(float delta) {
        handleInput(delta);
        // item creation //
        // handleSpawningItems();

        world.step(1/60f, 6, 2);

        player.update(delta);

        // doesn't draw because index is always 0
        if (index > 0) {
            for (int i = 0; i < index; i++) {
                otherPlayer[i].update(delta);
            }
        }


        //for (Enemy enemy: creator.getGhosts()) {
        //    enemy.update(delta);
        //}

        //for (Item item : items) {
        //    item.update(delta);
        //}

        gameCam.position.x = player.body.getPosition().x;
        gameCam.position.y = player.body.getPosition().y;

        // update cam
        gameCam.update();
        // render only what camera sees
        renderer.setView(gameCam);
    }

    private void handleInput(float delta) { //
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && player.body.getLinearVelocity().y == 0.0f) {
            player.body.applyLinearImpulse(new Vector2(0, 5.75f), player.body.getWorldCenter(), true);
            player.setCurrentState(Mage.State.JUMPING);
            Moving mv = new Moving();
            mv.state = Mage.State.JUMPING;
            mv.post.playerID = player.id;
            mv.post.posX = (int) player.getPosX();
            mv.post.posY = (int) player.getPosY();
            GameClient.sendTCP(mv);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.body.getLinearVelocity().x <= 2.4f) {
            player.body.applyLinearImpulse(new Vector2(0.25f, 0), player.body.getWorldCenter(), true);
            //player.setPosX(player.getPosX() - delta * player.getSpeed());
            Moving mv = new Moving();
            mv.walkingRight = true;
            mv.state = Mage.State.WALKING;
            mv.post.playerID = player.id;
            mv.post.posX = (int) player.getPosX();
            mv.post.posY = (int) player.getPosY();
            GameClient.sendTCP(mv);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.body.getLinearVelocity().x >= -2.4f) {
            player.body.applyLinearImpulse(new Vector2(-0.25f, 0), player.body.getWorldCenter(), true);
            //player.setPosX(player.getPosX() - delta * player.getSpeed());
            Moving mv = new Moving();
            mv.walkingRight = false;
            mv.state = Mage.State.WALKING;
            mv.post.playerID = player.id;
            mv.post.posX = (int) player.getPosX();
            mv.post.posY = (int) player.getPosY();
            GameClient.sendTCP(mv);
        }
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

        // DRAW myPLAYER
        player.draw(game.batch);

        // DRAW OTHER PLAYER
        if (index > 0){
            for (int i = 0; i < index; i++) {
                otherPlayer[i].draw(game.batch);
            }
        }

        //for (Enemy enemy: creator.getGhosts()) {
        //    enemy.draw(game.batch);
        //}

        // item creation
        //for (Item item : items) {
        //    item.draw(game.batch);
        //}
        game.batch.end();

        // PROTOTYPE HUD
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose () {
        map.dispose();
        renderer.dispose();
        world.dispose();
        box2DDebugRenderer.dispose();
        hud.dispose();
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
}
