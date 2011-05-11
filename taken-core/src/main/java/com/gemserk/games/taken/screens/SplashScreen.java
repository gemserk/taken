package com.gemserk.games.taken.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.animation4j.event.AnimationEvent;
import com.gemserk.animation4j.event.AnimationEventHandler;
import com.gemserk.animation4j.event.AnimationHandlerManager;
import com.gemserk.animation4j.gdx.converters.LibgdxConverters;
import com.gemserk.animation4j.timeline.TimelineAnimationBuilder;
import com.gemserk.animation4j.timeline.TimelineValueBuilder;
import com.gemserk.animation4j.timeline.sync.ObjectSynchronizer;
import com.gemserk.animation4j.timeline.sync.ReflectionObjectSynchronizer;
import com.gemserk.animation4j.timeline.sync.SynchrnonizedAnimation;
import com.gemserk.animation4j.timeline.sync.TimelineSynchronizer;
import com.gemserk.commons.gdx.ScreenAdapter;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.games.taken.LibgdxGame;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class SplashScreen extends ScreenAdapter {
	
	private final LibgdxGame game;

	private SpriteBatch spriteBatch;

	private Color color = new Color(Color.BLACK);

	// used by refleciton animation synchronizer
	
	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	private SynchrnonizedAnimation splashAnimation;

	private AnimationHandlerManager animationHandlerManager;

	private final ArrayList<Sprite> sprites;

	private ResourceManager<String> resourceManager;

	public SplashScreen(LibgdxGame game) {

		this.game = game;
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		int centerX = width / 2;
		int centerY = height / 2;

		this.sprites = new ArrayList<Sprite>();
		this.spriteBatch = new SpriteBatch();

		ObjectSynchronizer objectSynchronizer = new ReflectionObjectSynchronizer();

		splashAnimation = new SynchrnonizedAnimation(new TimelineAnimationBuilder() {
			{
				delay(1000);
				value("color", new TimelineValueBuilder<Color>(LibgdxConverters.color()) {
					{
						keyFrame(0, new Color(1f, 1f, 1f, 0f));
						keyFrame(2000, new Color(1f, 1f, 1f, 1f));
						keyFrame(4000, new Color(1f, 1f, 1f, 1f));
						keyFrame(4250, new Color(1f, 1f, 1f, 0.7f));
						keyFrame(4500, new Color(0f, 0f, 0f, 0f));
					}
				});
				speed(1.3f);
			}
		}.build(), new TimelineSynchronizer(objectSynchronizer, this));

		animationHandlerManager = new AnimationHandlerManager();
		animationHandlerManager.with(new AnimationEventHandler() {
			@Override
			public void onAnimationFinished(AnimationEvent e) {
				onSplashScreenFinished();
			}
		}).handleChangesOf(splashAnimation);

		splashAnimation.start(1);

		//

		resourceManager = new ResourceManagerImpl<String>();

		loadResources();

		Sprite gemserkLogo = resourceManager.getResourceValue("GemserkLogo");
		Sprite lwjglLogo = resourceManager.getResourceValue("LwjglLogo");
		Sprite libgdxLogo = resourceManager.<Sprite> getResourceValue("LibgdxLogo");

		resize(gemserkLogo, width * 0.8f);
		resize(lwjglLogo, width * 0.3f);
		resize(libgdxLogo, width * 0.3f);

		centerOn(gemserkLogo, centerX, centerY);
		centerOn(lwjglLogo, width * 0.85f, lwjglLogo.getHeight() * 0.5f);
		centerOn(libgdxLogo, width * 0.15f, libgdxLogo.getHeight() * 0.5f);

		this.sprites.add(gemserkLogo);
		this.sprites.add(lwjglLogo);
		this.sprites.add(libgdxLogo);
		
	}

	// TODO: use the SpriteUtils of 0.0.2-SNAPSHOT version
	
	/**
	 * Resizes the sprite to the specified width maintaining the aspect ration.
	 */
	private void resize(Sprite sprite, float width) {
		float aspect = (float) sprite.getHeight() / (float) sprite.getWidth();
		float height = width * aspect;
		sprite.setSize(width, height);
	}
	
	private void centerOn(Sprite sprite, float x, float y) {
		sprite.setOrigin(sprite.getWidth() * 0.5f, sprite.getHeight() * 0.5f);
		sprite.setPosition(x - sprite.getOriginX(), y - sprite.getOriginY());
	}

	@Override
	public void render(float delta) {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		spriteBatch.begin();
		for (int i = 0; i < sprites.size(); i++) {
			Sprite sprite = sprites.get(i);
			sprite.setColor(color);
			sprite.draw(spriteBatch);
		}
		spriteBatch.end();
		splashAnimation.update(delta * 1000);
		animationHandlerManager.checkAnimationChanges();
	}

	protected void onSplashScreenFinished() {
		game.setScreen(game.menuScreen, true);
	}

	private void loadResources() {
		new LibgdxResourceBuilder(resourceManager) {
			{
				texture("GemserkLogoTexture", "data/images/logo-gemserk-512x128-white.png");
				texture("LwjglLogoTexture", "data/images/logo-lwjgl-512x256-inverted.png");
				texture("LibgdxLogoTexture", "data/images/logo-libgdx-clockwork-512x256.png");
				sprite("GemserkLogo", "GemserkLogoTexture");
				sprite("LwjglLogo", "LwjglLogoTexture", 0, 0, 512, 185);
				sprite("LibgdxLogo", "LibgdxLogoTexture", 0, 25, 512, 256 - 50);
			}
		};
	}

	@Override
	public void dispose() {
		resourceManager.unloadAll();
		spriteBatch.dispose();
		Gdx.app.log("Taken", "SplashScreen resources disposed");
	}

}