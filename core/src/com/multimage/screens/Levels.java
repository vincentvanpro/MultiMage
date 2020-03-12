package com.multimage.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.multimage.MultiMageGame;


public class Levels implements Screen {

    private Stage stage;
    private Table table;
    private Skin skin;
    private TextureAtlas atlasForList;
    private Skin skinForList;
    private ScrollPane scrollPane;
    private TextButton play, back;

    private MultiMageGame game;

    public Levels(MultiMageGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), new TextureAtlas("ui/button.pack"));

        atlasForList = new TextureAtlas("ui/skin.atlas");
        skinForList = new Skin(atlasForList);
        skinForList.load(Gdx.files.internal("ui/skin.json"));

        table = new Table(skin);
        table.setFillParent(true);
        // table.debug();

        List<String> list = new List<String>(skinForList);
        list.setItems("ONE", "TWO", "THREE", "FOUR", "FIVE");

        scrollPane = new ScrollPane(list, skinForList);

        play = new TextButton("PLAY", skin);
        play.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new PlayScreen(game));
            }

        });
        play.pad(15f);
        back = new TextButton("BACK", skin, "small");
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu(game));
            }
        });
        back.pad(10f);

        table.clear();
        table.setBounds(0,0, stage.getWidth(), stage.getHeight());
        table.add().width(table.getWidth() / 3);
        table.add(new Label("SELECT LEVEL", skin, "big")).width(table.getWidth() / 3);
        table.add().width(table.getWidth() / 3).row();
        table.add(scrollPane).left().expandY();
        table.add(play).uniformX();
        table.add(back).uniformX().bottom().right();

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        table.invalidateHierarchy();
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
        skin.dispose();
        atlasForList.dispose();
        skinForList.dispose();
    }
}
