package com.multimage.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.multimage.MultiMage;

public class Hud implements Disposable {
    // PROTOTYPE

    // TODO MAKE NORMAL HUD IN THE FUTURE
    public Stage stage;
    private Viewport viewport;

    private Integer worldTimerMin;
    private Integer worldTimerSek;
    private float timeCount;
    private Integer score;
    private Integer exp;
    private Integer level;

    Label countDownLabel;
    Label scoreLabel;
    Label timeLabel;
    Label levelLabel;
    Label worldLabel;
    Label mageLabel;
    Label experienceLvlLabel;

    public Hud(SpriteBatch spriteBatch) {
        worldTimerMin = 0;
        worldTimerSek = 0;
        timeCount = 0;
        score = 0;
        exp = 0;
        level = 1;

        TextureAtlas atlas = new TextureAtlas("ui/button.pack");
        Skin skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), atlas);

        viewport = new FitViewport(MultiMage.V_WIDTH, MultiMage.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, spriteBatch);

        Table table = new Table();
        table.top();
        table.setFillParent(true); // table is size of a stage

        countDownLabel = new Label(worldTimerMin + " m " + worldTimerSek + " s", skin, "small");
        scoreLabel = new Label(String.format("%03d", score), skin, "small");
        timeLabel = new Label("TIME", skin, "small");
        levelLabel = new Label("1-1", skin, "small");
        worldLabel = new Label("WORLD", skin, "small");
        mageLabel = new Label("MAGE", skin, "small");
        experienceLvlLabel = new Label("LVL " + level +"    EXP " + exp + "/100", skin, "small");

        table.add(mageLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.row();
        table.add(scoreLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(countDownLabel).expandX();

        experienceLvlLabel.setPosition(35,65);
        experienceLvlLabel.setAlignment(Align.bottomLeft);
        stage.addActor(experienceLvlLabel);

        stage.addActor(table);
    }

    public void update(float delta) {
        timeCount += delta;
        if(timeCount >= 1) {
            worldTimerSek++;
            if (worldTimerSek == 60) {
                worldTimerMin++;
                worldTimerSek = 0;
            }
            countDownLabel.setText(worldTimerMin + "m " + worldTimerSek + "s");
            timeCount = 0;
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
