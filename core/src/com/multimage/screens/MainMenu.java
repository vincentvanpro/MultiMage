package com.multimage.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.multimage.MultiMage;
import com.multimage.tween.ActorAccessor;

public class MainMenu implements Screen {

    private final MultiMage game;
    private Stage stage;
    private TextureAtlas atlas;
    private Skin skin;
    private Table table;
    private TweenManager tweenManager;

    public MainMenu(MultiMage game) {
        this.game = game;
    }

    public MultiMage getGame() {
        return game;
    }


    @Override
    public void show() {
        stage = new Stage();

        Gdx.input.setInputProcessor(stage);

        atlas = new TextureAtlas("ui/button.pack");
        skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), atlas);

        table = new Table(skin);
        table.setFillParent(true);
        table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        TextButton buttonPlay = new TextButton("PLAY", skin);
        buttonPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new Levels(game));
            }
        });
        buttonPlay.pad(15);

        TextButton buttonMultiPlayer = new TextButton("MULTIPLAYER", skin);
        buttonMultiPlayer.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MultiPlayer(game));
            }
        });
        buttonMultiPlayer.pad(15);

        TextButton buttonPreferences = new TextButton("SETTINGS", skin);
        buttonPreferences.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new PreferencesMenu(game));
            }
        });
        buttonPreferences.pad(15);

        TextButton buttonExit = new TextButton("EXIT", skin);
        buttonExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        buttonExit.pad(15);

        // heading
        Label heading = new Label(MultiMage.TITLE, skin);
        heading.setFontScale(1f);

        // putting in a table
        table.add(heading);
        table.getCell(heading).spaceBottom(100);
        table.row();
        table.add(buttonPlay);
        table.getCell(heading).spaceBottom(15);
        table.row();
        table.add(buttonMultiPlayer);
        table.getCell(heading).spaceBottom(15);
        table.getCell(buttonMultiPlayer).spaceTop(5);
        table.row();
        table.add(buttonPreferences);
        table.getCell(heading).spaceBottom(15);
        table.getCell(buttonPreferences).spaceTop(5);
        table.row();
        table.add(buttonExit);
        table.getCell(buttonExit).spaceTop(5);
        // table.debug(); // adds an opportunity to see boxes // REMOVE LATER
        stage.addActor(table);

        // creating animations
        tweenManager = new TweenManager();

        Tween.registerAccessor(Actor.class, new ActorAccessor());

        // heading rgb animation
        Timeline.createSequence().beginSequence()
                .push(Tween.to(heading, ActorAccessor.RGB, .5f).target(0, 0, 1))
                .push(Tween.to(heading, ActorAccessor.RGB, .5f).target(0, 1, 0))
                .push(Tween.to(heading, ActorAccessor.RGB, .5f).target(0, 1, 1))
                .push(Tween.to(heading, ActorAccessor.RGB, .5f).target(1, 0, 0))
                .push(Tween.to(heading, ActorAccessor.RGB, .5f).target(1, 1, 0))
                .push(Tween.to(heading, ActorAccessor.RGB, .5f).target(1, 0, 1))
                .push(Tween.to(heading, ActorAccessor.RGB, .5f).target(1, 1, 1))
                .end().repeat(Tween.INFINITY, 0).start(tweenManager);

        // heading and buttons fade-in
        Timeline.createSequence().beginSequence()
                .push(Tween.set(buttonPlay, ActorAccessor.ALPHA).target(0))
                .push(Tween.set(buttonMultiPlayer, ActorAccessor.ALPHA).target(0))
                .push(Tween.set(buttonPreferences, ActorAccessor.ALPHA).target(0))
                .push(Tween.set(buttonExit, ActorAccessor.ALPHA).target(0))
                .push(Tween.from(heading, ActorAccessor.ALPHA, .5f).target(0))
                .push(Tween.to(buttonPlay, ActorAccessor.ALPHA, .5f).target(1))
                .push(Tween.to(buttonMultiPlayer, ActorAccessor.ALPHA, .5f).target(1))
                .push(Tween.to(buttonPreferences, ActorAccessor.ALPHA, .5f).target(1))
                .push(Tween.to(buttonExit, ActorAccessor.ALPHA, .5f).target(1))
                .end().start(tweenManager);

        // table fade-in
        Tween.from(table, ActorAccessor.ALPHA, .5f).target(0).start(tweenManager);
        Tween.from(table, ActorAccessor.Y, .5f).target(Gdx.graphics.getHeight() / 8).start(tweenManager);

        tweenManager.update(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        tweenManager.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        table.invalidateHierarchy(); // table sizes recalculated
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
        atlas.dispose();
        skin.dispose();
    }
}
