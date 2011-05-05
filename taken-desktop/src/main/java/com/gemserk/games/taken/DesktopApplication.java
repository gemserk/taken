package com.gemserk.games.taken;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class DesktopApplication {
	public static void main (String[] argv) {
		new LwjglApplication(new LibgdxGame(), "Codename: Taken", 800, 480, false);
	}
}
