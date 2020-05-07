package com.multimage.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.multimage.MultiMage;

public class GameOverScreen implements Screen {
    private Viewport viewport;
    private Stage stage;
    private TextureAtlas atlas;
    private Skin skin;

    private Game game;

    public GameOverScreen(Game game, String time) {
        this.game = game;

        atlas = new TextureAtlas("ui/button.pack");
        skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), atlas);

        viewport = new FitViewport(MultiMage.V_WIDTH, MultiMage.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((MultiMage) game).batch);

        Table table = new Table(skin);
        table.center();
        table.setFillParent(true);

        Label gameOverLabel = new Label("GAME OVER", skin, "big");
        Label scoreLabel = new Label("YOUR TIME IS: " + time, skin);
        Label playAgainLabel = new Label("Click to Play Again", skin, "small");


        table.add(gameOverLabel).expandX();
        table.row();
        table.add(scoreLabel).expandX();
        table.row();
        table.add(playAgainLabel).expandX().padTop(10f);

        stage.addActor(table);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if(Gdx.input.justTouched()) {
            PlayScreen.levelNumber = 1;
            game.setScreen(new PlayScreen((MultiMage) game));
            dispose();
        }
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

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
    }
}
