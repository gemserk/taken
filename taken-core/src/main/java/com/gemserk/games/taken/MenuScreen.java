package com.gemserk.games.taken;

import java.util.ArrayList;

import org.w3c.dom.Document;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.animation4j.gdx.Animation;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.artemis.components.MovementComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.systems.MovementSystem;
import com.gemserk.commons.artemis.systems.RenderLayer;
import com.gemserk.commons.artemis.systems.SpriteRendererSystem;
import com.gemserk.commons.artemis.systems.SpriteUpdateSystem;
import com.gemserk.commons.gdx.ScreenAdapter;
import com.gemserk.commons.gdx.camera.Camera;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.gdx.graphics.SpriteBatchUtils;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.commons.svg.inkscape.SvgInkscapeImage;
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class MenuScreen extends ScreenAdapter {

	private final LibgdxGame game;

	private ArrayList<Sprite> sprites;

	private SpriteBatch spriteBatch;

	private ResourceManager<String> resourceManager;

	private Libgdx2dCameraTransformImpl worldCamera = new Libgdx2dCameraTransformImpl();

	private Libgdx2dCamera backgroundLayerCamera = new Libgdx2dCameraTransformImpl();

	private Camera cameraData = new Camera(0, 0, 1f, 0f);

	private WorldWrapper worldWrapper;

	private World world;

	private Sprite overlay;

	private Color overlayColor;
	
	private boolean shouldExit = false;

	public MenuScreen(LibgdxGame game) {
		this.game = game;
		int viewportWidth = Gdx.graphics.getWidth();
		int viewportHeight = Gdx.graphics.getHeight();
		worldCamera.center(viewportWidth * 0.3f, viewportHeight * 0.3f);
	}

	@Override
	public void show() {
		super.show();
		
		Gdx.input.setCatchBackKey(false);

		this.sprites = new ArrayList<Sprite>();
		this.spriteBatch = new SpriteBatch();

		resourceManager = new ResourceManagerImpl<String>();

		loadResources();
		
		Sprite b1 = resourceManager.getResourceValue("BackgroundSprite");
		b1.setPosition(0, 0);
		sprites.add(b1);

		Sprite b2 = resourceManager.getResourceValue("BackgroundSprite");
		b2.setPosition(b1.getWidth(), 0);
		sprites.add(b2);

		// creates the scene

		world = new World();
		worldWrapper = new WorldWrapper(world);

		ArrayList<RenderLayer> renderLayers = new ArrayList<RenderLayer>();

		renderLayers.add(new RenderLayer(-1000, -100, backgroundLayerCamera));
		renderLayers.add(new RenderLayer(-100, 100, worldCamera));

		worldWrapper.addUpdateSystem(new FollowCharacterBehaviorSystem());
		worldWrapper.addUpdateSystem(new MovementSystem());
		worldWrapper.addUpdateSystem(new AnimationSystem());
		worldWrapper.addUpdateSystem(new CameraFollowSystem());
		worldWrapper.addUpdateSystem(new SpriteUpdateSystem());
		
		worldWrapper.addRenderSystem(new SpriteRendererSystem(renderLayers));

		worldWrapper.init();

		createBackground();

		Document scene = resourceManager.getResourceValue("MenuScene");
		new WorldLoader("World") {

			@Override
			protected void handleCharacterStartPoint(float x, float y) {
				createMainCharacter(x, y);
			}

			@Override
			protected void handleRobotStartPoint(float x, float y) {
				createRobo(x, y);
			}

			protected void handleStaticObject(SvgInkscapeImage svgImage, float width, float height, float angle, float sx, float sy, float x, float y) {
				Texture texture = resourceManager.getResourceValue(svgImage.getLabel());
				if (texture == null)
					return;
				Sprite sprite = new Sprite(texture);
				sprite.setScale(sx, sy);
				createStaticSprite(sprite, x, y, width, height, angle, 0, 0.5f, 0.5f, Color.WHITE);
			};

		}.processWorld(scene);
		
		overlay = resourceManager.getResourceValue("OverlaySprite");

		overlayColor = new Color(0f, 0f, 0f, 1f);
		Synchronizers.transition(overlayColor, Transitions.transitionBuilder(new Color(0f, 0f, 0f, 1f)).end(new Color(0f, 0f, 0f, 0f)).time(500).build());
	}

	void createBackground() {
		Resource<Texture> background = resourceManager.get("Background");
		createStaticSprite(new Sprite(background.get()), 0f, 0f, 512, 512, 0f, -103, 0f, 0f, Color.WHITE);
		createStaticSprite(new Sprite(background.get()), 512, 0f, 512, 512, 0f, -103, 0f, 0f, Color.WHITE);
	}

	void createStaticSprite(Sprite sprite, float x, float y, float width, float height, float angle, int layer, float centerx, float centery, Color color) {
		Entity entity = world.createEntity();
		entity.addComponent(new SpatialComponent(new Vector2(x, y), new Vector2(width, height), angle));
		entity.addComponent(new SpriteComponent(sprite, layer, new Vector2(centerx, centery), new Color(color)));
		entity.refresh();
	}

	void createMainCharacter(float x, float y) {
		Animation idleAnimation = resourceManager.getResourceValue("Human_Idle");
		Animation[] animations = new Animation[] { idleAnimation };

		Entity entity = world.createEntity();

		entity.setTag("MainCharacter");
		entity.setGroup("Player");

		entity.addComponent(new SpatialComponent(new Vector2(x, y), new Vector2(1f, 1f), 0f));
		entity.addComponent(new SpriteComponent(new Sprite(idleAnimation.getFrame(0)), //
				1, new Vector2(0.5f, 0.5f), new Color(Color.WHITE)));
		entity.addComponent(new AnimationComponent(animations));
		entity.addComponent(new CameraFollowComponent(cameraData));

		entity.refresh();
	}

	void createRobo(float x, float y) {
		Animation idleAnimation = resourceManager.getResourceValue("Robo");
		Animation[] animations = new Animation[] { idleAnimation };

		Entity entity = world.createEntity();
		entity.setTag("Robo");

		entity.addComponent(new SpatialComponent(new Vector2(x, y), new Vector2(1, 1), 0f));
		entity.addComponent(new MovementComponent(new Vector2(), 0f));
		entity.addComponent(new SpriteComponent(idleAnimation.getFrame(0), //
				2, new Vector2(0.5f, 0.5f), new Color(Color.WHITE)));
		entity.addComponent(new FollowCharacterComponent(new Vector2(x, y), 0f));
		entity.addComponent(new AnimationComponent(animations));

		entity.refresh();
	}

	@Override
	public void internalRender(float delta) {

		if (spriteBatch == null)
			return;
		
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// RenderSystem sprite batch is not being disposed.

		worldCamera.zoom(cameraData.getZoom() * 2f);
		worldCamera.move(cameraData.getX(), cameraData.getY());
		worldCamera.rotate(cameraData.getAngle());

		worldWrapper.render();

		// draw the HUD
		BitmapFont titleFont = resourceManager.getResourceValue("TitleFont");
		
		spriteBatch.begin();
		
		SpriteBatchUtils.drawCentered(spriteBatch, titleFont, "CODENAME: T.A.K.E.N.", Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() - 30f);
		SpriteBatchUtils.drawCentered(spriteBatch, titleFont, "tap screen to start", Gdx.graphics.getWidth() * 0.5f, 80f);
		
		overlay.setSize(width, height);
		overlay.setPosition(0, 0);
		overlay.setColor(overlayColor);
		overlay.draw(spriteBatch);
		
		spriteBatch.end();
		
	}
	
	@Override
	public void internalUpdate(float delta) {
		Synchronizers.synchronize();
		worldWrapper.update((int) (delta * 1000f));
		
		if (overlayColor.a >= 1f) {
			if (shouldExit)
				System.exit(0);
			game.setScreen(game.gameScreen, true);
		}

		if (Gdx.input.justTouched()) {
			Synchronizers.transition(overlayColor, Transitions.transitionBuilder(overlayColor).end(new Color(0f, 0f, 0f, 1f)).time(300).build());
		}
		
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			Synchronizers.transition(overlayColor, Transitions.transitionBuilder(overlayColor).end(new Color(0f, 0f, 0f, 1f)).time(300).build());
			shouldExit = true;
		}
	}

	private void loadResources() {
		new LibgdxResourceBuilder(resourceManager) {
			{
				setCacheWhenLoad(true);

				sprite("BackgroundSprite", "Background");
				font("TitleFont", "data/fonts/title.png", "data/fonts/title.fnt");

				texture("Background", "data/images/background-512x512.jpg");

				texture("Tile01", "data/images/tile01.png");
				texture("Tile02", "data/images/tile02.png");
				texture("Tile03", "data/images/tile03.png");
				texture("Tile04", "data/images/tile04.png");
				texture("Tile05", "data/images/tile05.png");
				texture("Tile06", "data/images/tile06.png");
				texture("Tile07", "data/images/tile07.png");
				texture("Tile08", "data/images/tile08.png");
				texture("Tile09", "data/images/tile09.png");

				texture("CharactersSpriteSheet", "data/images/spritesheet.png", false);

				animation("Human_Idle", "CharactersSpriteSheet", 0, 0, 32, 32, 2, true, 1000, 50);
				animation("Robo", "CharactersSpriteSheet", 4 * 32, 4 * 32, 32, 32, 1, false, 0);

				resource("MenuScene", xmlDocument("data/scenes/menu-scene.svg")//
						.cached() //
						.fileType(FileType.Internal));

				texture("OverlayTexture", "data/images/white-rectangle.png");
				sprite("OverlaySprite", "OverlayTexture");
			}
		};
	}

	@Override
	public void dispose() {
		resourceManager.unloadAll();
		spriteBatch.dispose();
		spriteBatch = null;
		Gdx.app.log("Taken", "MenuScreen resources disposed");
	}

}