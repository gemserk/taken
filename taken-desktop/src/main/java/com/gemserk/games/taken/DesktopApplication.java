package com.gemserk.games.taken;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.gemserk.games.taken.LibgdxGame;

public class DesktopApplication {
	public static void main (String[] argv) {
		new LwjglApplication(new LibgdxGame(), "Floating Islands", 800, 480, false);
	}
}
