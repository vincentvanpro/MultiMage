package com.multimage.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.multimage.MultiMage;


public class PreferencesMenu implements Screen {

    private static final String PREF_MUSIC_VOLUME = "volume";
    public static final String PREF_MUSIC_ENABLED = "music.enabled";
    private static final String PREFS_NAME = "com.multimage.settings";
    private static final String PREF_FULLSCREEN_ENABLED = "fullscreen.enabled";
    private static final String PREF_VSYNC_ENABLED = "vsync.enabled";

    private String muteMusicString;

    private Stage stage;
    private TextureAtlas atlas;

    private Skin skin;
    private Skin skinForSlidersAndCheckBox;
    private TextureAtlas atlasUiAtlas;

    private MultiMage game;

    public PreferencesMenu(MultiMage game){
        this.game = game;
    }

    protected Preferences getPrefs() {
        return Gdx.app.getPreferences(PREFS_NAME);
    }

    public boolean isFullscreenEnabled() {
        return getPrefs().getBoolean(PREF_FULLSCREEN_ENABLED, true);
    }

    public void setFullscreenEnabled(boolean fullscreenEnabled) {
        getPrefs().putBoolean(PREF_FULLSCREEN_ENABLED, fullscreenEnabled);
        getPrefs().flush();
    }

    public boolean isVSyncEnabled() {
        return getPrefs().getBoolean(PREF_VSYNC_ENABLED, true);
    }

    public void setVsyncEnabled(boolean vsyncEnabled) {
        getPrefs().putBoolean(PREF_VSYNC_ENABLED, vsyncEnabled);
        getPrefs().flush();
    }

    public boolean isMusicEnabled() {
        return getPrefs().getBoolean(PREF_MUSIC_ENABLED, true);
    }

    public void setMusicEnabled(boolean musicEnabled) {
        getPrefs().putBoolean(PREF_MUSIC_ENABLED, musicEnabled);
        getPrefs().flush();
    }

    public float getMusicVolume() {
        return getPrefs().getFloat(PREF_MUSIC_VOLUME, 0.5f);
    }

    public void setMusicVolume(float volume) {
        getPrefs().putFloat(PREF_MUSIC_VOLUME, volume);
        getPrefs().flush();
    }

    @Override
    public void show() {
        stage = new Stage();

        Gdx.input.setInputProcessor(stage);

        // font
        BitmapFont black = new BitmapFont(Gdx.files.internal("font/black32.fnt"), false);

        atlas = new TextureAtlas("ui/button.pack");
        skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), atlas);
        atlasUiAtlas = new TextureAtlas("ui/skin.atlas");
        skinForSlidersAndCheckBox = new Skin(atlasUiAtlas);
        skinForSlidersAndCheckBox.add("default-font", black);
        skinForSlidersAndCheckBox.load(Gdx.files.internal("ui/skin.json"));

        final Table table = new Table(skin);
        table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        table.setFillParent(true);


        // musicBox
        final CheckBox musicCheckbox = new CheckBox("MUSIC", skinForSlidersAndCheckBox);
        musicCheckbox.setChecked(!isMusicEnabled());
        musicCheckbox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean enabled = musicCheckbox.isChecked();
                setMusicEnabled(!enabled);
                if (!enabled) {
                    game.music.play();
                } else {
                    game.music.pause();
                }
                Gdx.app.log(MultiMage.TITLE, "Music " + (isMusicEnabled() ? "enabled" : "paused"));
                muteMusicString = isMusicEnabled() ? "mute music" : "unmute music";
                //table.
                }
            });

        // fullscreenBox
        final CheckBox fullscreenCheckbox = new CheckBox("", skinForSlidersAndCheckBox);
        fullscreenCheckbox.setChecked(!isFullscreenEnabled());
        fullscreenCheckbox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean enabled = fullscreenCheckbox.isChecked();
                setFullscreenEnabled(!enabled);
                Gdx.app.log(MultiMage.TITLE, "Fullscreen " + (isFullscreenEnabled() ? "enabled" : "disabled"));
                //Gdx.graphics.setFullscreenMode(new Graphics.DisplayMode(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 60, 100));
            }
        });

        // vsyncBox
        final CheckBox vsyncCheckbox = new CheckBox("", skinForSlidersAndCheckBox);
        vsyncCheckbox.setChecked(!isVSyncEnabled());
        vsyncCheckbox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean enabled = vsyncCheckbox.isChecked();
                setVsyncEnabled(!enabled);
                Gdx.app.log(MultiMage.TITLE, "VSync " + (isVSyncEnabled() ? "enabled" : "disabled"));
                //Gdx.graphics.setFullscreenMode(new Graphics.DisplayMode(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 60, 100));
            }
        });

        // volumeMUSIC Slider
        final Slider volumeMusicSlider = new Slider( 0f, 1f, 0.1f,false, skinForSlidersAndCheckBox);
        volumeMusicSlider.setValue(getMusicVolume());
        setMusicVolume(getMusicVolume());
        volumeMusicSlider.addListener( new EventListener() {
            @Override
            public boolean handle(Event event) {
                setMusicVolume(volumeMusicSlider.getValue());
                game.music.setVolume(volumeMusicSlider.getValue());
                return false;
            }
        });

        // return to main screen button
        final TextButton backButton = new TextButton("BACK", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu(game));
            }
        });
        backButton.pad(15);

        Label heading = new Label("PREFERENCES", skin);
        Label volumeMusicLabel = new Label("music", skin);
        muteMusicString = isMusicEnabled() ? "mute music" : "unmute music";
        Label musicOnOffLabel = new Label(muteMusicString, skin);
        Label fullscreenOnOffLabel = new Label("fullscreen", skin);
        Label vsyncOnOffLabel = new Label("vsync", skin);

        heading.setFontScale(1f);

        table.add(heading);
        table.getCell(heading).spaceBottom(100);
        table.row();
        table.add(volumeMusicLabel);
        table.add(volumeMusicSlider);
        table.row();
        table.add(musicOnOffLabel);
        table.add(musicCheckbox);
        table.getCell(heading).spaceBottom(15);
        table.row();
        table.add(fullscreenOnOffLabel);
        table.add(fullscreenCheckbox);
        table.getCell(heading).spaceBottom(15);
        table.row();
        table.add(vsyncOnOffLabel);
        table.add(vsyncCheckbox);
        table.getCell(heading).spaceBottom(15);
        table.row();
        table.add(backButton);
        table.getCell(backButton).spaceTop(50);

        stage.addActor(table);
    }

    public static boolean isFullScreenEnabled() {
        return false;
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
        skinForSlidersAndCheckBox.dispose();
        atlasUiAtlas.dispose();
    }
}
