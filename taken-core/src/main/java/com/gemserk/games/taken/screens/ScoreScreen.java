package com.gemserk.games.taken.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.animation4j.transitions.Transition;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.animation4j.transitions.event.TransitionEventHandler;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.gdx.ScreenAdapter;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.games.taken.LibgdxGame;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class ScoreScreen extends ScreenAdapter {

	private final LibgdxGame game;

	private final ResourceManager<String> resourceManager;

	private final InputDevicesMonitorImpl<String> inputDevicesMonitor;

	private static final Color endColor = new Color(1f, 1f, 1f, 0f);

	private static final Color startColor = new Color(1f, 1f, 1f, 1f);

	// private boolean fadingOut = false;

	private Sprite backgroundSprite;

	private SpriteBatch spriteBatch;

	private int score = 0;

	private Color fadeInColor;

	private boolean inputEnabled;

	public void setScore(int score) {
		this.score = score;
	}

	public ScoreScreen(LibgdxGame game) {
		this.game = game;
		resourceManager = new ResourceManagerImpl<String>();
		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorPointerDown("continue", 0);
			}
		};
	}

	@Override
	public void show() {
		spriteBatch = new SpriteBatch();
		loadResources();
		fadeInColor = new Color(1f, 1f, 1f, 1f);

		backgroundSprite = resourceManager.getResourceValue("Background");

		Synchronizers.transition(fadeInColor, Transitions.transitionBuilder(startColor).end(endColor).time(2000).build(), new TransitionEventHandler<Color>() {
			@Override
			public void onTransitionFinished(Transition<Color> transition) {
			}
		});
		// fadingOut = false;

		inputEnabled = true;

		Gdx.input.setCatchBackKey(true);
	}

	@Override
	public void hide() {
		Gdx.input.setCatchBackKey(false);
	}

	@Override
	public void internalRender(float delta) {
		Synchronizers.synchronize();
		
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);

		if (spriteBatch == null)
			return;

		BitmapFont bitmapFont = resourceManager.getResourceValue("TextFont");

		spriteBatch.begin();

		backgroundSprite.setColor(Color.WHITE);

		backgroundSprite.setPosition(0, 0);
		backgroundSprite.draw(spriteBatch);

		backgroundSprite.setPosition(backgroundSprite.getWidth(), 0);
		backgroundSprite.draw(spriteBatch);

		spriteBatch.setColor(Color.WHITE);

		String gameOverString = "GAME OVER";

		TextBounds textBounds = bitmapFont.getBounds(gameOverString);
		bitmapFont.draw(spriteBatch, gameOverString, width * 0.5f - textBounds.width * 0.5f, height * 0.5f + textBounds.height * 0.5f + textBounds.height);

		String pointsString = "score: " + score;

		textBounds = bitmapFont.getBounds(pointsString);
		bitmapFont.draw(spriteBatch, pointsString, width * 0.5f - textBounds.width * 0.5f, height * 0.5f + textBounds.height * 0.5f - textBounds.height);

		spriteBatch.setColor(fadeInColor);

		backgroundSprite.setColor(fadeInColor);

		backgroundSprite.setPosition(0, 0);
		backgroundSprite.draw(spriteBatch);

		backgroundSprite.setPosition(backgroundSprite.getWidth(), 0);
		backgroundSprite.draw(spriteBatch);

		spriteBatch.end();
	}

	@Override
	public void internalUpdate(float delta) {
		
		if (!inputEnabled)
			return;

		// if (inputEnabled)
		inputDevicesMonitor.update();

		if (inputDevicesMonitor.getButton("continue").isPressed()) {
			inputEnabled = false;
			Synchronizers.transition(fadeInColor, Transitions.transitionBuilder(endColor).end(startColor).time(500).build(), new TransitionEventHandler<Color>() {
				@Override
				public void onTransitionFinished(Transition<Color> transition) {
					game.setScreen(game.gameScreen);
					dispose();
				}
			});
		}

	}

	protected void loadResources() {

		new LibgdxResourceBuilder(resourceManager) {
			{
				texture("BackgroundTexture", "data/images/background-512x512.jpg");
				sprite("Background", "BackgroundTexture");
				font("TextFont", "data/fonts/text.png", "data/fonts/text.fnt");
			}

		};

	}

	@Override
	public void dispose() {
		resourceManager.unloadAll();
		spriteBatch.dispose();
		spriteBatch = null;
	}

}
