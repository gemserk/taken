package com.gemserk.games.taken;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.animation4j.commons.values.converters.CommonConverters;
import com.gemserk.animation4j.converters.Converters;
import com.gemserk.animation4j.gdx.converters.LibgdxConverters;
import com.gemserk.commons.gdx.SplashScreen;
import com.gemserk.commons.values.FloatValue;

public class LibgdxGame extends Game {
	
	public ScoreScreen scoreScreen;

	public GameScreen gameScreen;

	@Override
	public void create() {

		Converters.register(Vector2.class, LibgdxConverters.vector2());
		Converters.register(Color.class, LibgdxConverters.color());
		Converters.register(FloatValue.class, CommonConverters.floatValue());

		final Game game = this;

		final Texture gemserkLogo = new Texture(Gdx.files.internal("data/logo-gemserk-512x128-white.png"));
		gemserkLogo.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		scoreScreen = new ScoreScreen(this);
		gameScreen = new GameScreen(this);

		SplashScreen splashScreen = new SplashScreen(gemserkLogo) {

			@Override
			protected void onSplashScreenFinished() {
				game.setScreen(gameScreen);
			}

			@Override
			public void dispose() {
				gemserkLogo.dispose();
			}

		};
		
		setScreen(scoreScreen);

	}

}
