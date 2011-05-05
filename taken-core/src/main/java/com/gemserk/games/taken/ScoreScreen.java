package com.gemserk.games.taken;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.gdx.ScreenAdapter;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.commons.gdx.resources.dataloaders.BitmapFontDataLoader;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;
import com.gemserk.resources.resourceloaders.CachedResourceLoader;
import com.gemserk.resources.resourceloaders.ResourceLoaderImpl;

public class ScoreScreen extends ScreenAdapter {

	private final LibgdxGame game;

	private final ResourceManager<String> resourceManager;

	private final InputDevicesMonitorImpl<String> inputDevicesMonitor;

	private final LibgdxResourceBuilder libgdxResourceBuilder;

	private static final Color endColor = new Color(1f, 1f, 1f, 0f);

	private static final Color startColor = new Color(1f, 1f, 1f, 1f);

	private boolean fadingOut = false;

	private Sprite backgroundSprite;

	private SpriteBatch spriteBatch;

	private int score = 0;

	private Color fadeInColor;

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
		libgdxResourceBuilder = new LibgdxResourceBuilder(resourceManager);
	}

	@Override
	public void show() {
		spriteBatch = new SpriteBatch();
		loadResources();
		fadeInColor = new Color(1f, 1f, 1f, 1f);

		backgroundSprite = resourceManager.getResourceValue("Background");
		// backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		Synchronizers.transition(fadeInColor, Transitions.transitionBuilder(startColor).end(endColor).time(2000).build());
		fadingOut = false;
	}

	@Override
	public void render(float delta) {
		Synchronizers.synchronize();

		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);

		BitmapFont bitmapFont = resourceManager.getResourceValue("Font");

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

		inputDevicesMonitor.update();

		if (inputDevicesMonitor.getButton("continue").isPressed() && !fadingOut) {
			Synchronizers.transition(fadeInColor, Transitions.transitionBuilder(endColor).end(startColor).time(500).build());
			fadingOut = true;
		}

		if (fadeInColor.equals(startColor) && fadingOut) {
			game.setScreen(game.gameScreen, true);
		}
	}

	protected void loadResources() {

		{
			libgdxResourceBuilder.texture("BackgroundTexture", "data/background-512x512.jpg");
			libgdxResourceBuilder.texture("FontTexture", "data/font.png");
			libgdxResourceBuilder.sprite("Background", "BackgroundTexture");
		}

		Texture fontTexture = resourceManager.getResourceValue("FontTexture");
		resourceManager.add("Font", new CachedResourceLoader<BitmapFont>(new ResourceLoaderImpl<BitmapFont>( //
				new BitmapFontDataLoader(Gdx.files.internal("data/font.fnt"), new Sprite(fontTexture)))));
	}

	@Override
	public void dispose() {
		resourceManager.unloadAll();
		spriteBatch.dispose();
	}

}
