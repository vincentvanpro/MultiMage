package com.multimage.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.multimage.MultiMage;


public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = MultiMage.TITLE + " v." + MultiMage.VERSION;
		config.vSyncEnabled = true;
		config.width = 1600;
		config.height = 900;

		new LwjglApplication(new MultiMage(), config);
	}
}
