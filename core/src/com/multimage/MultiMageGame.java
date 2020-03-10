package com.multimage;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.multimage.screens.PlayScreen;
import com.multimage.screens.Splash;

public class MultiMageGame extends Game {
	public static final String TITLE = "MultiMage" , VERSION = "RAW";

	public static final int V_WIDTH = 400;
	public static final int V_HEIGHT = 208;

	public SpriteBatch batch;


	@Override
	public void create() {
		batch = new SpriteBatch();
		setScreen(new Splash(this));
		// setScreen(new PlayScreen(this));
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