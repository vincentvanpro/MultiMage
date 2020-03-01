package com.multimage;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Preferences;
import com.multimage.screens.PreferencesMenu;
import com.multimage.screens.Splash;

public class MultiMageGame extends Game {
	public static final String TITLE = "MultiMage" , VERSION = "RAW";
	private PreferencesMenu preferences;

	@Override
	public void create() {
		setScreen(new Splash());
		preferences = new PreferencesMenu();
	}

	public PreferencesMenu getPreferences() {
		return this.preferences;
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}