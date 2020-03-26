package com.multimage;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.multimage.screens.PlayScreen;
import com.multimage.screens.Splash;

public class MultiMage extends Game {
	public static final String TITLE = "MultiMage" , VERSION = "RAW";

	public static final int V_WIDTH = 400;
	public static final int V_HEIGHT = 208;
	public static final float PPM = 100;  // PIXELS PER METER / SCALE

	public static final short DEFAULT_BIT = 1;
	public static final short MAGE_BIT = 2;
	public static final short GROUND_BIT  = 4;
	public static final short CHEST_BIT = 8;
	public static final short DESTROYED_BIT = 16;

	public SpriteBatch batch;
	public Music music;

	public static AssetManager manager;

	@Override
	public void create() {
		batch = new SpriteBatch();
		manager = new AssetManager();
		manager.load("audio/music/main_menu_music.ogg", Music.class);
		manager.finishLoading();
		music = MultiMage.manager.get("audio/music/main_menu_music.ogg", Music.class);
		music.setLooping(true);
		music.setVolume(Gdx.app.getPreferences("com.multimage.settings")
				.getFloat("volume", 0.5f));

		if (Gdx.app.getPreferences("com.multimage.settings")
				.getBoolean("music.enabled", true)) {
			music.play();
		} else {
			music.pause();
		}
		//setScreen(new Splash(this));
		setScreen(new PlayScreen(this));
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void render() {
		super.render();
	}

}