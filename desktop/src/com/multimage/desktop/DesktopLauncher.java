package com.multimage.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.multimage.MultiMageGame;


public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = MultiMageGame.TITLE + " v" + MultiMageGame.VERSION;
		config.vSyncEnabled = true;
		config.width = 1280;
		config.height = 720;

		new LwjglApplication(new MultiMageGame(), config);
	}
}
