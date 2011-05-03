package com.gemserk.games.taken;

import java.util.ArrayList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.animation4j.commons.values.converters.CommonConverters;
import com.gemserk.animation4j.converters.Converters;
import com.gemserk.animation4j.gdx.converters.LibgdxConverters;
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

		scoreScreen = new ScoreScreen(this);
		gameScreen = new GameScreen(this);

		final Texture gemserkLogo = new Texture(Gdx.files.internal("data/logo-gemserk-512x128-white.png"));
		gemserkLogo.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		final Texture lwjglLogo = new Texture(Gdx.files.internal("data/logo-lwjgl-512x256-inverted.png"));
		lwjglLogo.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		final Texture libgdxLogo = new Texture(Gdx.files.internal("data/logo-libgdx-clockwork-512x256.png"));
		libgdxLogo.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		
		int centerX = width / 2;
		int centerY = height / 2;
		
		Sprite gemserkLogoSprite = new Sprite(gemserkLogo);

		float aspect = (float) gemserkLogoSprite.getWidth() / (float) gemserkLogoSprite.getHeight();
		float newWidth = width - (width / 5); // - 20%
		float newHeight = newWidth / aspect;

		gemserkLogoSprite.setPosition(centerX - newWidth / 2, centerY - newHeight / 2);
		gemserkLogoSprite.setSize(newWidth, newHeight);
		
		// if plataforma pc
		
		Sprite lwjglLogoSprite = new Sprite(lwjglLogo, 0, 0, 512, 185);
		
		aspect = (float) lwjglLogoSprite.getWidth() / (float) lwjglLogoSprite.getHeight();
		newWidth = width * 0.4f; // - 50%
		newHeight = newWidth / aspect;
		
		lwjglLogoSprite.setPosition(width - newWidth, 10);
		lwjglLogoSprite.setSize(newWidth, newHeight);
		
		Sprite libgdxLogoSprite = new Sprite(libgdxLogo);
		
		aspect = (float) libgdxLogoSprite.getWidth() / (float) libgdxLogoSprite.getHeight();
		newWidth = width * 0.4f; // - 50%
		newHeight = newWidth / aspect;
		
		libgdxLogoSprite.setPosition(0, 10);
		libgdxLogoSprite.setSize(newWidth, newHeight);

		ArrayList<Sprite> sprites = new ArrayList<Sprite>();
		sprites.add(gemserkLogoSprite);
		sprites.add(lwjglLogoSprite);
		sprites.add(libgdxLogoSprite);

		SplashScreen splashScreen = new SplashScreen(sprites) {

			@Override
			protected void onSplashScreenFinished() {
				game.setScreen(gameScreen);
			}

			@Override
			public void dispose() {
				gemserkLogo.dispose();
				lwjglLogo.dispose();
				libgdxLogo.dispose();
			}

		};
		
		setScreen(gameScreen);

	}

}
