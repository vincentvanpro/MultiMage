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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.multimage.MultiMage;
import com.multimage.network.packets.*;
import com.multimage.scenes.Hud;
import com.multimage.sprites.Mage;
import com.multimage.tools.WorldCreator;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiPlayer extends ApplicationAdapter implements Screen, InputProcessor {

    MultiMage game;
    SpriteBatch batch;

    Client GameClient= new Client();
    Kryo kryo = new Kryo();

    Mage[] otherPlayer = new Mage[20];
    int index = 0;

    public MultiPlayer(MultiMage game){
        this.game = game;
        batch = game.batch;

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
                GameClient.sendTCP(new Request());
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof RequestAnswer) {
                    boolean serverAnswer = ((RequestAnswer) object).accepted;
                    if (serverAnswer) {
                        GameClient.sendTCP(new FirstPacket());
                        System.out.println("Joined server");
                    } else {
                        System.out.println("Rejected");
                    }
                } else if (object instanceof FirstPacket) {
                    FirstPacket firstPacket = (FirstPacket) object;
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
                }
            }
        });
        GameClient.start();
        try {
            GameClient.connect(5000, "localhost", 54555);
        } catch (IOException ex) {
            Logger.getLogger(MultiPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    Mage player;
    float timePassed = 0;

    @Override
    public void create () {
        Gdx.graphics.setResizable(false);
        Gdx.graphics.setVSync(true);
        Gdx.graphics.setWindowedMode(800, 600);
        Gdx.input.setInputProcessor(this);
        for (int i = 0; i < 10; i++) {
            otherPlayer[i] = new Mage();
        }
        // bg.setColor(Color.ORANGE);
        batch = new SpriteBatch();
        player = new Mage();
        FirstPacket fp = new FirstPacket();
        GameClient.sendTCP(fp); ////////////////////// NETWORK
//        shooterAtlas = new TextureAtlas(Gdx.files.internal("shooter.atlas"));
//        animation = new Animation(1/8f, shooterAtlas.getRegions());
    }
    @Override
    public void render () {

        timePassed += Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0, 1, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        // batch.draw(bg.getTexture(), 0, 0);
        // batch.setColor(Color.BROWN);

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (player.getPosX() >= -64)
                player.setPosX(player.getPosX() - Gdx.graphics.getDeltaTime() * player.speed);
            Moving mv = new Moving();
            mv.walkingRight = false;
            mv.state = Mage.State.WALKING;
            mv.post.playerID = player.id;
            mv.post.posX = (int) player.getPosX();
            mv.post.posY = (int) player.getPosY();
            GameClient.sendTCP(mv);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (player.getPosX() <= 600)
                player.setPosX(player.getPosX() + Gdx.graphics.getDeltaTime() * player.speed);
            Moving mv = new Moving();
            mv.walkingRight = true;
            mv.state = Mage.State.WALKING;
            mv.post.playerID = player.id;
            mv.post.posX = (int) player.getPosX();
            mv.post.posY = (int) player.getPosY();
            GameClient.sendTCP(mv);
        }

        //////////////////// DRAW OTHER PLAYER /////////////////////
        if (index > 0){
            for (int i = 0; i < index; i++) {
                batch.draw(otherPlayer[i].getFrame(timePassed), otherPlayer[i].getPosX(), otherPlayer[i].getPosY(), 256, 256);
            }
        }
        // DRAW myPLAYER
        batch.draw(player.getFrame(timePassed), player.getPosX(), player.getPosY(), 256, 256);
        // batch.setColor(255, 100, 100, 5);

        batch.end();

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose () {
        batch.dispose();
        // player.AtlasDispose();
    }

    @Override
    public boolean keyUp(int i) {
        if(!Gdx.input.isKeyPressed(Input.Keys.D) && !Gdx.input.isKeyPressed(Input.Keys.S)){
//            if (Gdx.input.isKeyPressed(Keys.LEFT)) {// && player.isAttacking()
//                player.setCurrentState(Character.CharacterState.WALKING);
//                player.setDirection(Character.CharacterDirection.LEFT);
//            } else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
//                player.setCurrentState(Character.CharacterState.WALKING);
//                player.setDirection(Character.CharacterDirection.RIGHT);
//            } else {
//                player.setCurrentState(Character.CharacterState.IDLE);
//            }
            player.setCurrentState(Mage.State.STANDING);
            // player.setAttacking(false);
            // player.setSpeed(300);
            Moving mv = new Moving();
            mv.state = Mage.State.STANDING;
            mv.post.playerID = player.id;
            mv.post.posX = (int) player.getPosX();
            mv.post.posY = (int) player.getPosY();
            GameClient.sendTCP(mv);
            return true;
        }
        // player.setSpeed(0);
        //if (!player.isAttacking() && !(Gdx.input.isKeyPressed(Input.Keys.LEFT)||Gdx.input.isKeyPressed(Keys.RIGHT))){
        //    player.setCurrentState(Mage.State.STANDING);
        //    Moving mv = new Moving();
        //    mv.state = Mage.State.STANDING;
        //    mv.post.playerID = player.id;
        //    mv.post.posX = (int) player.getPosX();
        //    mv.post.posY = (int) player.getPosY();
        //    GameClient.sendTCP(mv);
        //}
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        // System.out.println("keyDown");
        //if (!player.isAttacking()){
        //    if (keycode == Input.Keys.RIGHT){
        //        player.setCurrentState(Mage.State.WALKING);
        //        //player.setDirection(Character.CharacterDirection.RIGHT);
        //        //player.setSpeed(300);
        //    } else if (keycode == Input.Keys.LEFT) {
        //        player.setCurrentState(Mage.State.WALKING);
        //        //player.setDirection(Character.CharacterDirection.LEFT);
        //        //player.setSpeed(300);
        //    } else if (keycode == Input.Keys.D) {
        //        player.setCurrentState(Character.CharacterState.ATTACKING);
        //        player.setSpeed(0);
        //        player.setAttacking(true);
        //        Moving mv = new Moving();
        //        mv.state = CharacterState.ATTACKING;
        //        mv.walkingRight = player.getDirection();
        //        mv.post.playerID = player.id;
        //        mv.post.posX = (int) player.getPosX();
        //        mv.post.posY = (int) player.getPosY();
        //        GameClient.sendTCP(mv);
        //    } else if (keycode == Input.Keys.S) {
        //        player.setCurrentState(Character.CharacterState.SHOOTING);
        //        player.setSpeed(0);
        //        player.setAttacking(true);
        //        Moving mv = new Moving();
        //        mv.state = CharacterState.SHOOTING;
        //        mv.walkingRight = player.getDirection();
        //        mv.post.playerID = player.id;
        //        mv.post.posX = (int) player.getPosX();
        //        mv.post.posY = (int) player.getPosY();
        //        GameClient.sendTCP(mv);
        //    }
        //}
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
