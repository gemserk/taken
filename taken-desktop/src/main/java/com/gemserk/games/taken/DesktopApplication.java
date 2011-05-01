package com.gemserk.games.taken;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class DesktopApplication {
	public static void main (String[] argv) {
		new LwjglApplication(new LibgdxGame(), "Codename: RoboTaken", 480, 480, false);
	}
}
