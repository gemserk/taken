package com.gemserk.games.taken;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.gdx.ScreenAdapter;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class PauseScreen extends ScreenAdapter {

	private final LibgdxGame game;

	private final Screen pausedScreen;

	private SpriteBatch spriteBatch;

	private ResourceManager<String> resourceManager;

	private Sprite overlay;

	private BitmapFont font;

	private InputAdapter inputProcessor;

	private boolean shouldReturnToGame;

	private Color overlayColor;

	public PauseScreen(LibgdxGame game, Screen pausedScreen) {
		this.game = game;
		this.pausedScreen = pausedScreen;
	}

	@Override
	public void show() {
		super.show();

		this.spriteBatch = new SpriteBatch();

		resourceManager = new ResourceManagerImpl<String>();

		loadResources();

		overlay = resourceManager.getResourceValue("OverlaySprite");
		font = resourceManager.getResourceValue("PauseFont");
		
		overlayColor = new Color();
		
		Synchronizers.transition(overlayColor, Transitions.transitionBuilder(new Color(0f, 0f, 0f, 0f))
				.end(new Color(0f,0f,0f,0.5f))
				.time(500)
				.build());

		shouldReturnToGame = false;
		Gdx.input.setCatchBackKey(true);
		inputProcessor = new InputAdapter() {
			public boolean keyTyped(char character) {
				shouldReturnToGame = true;
				return super.keyTyped(character);
			};

			@Override
			public boolean touchDown(int x, int y, int pointer, int button) {
				shouldReturnToGame = true;
				return super.touchDown(x, y, pointer, button);
			}
		};
		Gdx.input.setInputProcessor(inputProcessor);
	}

	@Override
	public void hide() {
		super.hide();
		Gdx.input.setCatchBackKey(false);
		Gdx.input.setInputProcessor(null);
	}

	public void internalRender(float delta) {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		pausedScreen.render(delta);
		spriteBatch.begin();

		overlay.setSize(width, height);
		overlay.setPosition(0, 0);
		overlay.setColor(overlayColor);
		overlay.draw(spriteBatch);
		
		drawCentered(font, "Paused", width * 0.5f, height * 0.5f);

		spriteBatch.end();
	}
	
	@Override
	public void internalUpdate(float delta) {
		Synchronizers.synchronize();
		if (shouldReturnToGame)
			game.setScreen(pausedScreen, true);
	}

	private void drawCentered(BitmapFont font, String text, float x, float y) {
		font.setScale(1f);
		TextBounds bounds = font.getBounds(text);
		font.draw(spriteBatch, text, x - bounds.width * 0.5f, y + bounds.height * 0.5f);
	}

	private void loadResources() {
		new LibgdxResourceBuilder(resourceManager) {
			{
				texture("OverlayTexture", "data/images/white-rectangle.png");
				sprite("OverlaySprite", "OverlayTexture");
				font("PauseFont", "data/fonts/title.png", "data/fonts/title.fnt");
			}
		};
	}

	@Override
	public void dispose() {
		resourceManager.unloadAll();
		spriteBatch.dispose();
		Gdx.app.log("Taken", "PauseScreen resources disposed");
	}

}