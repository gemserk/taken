package com.gemserk.games.taken;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
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
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;
import com.gemserk.resources.dataloaders.DataLoader;
import com.gemserk.resources.resourceloaders.ResourceLoaderImpl;

public class SplashScreen extends ScreenAdapter {
	
	private final LibgdxGame game;

	private SpriteBatch spriteBatch;

	private Color color = Color.BLACK;

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

		locate(resourceManager.<Sprite> getResourceValue("GemserkLogo"), centerX, centerY, width * 0.8f);
		locate(resourceManager.<Sprite> getResourceValue("LwjglLogo"), width * 0.85f, 30, width * 0.3f);
		locate(resourceManager.<Sprite> getResourceValue("LibgdxLogo"), width * 0.15f, 30, width * 0.3f);

	}

	private void locate(Sprite sprite, float x, float y, float width) {
		float aspect = (float) sprite.getHeight() / (float) sprite.getWidth();
		float height = width * aspect;

		sprite.setSize(width, height);

		sprite.setOrigin(sprite.getWidth() * 0.5f, sprite.getHeight() * 0.5f);
		sprite.setPosition(x - sprite.getOriginX(), y - sprite.getOriginY());

		this.sprites.add(sprite);
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
		game.setScreen(game.gameScreen, true);
	}

	private void loadResources() {
		new LibgdxResourceBuilder(resourceManager) {
			{
				texture("GemserkLogoTexture", "data/logo-gemserk-512x128-white.png");
				texture("LwjglLogoTexture", "data/logo-lwjgl-512x256-inverted.png");
				texture("LibgdxLogoTexture", "data/logo-libgdx-clockwork-512x256.png");

				sprite("GemserkLogo", "GemserkLogoTexture");
				sprite("LwjglLogo", "LwjglLogoTexture", 0, 0, 512, 185);
				sprite("LibgdxLogo", "LibgdxLogoTexture", 0, 25, 512, 256 - 50);
			}

			private void sprite(String id, String textureId) {
				final Resource<Texture> texture = resourceManager.get(textureId);
				resourceManager.add(id, new ResourceLoaderImpl<Sprite>(new DataLoader<Sprite>() {
					@Override
					public Sprite load() {
						return new Sprite(texture.get());
					}
				}));
			}

			private void sprite(String id, String textureId, final int x, final int y, final int width, final int height) {
				final Resource<Texture> texture = resourceManager.get(textureId);
				resourceManager.add(id, new ResourceLoaderImpl<Sprite>(new DataLoader<Sprite>() {
					@Override
					public Sprite load() {
						return new Sprite(texture.get(), x, y, width, height);
					}
				}));
			}

		};
	}

	@Override
	public void dispose() {
		resourceManager.unloadAll();
		Gdx.app.log("Taken", "SplashScreen resources disposed");
		this.spriteBatch.dispose();
	}

}