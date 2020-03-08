package com.multimage.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class Levels implements Screen {

    private Stage stage;
    private Table table;
    private TextureAtlas atlas;
    private Skin skin;
    private TextureAtlas atlasForList;
    private Skin skinForList;
    private List<String> list;
    private ScrollPane scrollPane;
    private TextButton play, back;

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        atlas = new TextureAtlas("ui/button.pack");
        skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), atlas);

        atlasForList = new TextureAtlas("ui/skin.atlas");
        skinForList = new Skin(atlasForList);
        skinForList = new Skin(atlasForList);
        skinForList.load(Gdx.files.internal("ui/skin.json"));

        table = new Table(skin);
        // table.debug();

        list = new List<String>(skinForList);
        list.setItems("ONE", "TWO", "THREE", "FOUR", "FIVE");

        scrollPane = new ScrollPane(list, skinForList);

        play = new TextButton("PLAY", skin);
        play.pad(15f);
        back = new TextButton("BACK", skin, "small");
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
            }
        });
        back.pad(10f);


        setUpTable();
        stage.addActor(table);
    }

    private void setUpTable() {
        table.clear();
        table.setBounds(0,0, stage.getWidth(), stage.getHeight());
        table.add().width(table.getWidth() / 3);
        table.add("LEVEL SELECTION").width(table.getWidth() / 3);
        table.add().width(table.getWidth() / 3).row();
        table.add(scrollPane).left().expandY();
        table.add(play);
        table.add(back).bottom().right();
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
        table.setClip(true);
        setUpTable();
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
        atlasForList.dispose();
        skinForList.dispose();
    }
}
