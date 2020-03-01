package com.multimage.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.multimage.MultiMageGame;
import com.multimage.tween.ActorAccessor;

public class MainMenu implements Screen {

    private Stage stage;
    private TextureAtlas atlas;
    private Skin skin;
    private Table table;
    private TextButton buttonPlay, buttonExit, buttonPreferences;
    private BitmapFont white, black;
    private Label heading;
    private TweenManager tweenManager;


    @Override
    public void show() {
        stage = new Stage();

        Gdx.input.setInputProcessor(stage);

        atlas = new TextureAtlas("ui/button.pack");
        skin = new Skin(atlas);

        table = new Table(skin);
        table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // fonts
        white = new BitmapFont(Gdx.files.internal("font/white32.fnt"), false);
        black = new BitmapFont(Gdx.files.internal("font/black32.fnt"), false);

        // buttons
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("button.up");
        textButtonStyle.up = skin.getDrawable("button.down");
        textButtonStyle.pressedOffsetX = 1;
        textButtonStyle.pressedOffsetY = -1;
        textButtonStyle.font = black;

        buttonExit = new TextButton("EXIT", textButtonStyle);
        buttonExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        buttonExit.pad(15);

        buttonPlay = new TextButton("PLAY", textButtonStyle);
        buttonPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new Levels());
            }
        });
        buttonPlay.pad(15);

        buttonPreferences = new TextButton("SETTINGS", textButtonStyle);
        buttonPreferences.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new PreferencesMenu());
            }
        });
        buttonPreferences.pad(15);

        // heading
        heading = new Label(MultiMageGame.TITLE, new Label.LabelStyle(white, Color.WHITE));
        heading.setFontScale(1f);

        // putting in a table
        table.add(heading);
        table.getCell(heading).spaceBottom(100);
        table.row();
        table.add(buttonPlay);
        table.getCell(heading).spaceBottom(15);
        table.row();
        table.add(buttonPreferences);
        table.getCell(heading).spaceBottom(15);
        table.row();
        table.add(buttonExit);
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
                .push(Tween.set(buttonPreferences, ActorAccessor.ALPHA).target(0))
                .push(Tween.set(buttonExit, ActorAccessor.ALPHA).target(0))
                .push(Tween.from(heading, ActorAccessor.ALPHA, .5f).target(0))
                .push(Tween.to(buttonPlay, ActorAccessor.ALPHA, .5f).target(1))
                .push(Tween.to(buttonPreferences, ActorAccessor.ALPHA, .5f).target(1))
                .push(Tween.to(buttonExit, ActorAccessor.ALPHA, .5f).target(1))
                .end().start(tweenManager);

        // table fade-in
        Tween.from(table, ActorAccessor.ALPHA, .5f).target(0).start(tweenManager);
        Tween.from(table, ActorAccessor.Y, .5f).target(Gdx.graphics.getHeight() / 8).start(tweenManager);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        tweenManager.update(delta);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
        atlas.dispose();
        skin.dispose();
        white.dispose();
        black.dispose();
    }
}
