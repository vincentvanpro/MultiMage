package com.multimage;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.multimage.screens.Splash;

public class MultiMageGame extends Game {
	public static final String TITLE = "MultiMage" , VERSION = "RAW";

	@Override
	public void create() {
		Gdx.app.log(TITLE, "create()");
		setScreen(new Splash());
	}

	@Override
	public void resize(int width, int height) {
		Gdx.app.log(TITLE, "resize()");
		super.resize(width, height);
	}

	@Override
	public void render() {
		Gdx.app.log(TITLE, "render()");
		super.render();
	}

	@Override
	public void pause() {
		Gdx.app.log(TITLE, "pause()");
		super.pause();
	}

	@Override
	public void resume() {
		Gdx.app.log(TITLE, "resume()");
		super.resume();
	}

	@Override
	public void dispose() {
		Gdx.app.log(TITLE, "dispose()");
		super.dispose();
	}
}