package com.gemserk.games.taken;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.commons.gdx.ScreenAdapter;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class PauseScreen extends ScreenAdapter {

	private final LibgdxGame game;

	private final Screen pausedScreen;

	private SpriteBatch spriteBatch;

	private ResourceManager<String> resourceManager;

	private Sprite background;

	private BitmapFont font;

	private InputAdapter inputProcessor;

	private boolean shouldReturnToGame;

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

		background = resourceManager.getResourceValue("BackgroundSprite");
		font = resourceManager.getResourceValue("PauseFont");

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

	@Override
	public void render(float delta) {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		internalRender(delta);
		update(delta);
	}

	public void internalRender(float delta) {
		pausedScreen.render(delta);
		spriteBatch.begin();

		background.setPosition(0, 0);
		background.setColor(1f, 1f, 1f, 0.5f);
		background.draw(spriteBatch);

		background.setPosition(background.getWidth(), 0);
		background.setColor(1f, 1f, 1f, 0.5f);
		background.draw(spriteBatch);

		drawCentered(font, "Paused", Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f);

		spriteBatch.end();
	}

	private void update(float delta) {
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
				texture("Background", "data/images/background-512x512.jpg");
				sprite("BackgroundSprite", "Background");
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