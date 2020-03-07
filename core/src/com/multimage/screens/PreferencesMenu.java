package com.multimage.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
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
import com.multimage.MultiMageGame;


public class PreferencesMenu implements Screen {

    private static final String PREF_MUSIC_VOLUME = "volume";
    private static final String PREF_MUSIC_ENABLED = "music.enabled";
    private static final String PREF_SOUND_ENABLED = "sound.enabled";
    private static final String PREF_SOUND_VOL = "sound";
    private static final String PREFS_NAME = "com.multimage.settings";

    private Stage stage;
    private TextureAtlas atlas;


    private Skin skin;
    private Skin skinForSlidersAndCheckBox;
    private TextureAtlas atlasUiAtlas;

    protected Preferences getPrefs() {
        return Gdx.app.getPreferences(PREFS_NAME);
    }

    public boolean isSoundEffectsEnabled() {
        return getPrefs().getBoolean(PREF_SOUND_ENABLED, true);
    }

    public void setSoundEffectsEnabled(boolean soundEffectsEnabled) {
        getPrefs().putBoolean(PREF_SOUND_ENABLED, soundEffectsEnabled);
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

    public float getSoundVolume() {
        return getPrefs().getFloat(PREF_SOUND_VOL, 0.5f);
    }

    public void setSoundVolume(float volume) {
        getPrefs().putFloat(PREF_SOUND_VOL, volume);
        getPrefs().flush();
    }


    @Override
    public void show() {
        stage = new Stage();

        Gdx.input.setInputProcessor(stage);

        // font
        BitmapFont white = new BitmapFont(Gdx.files.internal("font/white32.fnt"), false);
        BitmapFont black = new BitmapFont(Gdx.files.internal("font/black32.fnt"), false);

        atlas = new TextureAtlas("ui/button.pack");
        skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), atlas);
        atlasUiAtlas = new TextureAtlas("ui/skin.atlas");
        skinForSlidersAndCheckBox = new Skin(atlasUiAtlas);
        skinForSlidersAndCheckBox.add("default-font", black);
        skinForSlidersAndCheckBox.load(Gdx.files.internal("ui/skin.json"));

        Table table = new Table(skin);
        table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        table.setFillParent(true);


        // musicBox
        final CheckBox musicCheckbox = new CheckBox("MUSIC", skinForSlidersAndCheckBox);
        musicCheckbox.setChecked(isMusicEnabled());
        setMusicEnabled(isMusicEnabled());
        musicCheckbox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(event.getListenerActor() == musicCheckbox) {
                    // set music
                    setMusicEnabled(!musicCheckbox.isChecked());
                    Gdx.app.log(MultiMageGame.TITLE, "Music " + (isMusicEnabled() ? "enabled" : "disabled"));
                }
            }
        });

        // SoundBox
        final CheckBox soundCheckbox = new CheckBox("SOUND", skinForSlidersAndCheckBox);
        soundCheckbox.setChecked(isSoundEffectsEnabled());
        setSoundEffectsEnabled(isSoundEffectsEnabled());
        soundCheckbox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(event.getListenerActor() == soundCheckbox) {
                    setSoundEffectsEnabled(!soundCheckbox.isChecked());
                    Gdx.app.log(MultiMageGame.TITLE, "Sound " + (isSoundEffectsEnabled() ? "enabled" : "disabled"));
                    // System.out.println(getPrefs().getString(PREF_SOUND_ENABLED));
                }
            }
        });

        // volumeMUSIC Slider
        final Slider volumeMusicSlider = new Slider( 0f, 1f, 0.1f,false, skinForSlidersAndCheckBox);
        volumeMusicSlider.setValue(100f);
        setMusicVolume(100f);
        volumeMusicSlider.addListener( new EventListener() {
            @Override
            public boolean handle(Event event) {
                setMusicVolume(volumeMusicSlider.getValue());
                // Gdx.app.log(MultiMageGame.TITLE, "Sound " + (getMusicVolume()));
                return false;
            }
        });

        // volumeSOUND Slider
        final Slider volumeSoundSlider = new Slider( 0f, 1f, 0.1f,false, skinForSlidersAndCheckBox);
        volumeSoundSlider.setValue(100f);
        setSoundVolume(100f);
        volumeSoundSlider.addListener( new EventListener() {
            @Override
            public boolean handle(Event event) {
                setSoundVolume(volumeSoundSlider.getValue());
                // Gdx.app.log(MultiMageGame.TITLE, "Sound " + (getSoundVolume()));
                return false;

            }
        });

        // return to main screen button
        final TextButton backButton = new TextButton("BACK", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
            }
        });
        backButton.pad(15);

        Label heading = new Label("PREFERENCES", skin);
        Label volumeMusicLabel = new Label("music", skin);
        Label volumeSoundLabel = new Label("sound", skin);
        Label musicOnOffLabel = new Label("on/off", skin);
        Label soundOnOffLabel = new Label("on/off", skin);

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
        table.add(volumeSoundLabel);
        table.add(volumeSoundSlider);
        table.row();
        table.add(soundOnOffLabel);
        table.add(soundCheckbox);
        table.getCell(heading).spaceBottom(15);
        table.row();
        table.add(backButton);
        table.getCell(backButton).spaceTop(50);

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
        skinForSlidersAndCheckBox.dispose();
        atlasUiAtlas.dispose();
    }
}
