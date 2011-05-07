package com.gemserk.games.taken;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.animation4j.commons.values.converters.CommonConverters;
import com.gemserk.animation4j.converters.Converters;
import com.gemserk.animation4j.gdx.converters.LibgdxConverters;
import com.gemserk.commons.values.FloatValue;

public class LibgdxGame extends Game {

	public ScoreScreen scoreScreen;

	public GameScreen gameScreen;

	public SplashScreen splashScreen;

	public MenuScreen menuScreen;

	public PauseScreen pauseScreen;

	@Override
	public void create() {
		Converters.register(Vector2.class, LibgdxConverters.vector2());
		Converters.register(Color.class, LibgdxConverters.color());
		Converters.register(FloatValue.class, CommonConverters.floatValue());

		scoreScreen = new ScoreScreen(this);
		gameScreen = new GameScreen(this);
		splashScreen = new SplashScreen(this);
		menuScreen = new MenuScreen(this);
		pauseScreen = new PauseScreen(this);

		setScreen(splashScreen);
		// setScreen(menuScreen);
		// setScreen(gameScreen);
	}

	public void setScreen(Screen screen, boolean disposeCurrent) {
		Screen currentScreen = super.getScreen();
		setScreen(screen);
		if (disposeCurrent)
			currentScreen.dispose();
	}

}
