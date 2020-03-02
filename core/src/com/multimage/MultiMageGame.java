package com.multimage;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Preferences;
import com.multimage.screens.Splash;

public class MultiMageGame extends Game {
	public static final String TITLE = "MultiMage" , VERSION = "RAW";
	private Preferences preferences;

	@Override
	public void create() {
		setScreen(new Splash());
	}

	public Preferences getPreferences() {
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