package com.multimage.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.audio.Mp3;
import com.multimage.MultiMage;


public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = MultiMage.TITLE + " v." + MultiMage.VERSION;
		config.vSyncEnabled = true;
		config.width = 1280;
		config.height = 720;

		new LwjglApplication(new MultiMage(), config);
	}
}
