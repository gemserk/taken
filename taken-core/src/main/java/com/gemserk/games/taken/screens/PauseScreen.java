package com.gemserk.games.taken.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.animation4j.transitions.Transition;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.animation4j.transitions.event.TransitionEventHandler;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.gdx.ScreenAdapter;
import com.gemserk.commons.gdx.gui.TextButton;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.games.taken.LibgdxGame;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class PauseScreen extends ScreenAdapter {

	private final LibgdxGame game;

	private ResourceManager<String> resourceManager;

	private SpriteBatch spriteBatch;

	private Sprite overlay;

	private Color overlayColor;

	private TextButton resumeButton;

	private TextButton menuButton;

	public PauseScreen(LibgdxGame game) {
		this.game = game;
	}

	@Override
	public void show() {
		super.show();

		this.spriteBatch = new SpriteBatch();

		resourceManager = new ResourceManagerImpl<String>();

		loadResources();

		overlay = resourceManager.getResourceValue("OverlaySprite");
		BitmapFont font = resourceManager.getResourceValue("PauseFont");

		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		resumeButton = new TextButton(font, "Resume", width * 0.5f, height * 0.5f + 40f);
		menuButton = new TextButton(font, "Menu", width * 0.5f, height * 0.5f - 40f);

		overlayColor = new Color();

		Synchronizers.transition(overlayColor, Transitions.transitionBuilder(new Color(0f, 0f, 0f, 0f)).end(new Color(0f, 0f, 0f, 0.3f)).time(500).build());

		Gdx.input.setCatchBackKey(true);
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

		game.gameScreen.internalRender(delta);

		if (spriteBatch == null)
			return;

		spriteBatch.begin();

		overlay.setSize(width, height);
		overlay.setPosition(0, 0);
		overlay.setColor(overlayColor);
		overlay.draw(spriteBatch);

		resumeButton.draw(spriteBatch);
		menuButton.draw(spriteBatch);

		spriteBatch.end();
	}

	@Override
	public void internalUpdate(float delta) {
		resumeButton.update();
		menuButton.update();

		Synchronizers.synchronize();

		if (resumeButton.isReleased() || Gdx.input.isKeyPressed(Keys.BACK)) {
			Synchronizers.transition(overlayColor, Transitions.transitionBuilder(overlayColor).end(new Color(0f, 0f, 0f, 0f)).time(300).build(), new TransitionEventHandler<Color>() {
				@Override
				public void onTransitionFinished(Transition<Color> transition) {
					game.setScreen(game.gameScreen, true);
				}
			});
		}

		if (menuButton.isReleased()) {
			System.out.println ("menu released");
			Synchronizers.transition(overlayColor, Transitions.transitionBuilder(overlayColor).end(new Color(0f, 0f, 0f, 1f)).time(300).build(), new TransitionEventHandler<Color>() {
				@Override
				public void onTransitionFinished(Transition<Color> transition) {
					game.setScreen(game.menuScreen, true);
					game.gameScreen.setGameOver(true);
					game.gameScreen.dispose();
				}
			});
		}
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
		spriteBatch = null;
		Gdx.app.log("Taken", "PauseScreen resources disposed");
	}

}