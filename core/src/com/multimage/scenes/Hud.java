package com.multimage.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.multimage.MultiMageGame;

public class Hud {
    // PROTOTYPE

    // TODO MAKE NORMAL HUD IN THE FUTURE
    public Stage stage;
    private Viewport viewport;

    private Integer worldTimer;
    private float timeCount;
    private Integer score;

    Label countDownLabel;
    Label scoreLabel;
    Label timeLabel;
    Label levelLabel;
    Label worldLabel;
    Label mageLabel;

    public Hud(SpriteBatch spriteBatch) {
        worldTimer = 300;
        timeCount = 0;
        score = 0;

        TextureAtlas atlas = new TextureAtlas("ui/button.pack");
        Skin skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), atlas);

        viewport = new FitViewport(MultiMageGame.V_WIDTH, MultiMageGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, spriteBatch);

        Table table = new Table();
        table.top();
        table.setFillParent(true); // table is size of a stage

        countDownLabel = new Label(String.format("%03d", worldTimer), skin, "small");
        scoreLabel = new Label(String.format("%03d", score), skin, "small");
        timeLabel = new Label("TIME", skin, "small");
        levelLabel = new Label("1-1", skin, "small");
        worldLabel = new Label("WORLD", skin, "small");
        mageLabel = new Label("MAGE", skin, "small");

        table.add(mageLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.row();
        table.add(scoreLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(countDownLabel).expandX();

        stage.addActor(table);
    }
}
