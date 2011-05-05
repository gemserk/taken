package com.gemserk.games.taken;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.commons.gdx.ScreenAdapter;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class MenuScreen extends ScreenAdapter {

	private final LibgdxGame game;

	private ArrayList<Sprite> sprites;

	private SpriteBatch spriteBatch;

	private ResourceManager<String> resourceManager;

	public MenuScreen(LibgdxGame game) {
		this.game = game;
	}
	
	@Override
	public void show() {
		super.show();
		
		this.sprites = new ArrayList<Sprite>();
		this.spriteBatch = new SpriteBatch();

		resourceManager = new ResourceManagerImpl<String>();

		loadResources();

		Sprite b1 = resourceManager.getResourceValue("Background");
		b1.setPosition(0, 0);
		sprites.add(b1);

		Sprite b2 = resourceManager.getResourceValue("Background");
		b2.setPosition(b1.getWidth(), 0);
		sprites.add(b2);

	}

	@Override
	public void render(float delta) {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		BitmapFont titleFont = resourceManager.getResourceValue("TitleFont");
		
		spriteBatch.begin();
		
		for (int i = 0; i < sprites.size(); i++) {
			Sprite sprite = sprites.get(i);
			sprite.draw(spriteBatch);
		}
		
		drawCentered(titleFont, "CODENAME: T.A.K.E.N.", Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() - 30f);
		
		spriteBatch.end();
	}

	private void drawCentered(BitmapFont font, String text, float x, float y) {
		font.setScale(1f);
		TextBounds bounds = font.getBounds(text);
		font.draw(spriteBatch, text, x - bounds.width * 0.5f, y - bounds.height * 0.5f);
	}

	protected void onSplashScreenFinished() {
		game.setScreen(game.gameScreen, true);
	}

	private void loadResources() {
		new LibgdxResourceBuilder(resourceManager) {
			{
				texture("BackgroundTexture", "data/background-512x512.jpg");
				sprite("Background", "BackgroundTexture");		
				font("TitleFont", "data/fonts/title.png", "data/fonts/title.fnt");
			}
		};
	}

	@Override
	public void dispose() {
		resourceManager.unloadAll();
		spriteBatch.dispose();
		Gdx.app.log("Taken", "MenuScreen resources disposed");
	}

}