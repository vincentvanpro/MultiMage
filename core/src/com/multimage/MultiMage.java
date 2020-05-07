package com.multimage;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.multimage.screens.*;

public class MultiMage extends Game {
	public static final String TITLE = "MultiMage" , VERSION = "RAW";

	public static final int V_WIDTH = 864;
	public static final int V_HEIGHT = 485;
	public static final float PPM = 100;  // PIXELS PER METER / SCALE

	public static final short OBJECT_BIT = 1;
	public static final short MAGE_BIT = 2;
	public static final short GROUND_BIT  = 4;
	public static final short CHEST_BIT = 8;
	public static final short BONUS_BIT = 16;
	public static final short LEVERS_BIT = 32;
	public static final short OPENABLE_DOOR_BIT = 64;
	public static final short DESTROYED_BIT = 128;
	public static final short ITEM_BIT = 256;
	public static final short ENEMY_BIT = 512;
	public static final short ENEMY_BODY_BIT = 1024;
	public static final short PLATFORM_BIT = 2048;
	public static final short MAGE_HAND_BIT = 4096;
	public static final short PORTAL_BIT = 8192;
	public static final short FIREBALL_BIT = 16384;

	public SpriteBatch batch;

	public static AssetManager manager;
	public static Music music;

	@Override
	public void create() {
		batch = new SpriteBatch();
		manager = new AssetManager();
		manager.load("audio/music/main_menu_music.ogg", Music.class);
		manager.load("audio/music/first_level_music.ogg", Music.class);
		manager.load("audio/music/second_level_music.ogg", Music.class);
		manager.load("audio/music/third_level_music.ogg", Music.class);
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
		setScreen(new Splash(this));
		//setScreen(new MainMenu(this));
		//setScreen(new PlayScreen(this));
		//setScreen(new PreferencesMenu(this));
		setScreen(new MultiPlayer(this));
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