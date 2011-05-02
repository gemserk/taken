package com.gemserk.games.taken;

import java.util.ArrayList;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import org.w3c.dom.Element;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gemserk.animation4j.interpolator.function.InterpolationFunctions;
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
import com.gemserk.commons.gdx.box2d.Box2DCustomDebugRenderer;
import com.gemserk.commons.gdx.camera.Camera;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.gdx.controllers.Controller;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.commons.gdx.resources.dataloaders.BitmapFontDataLoader;
import com.gemserk.commons.svg.inkscape.SvgDocument;
import com.gemserk.commons.svg.inkscape.SvgDocumentHandler;
import com.gemserk.commons.svg.inkscape.SvgInkscapeGroup;
import com.gemserk.commons.svg.inkscape.SvgInkscapeGroupHandler;
import com.gemserk.commons.svg.inkscape.SvgInkscapeImage;
import com.gemserk.commons.svg.inkscape.SvgInkscapeImageHandler;
import com.gemserk.commons.svg.inkscape.SvgInkscapePath;
import com.gemserk.commons.svg.inkscape.SvgInkscapePathHandler;
import com.gemserk.commons.svg.inkscape.SvgParser;
import com.gemserk.componentsengine.input.ButtonMonitor;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxButtonMonitor;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.componentsengine.properties.PropertyBuilder;
import com.gemserk.componentsengine.utils.Container;
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;
import com.gemserk.resources.dataloaders.DataLoader;
import com.gemserk.resources.resourceloaders.CachedResourceLoader;
import com.gemserk.resources.resourceloaders.ResourceLoaderImpl;

public class GameScreen extends ScreenAdapter {

	static class KeyboardCharacterController implements CharacterController {

		private Vector2 direction = new Vector2(0f, 0f);

		private boolean walking = false;

		private boolean jumped = false;

		ButtonMonitor jumpButtonMonitor = new LibgdxButtonMonitor(Keys.DPAD_UP);

		ButtonMonitor shrinkGrowButtonMonitor = new LibgdxButtonMonitor(Keys.S);

		@Override
		public void update(int delta) {

			walking = false;
			jumped = false;

			direction.set(0f, 0f);

			if (Gdx.input.isKeyPressed(Keys.DPAD_RIGHT)) {
				direction.x = 1f;
				walking = true;
			} else if (Gdx.input.isKeyPressed(Keys.DPAD_LEFT)) {
				direction.x = -1f;
				walking = true;
			}

			jumpButtonMonitor.update();
			shrinkGrowButtonMonitor.update();

			jumped = jumpButtonMonitor.isPressed();

		}

		@Override
		public boolean isWalking() {
			return walking;
		}

		@Override
		public void getWalkingDirection(float[] d) {
			d[0] = direction.x;
			d[1] = direction.y;
		}

		@Override
		public boolean jumped() {
			return jumped;
		}

		@Override
		public boolean shouldSwitchSize() {
			return false;
			// return shrinkGrowButtonMonitor.isPressed();
		}
	}

	private LibgdxGame game;

	private WorldWrapper worldWrapper;

	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	private Libgdx2dCameraTransformImpl camera = new Libgdx2dCameraTransformImpl();

	private Libgdx2dCamera backgroundLayerCamera = new Libgdx2dCameraTransformImpl();

	private Libgdx2dCamera hudLayerCamera = new Libgdx2dCameraTransformImpl();

	private Box2DCustomDebugRenderer box2dCustomDebugRenderer;

	private com.badlogic.gdx.physics.box2d.World physicsWorld;

	private SvgDocument svgDocument;

	private PhysicsObjectsFactory physicsObjectsFactory;

	private int viewportWidth;

	private int viewportHeight;

	private Camera cameraData;

	private ResourceManager<String> resourceManager;

	private World world;

	private ArrayList<Controller> controllers = new ArrayList<Controller>();

	private Entity mainCharacter;

	private boolean gameOver = true;

	private float score;

	private SpriteBatch spriteBatch;

	private final Color laserEndColor = new Color(1f, 1f, 1f, 0f);

	private final Color laserStartColor = new Color(1f, 1f, 1f, 1f);

	public GameScreen(LibgdxGame game) {
		this.game = game;

		viewportWidth = Gdx.graphics.getWidth();
		viewportHeight = Gdx.graphics.getHeight();

		camera.center(viewportWidth / 2, viewportHeight / 2);
		camera.zoom(40f);

		float zoom = 40f;
		float invZoom = 1 / zoom;

		Vector2 cameraPosition = new Vector2(viewportWidth * 0.5f * invZoom, viewportHeight * 0.5f * invZoom);
		cameraData = new Camera(cameraPosition.x, cameraPosition.y, zoom, 0f);

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();

		resourceManager = new ResourceManagerImpl<String>();

		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorKey("debug", Keys.D);
				monitorKey("score", Keys.P);
			}
		};

		spriteBatch = new SpriteBatch();
	}

	void restartGame() {
		// create the scene...
		physicsWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0, -10f), false);

		physicsObjectsFactory = new PhysicsObjectsFactory(physicsWorld);

		box2dCustomDebugRenderer = new Box2DCustomDebugRenderer(camera, physicsWorld);

		world = new World();

		worldWrapper = new WorldWrapper(world);

		ArrayList<RenderLayer> renderLayers = new ArrayList<RenderLayer>();

		// background layer
		renderLayers.add(new RenderLayer(-1000, -100, backgroundLayerCamera));
		// world layer
		renderLayers.add(new RenderLayer(-100, 100, camera));
		// hud layer
		renderLayers.add(new RenderLayer(100, 1000, hudLayerCamera));

		worldWrapper.add(new CharacterControllerSystem(resourceManager));
		worldWrapper.add(new PhysicsSystem(physicsWorld));
		worldWrapper.add(new FollowCharacterBehaviorSystem());
		worldWrapper.add(new EnemyWeaponSystem(this, resourceManager));
		worldWrapper.add(new FriendlyWeaponSystem(this, resourceManager));
		worldWrapper.add(new MovementSystem());
		worldWrapper.add(new BulletSystem());
		worldWrapper.add(new HitDetectionSystem(resourceManager));
		worldWrapper.add(new AnimationSystem());
		worldWrapper.add(new SpriteUpdateSystem());
		worldWrapper.add(new SpriteRendererSystem(renderLayers));
		worldWrapper.add(new CameraFollowSystem());
		worldWrapper.add(new TimerSystem());

		worldWrapper.add(new EnemySpawnerSystem(this));

		worldWrapper.init();

		// load scene!!

		loadResources();

		createBackground();

		createMainCharacter();

		createRobo();

		createEnemySpawner();

		loadWorld();

		loadPhysicObjects();

		score = 0;
	}

	void loadWorld() {
		SvgParser svgParser = new SvgParser();
		svgParser.addHandler(new SvgDocumentHandler() {
			@Override
			protected void handle(SvgParser svgParser, SvgDocument svgDocument, Element element) {
				GameScreen.this.svgDocument = svgDocument;
			}
		});
		svgParser.addHandler(new SvgInkscapeGroupHandler() {
			@Override
			protected void handle(SvgParser svgParser, SvgInkscapeGroup svgInkscapeGroup, Element element) {

				if (svgInkscapeGroup.getGroupMode().equals("layer") && !svgInkscapeGroup.getLabel().equalsIgnoreCase("World")) {
					svgParser.processChildren(false);
					return;
				}

			}
		});
		svgParser.addHandler(new SvgInkscapeImageHandler() {

			private boolean isFlipped(Matrix3f matrix) {
				return matrix.getM00() != matrix.getM11();
			}

			@Override
			protected void handle(SvgParser svgParser, SvgInkscapeImage svgImage, Element element) {

				if (svgImage.getLabel() == null)
					return;

				Resource<Texture> texture = resourceManager.get(svgImage.getLabel());

				if (texture == null)
					return;

				float width = svgImage.getWidth();
				float height = svgImage.getHeight();

				Matrix3f transform = svgImage.getTransform();

				Vector3f position = new Vector3f(svgImage.getX() + width * 0.5f, svgImage.getY() + height * 0.5f, 0f);
				transform.transform(position);

				Vector3f direction = new Vector3f(1f, 0f, 0f);
				transform.transform(direction);

				float angle = 360f - (float) (Math.atan2(direction.y, direction.x) * 180 / Math.PI);

				float sx = 1f;
				float sy = 1f;

				if (isFlipped(transform)) {
					sy = -1f;
				}

				float x = position.x;
				float y = svgDocument.getHeight() - position.y;

				Sprite sprite = new Sprite(texture.get());
				sprite.setScale(sx, sy);

				createStaticSprite(sprite, x, y, width, height, angle, 0, 0.5f, 0.5f, Color.WHITE);

			}
		});
		svgParser.parse(Gdx.files.internal("data/scene01.svg").read());
	}

	void loadPhysicObjects() {
		SvgParser svgParser = new SvgParser();
		svgParser.addHandler(new SvgDocumentHandler() {
			@Override
			protected void handle(SvgParser svgParser, SvgDocument svgDocument, Element element) {
				GameScreen.this.svgDocument = svgDocument;
			}
		});
		svgParser.addHandler(new SvgInkscapeGroupHandler() {
			@Override
			protected void handle(SvgParser svgParser, SvgInkscapeGroup svgInkscapeGroup, Element element) {

				if (svgInkscapeGroup.getGroupMode().equals("layer") && !svgInkscapeGroup.getLabel().equalsIgnoreCase("Physics")) {
					svgParser.processChildren(false);
					return;
				}

			}
		});
		svgParser.addHandler(new SvgInkscapePathHandler() {
			@Override
			protected void handle(SvgParser svgParser, SvgInkscapePath svgPath, Element element) {

				// String collisionMaskValue = element.getAttribute("collisionMask");

				Vector2f[] points = svgPath.getPoints();
				Vector2[] vertices = new Vector2[points.length];

				for (int i = 0; i < points.length; i++) {
					Vector2f point = points[i];
					vertices[i] = new Vector2(point.x, svgDocument.getHeight() - point.y);
					// System.out.println(vertices[i]);
				}

				physicsObjectsFactory.createGround(new Vector2(), vertices);

			}
		});
		svgParser.parse(Gdx.files.internal("data/scene01.svg").read());
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

	void createMainCharacter() {
		Resource<Texture> resource = resourceManager.get("Human");
		Resource<SpriteSheet> walkingAnimationResource = resourceManager.get("Human_Walking");
		Resource<SpriteSheet> idleAnimationResource = resourceManager.get("Human_Idle");
		Resource<SpriteSheet> jumpAnimationResource = resourceManager.get("Human_Jump");
		Resource<SpriteSheet> fallAnimationResource = resourceManager.get("Human_Fall");

		Sprite sprite = new Sprite(resource.get());

		float x = 2f;
		float y = 2f;

		float size = 1f;

		float width = 0.15f;
		float height = 1f;

		// Body body = physicsObjectsFactory.createDynamicRectangle(x, y, width, height, true, 1f);
		// Body body = physicsObjectsFactory.createDynamicCircle(x, y, height * 0.5f, false, 1f);

		Vector2[] bodyShape = Box2dUtils.createRectangle(width, height);
		Body body = physicsObjectsFactory.createPolygonBody(x, y, bodyShape, true, 0.1f, 1f, 0.15f);

		mainCharacter = world.createEntity();

		body.setUserData(mainCharacter);

		PhysicsComponent physicsComponent = new PhysicsComponent(body);
		physicsComponent.setVertices(bodyShape);

		mainCharacter.setGroup("Player");

		mainCharacter.addComponent(physicsComponent);
		mainCharacter.addComponent(new SpatialComponent( //
				new Box2dPositionProperty(body), //
				PropertyBuilder.vector2(size, size), //
				new Box2dAngleProperty(body)));
		// PropertyBuilder.property(ValueBuilder.floatValue(0f))));
		// entity.addComponent(new SpatialComponent(new Vector2(0, 0), new Vector2(viewportWidth, viewportWidth), 0f));
		mainCharacter.addComponent(new SpriteComponent(sprite, 1, new Vector2(0.5f, 0.5f), new Color(Color.WHITE)));

		SpriteSheet[] spriteSheets = new SpriteSheet[] { walkingAnimationResource.get(), idleAnimationResource.get(), jumpAnimationResource.get(), fallAnimationResource.get(), };

		FrameAnimation[] animations = new FrameAnimation[] { new FrameAnimationImpl(150, 2, true), new FrameAnimationImpl(new int[] { 1000, 50 }, true), new FrameAnimationImpl(100, 1, false), new FrameAnimationImpl(new int[] { 400, 200 }, true), };

		mainCharacter.addComponent(new AnimationComponent(spriteSheets, animations));

		mainCharacter.addComponent(new CameraFollowComponent(cameraData));

		mainCharacter.addComponent(new HealthComponent(new Container(100f, 100f)));

		KeyboardCharacterController characterController = new KeyboardCharacterController();
		mainCharacter.addComponent(new CharacterControllerComponent(characterController));
		controllers.add(characterController);

		mainCharacter.refresh();
	}

	void createEnemySpawner() {
		Entity entity = world.createEntity();

		entity.addComponent(new SpawnerComponent(4000));

		entity.refresh();
	}

	void createRobo() {
		Resource<SpriteSheet> enemyAnimationResource = resourceManager.get("Robo");
		Sprite sprite = enemyAnimationResource.get().getFrame(0);

		float x = 3f;
		float y = 3f;

		float size = 1f;

		Entity entity = world.createEntity();

		entity.addComponent(new SpatialComponent(new Vector2(x, y), new Vector2(size, size), 0f));
		entity.addComponent(new MovementComponent(new Vector2(), 0f));
		entity.addComponent(new SpriteComponent(sprite, 2, new Vector2(0.5f, 0.5f), new Color(Color.WHITE)));
		entity.addComponent(new FollowCharacterComponent(new Vector2(x, y), 0f));

		entity.addComponent(new FriendlyWeaponComponent(500, 6f));

		SpriteSheet[] spriteSheets = new SpriteSheet[] { enemyAnimationResource.get(), };

		FrameAnimation[] animations = new FrameAnimation[] { new FrameAnimationImpl(150, 1, false), };

		entity.addComponent(new AnimationComponent(spriteSheets, animations));

		entity.refresh();
	}

	void createEnemy(float x, float y) {
		Resource<SpriteSheet> enemyAnimationResource = resourceManager.get("Enemy");
		Sprite sprite = enemyAnimationResource.get().getFrame(0);

		float size = 1f;

		Entity entity = world.createEntity();

		entity.setGroup("Enemy");

		entity.addComponent(new SpatialComponent(new Vector2(x, y), new Vector2(size, size), 0f));
		entity.addComponent(new MovementComponent(new Vector2(), 0f));
		entity.addComponent(new SpriteComponent(sprite, 2, new Vector2(0.5f, 0.5f), new Color(Color.WHITE)));
		entity.addComponent(new FollowCharacterComponent(new Vector2(x, y), 0f));
		entity.addComponent(new EnemyWeaponComponent(1500, 3f));

		entity.addComponent(new HealthComponent(new Container(20f, 20f)));

		SpriteSheet[] spriteSheets = new SpriteSheet[] { enemyAnimationResource.get(), };

		FrameAnimation[] animations = new FrameAnimation[] { new FrameAnimationImpl(150, 1, false), };

		entity.addComponent(new AnimationComponent(spriteSheets, animations));

		entity.refresh();
	}

	void createEnemyLaser(float x, float y, int time, float dx, float dy) {
		Resource<SpriteSheet> enemyAnimationResource = resourceManager.get("EnemyLaser");
		Sprite sprite = enemyAnimationResource.get().getFrame(0);

		float size = 1f;

		Entity entity = world.createEntity();

		entity.setGroup("Enemy");

		Color color = new Color();

		Synchronizers.transition(color, Transitions.transitionBuilder(laserStartColor).end(laserEndColor).time(time)//
				.functions(InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut()) //
				.build());

		entity.addComponent(new SpatialComponent(new Vector2(x, y), new Vector2(size, size), 0f));
		entity.addComponent(new MovementComponent(new Vector2(dx, dy), 0f));
		entity.addComponent(new SpriteComponent(sprite, 2, new Vector2(0.5f, 0.5f), color));
		entity.addComponent(new TimerComponent(time));
		entity.addComponent(new BulletComponent());

		entity.addComponent(new HitComponent("Player", 2f));

		entity.addComponent(new HealthComponent(new Container(1f, 1f)));

		SpriteSheet[] spriteSheets = new SpriteSheet[] { enemyAnimationResource.get(), };

		FrameAnimation[] animations = new FrameAnimation[] { new FrameAnimationImpl(150, 3, false), };

		entity.addComponent(new AnimationComponent(spriteSheets, animations));

		entity.refresh();
	}

	void createFriendlyLaser(float x, float y, int time, float dx, float dy) {
		Resource<SpriteSheet> enemyAnimationResource = resourceManager.get("FriendlyLaser");
		Sprite sprite = enemyAnimationResource.get().getFrame(0);

		float size = 1f;

		Entity entity = world.createEntity();

		entity.setGroup("Player");

		Color color = new Color();

		Synchronizers.transition(color, Transitions.transitionBuilder(laserStartColor).end(laserEndColor).time(time)//
				.functions(InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut()) //
				.build());

		entity.addComponent(new SpatialComponent(new Vector2(x, y), new Vector2(size, size), 0f));
		entity.addComponent(new MovementComponent(new Vector2(dx, dy), 0f));
		entity.addComponent(new SpriteComponent(sprite, 2, new Vector2(0.5f, 0.5f), color));
		entity.addComponent(new TimerComponent(time));

		entity.addComponent(new BulletComponent());

		entity.addComponent(new HitComponent("Enemy", 10f));

		entity.addComponent(new HealthComponent(new Container(2f, 2f)));

		SpriteSheet[] spriteSheets = new SpriteSheet[] { enemyAnimationResource.get(), };

		FrameAnimation[] animations = new FrameAnimation[] { new FrameAnimationImpl(150, 3, false), };

		entity.addComponent(new AnimationComponent(spriteSheets, animations));

		entity.refresh();
	}

	@Override
	public void render(float delta) {

		// if (gameOver)
		// return;

		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);

		camera.zoom(cameraData.getZoom() * 2f);
		camera.move(cameraData.getX(), cameraData.getY());
		camera.rotate(cameraData.getAngle());

		int deltaInMs = (int) (delta * 1000f);

		score += 100 * delta;

		HealthComponent healthComponent = mainCharacter.getComponent(HealthComponent.class);
		SpatialComponent spatialComponent = mainCharacter.getComponent(SpatialComponent.class);

		if (healthComponent.getHealth().isEmpty() || (spatialComponent.getPosition().y < -50)) {
			// set score based on something...!!
			game.scoreScreen.setScore((int) score);
			game.setScreen(game.scoreScreen);
			gameOver = true;
		}

		worldWrapper.update(deltaInMs);

		Resource<BitmapFont> font = resourceManager.get("Font");
		BitmapFont bitmapFont = font.get();

		spriteBatch.begin();
		String scoreLabel = "score: " + (int) score;
		spriteBatch.setColor(Color.WHITE);
		bitmapFont.setScale(0.7f);
		bitmapFont.draw(spriteBatch, scoreLabel, 10, Gdx.graphics.getHeight() - 10);
		spriteBatch.end();

		Synchronizers.synchronize();

		inputDevicesMonitor.update();

		if (inputDevicesMonitor.getButton("score").isPressed()) {
			game.setScreen(game.scoreScreen);
		}

		for (int i = 0; i < controllers.size(); i++) {
			Controller controller = controllers.get(i);
			controller.update(deltaInMs);
		}

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {

		// if game over then ->
		if (gameOver) {
			restartGame();
			gameOver = false;
		}

	}

	protected void loadResources() {

		new LibgdxResourceBuilder(resourceManager) {
			{
				texture("Background", "data/background-512x512.jpg");
				texture("Human", "data/character-64x64.png");

				texture("Grass01", "data/grass01.png");
				texture("Grass02", "data/grass02.png");
				texture("Grass03", "data/grass03.png");
				texture("Grass04", "data/grass04.png");

				texture("Tile01", "data/tile01.png");
				texture("Tile02", "data/tile02.png");
				texture("Tile03", "data/tile03.png");
				texture("Tile04", "data/tile04.png");
				texture("Tile05", "data/tile05.png");

				texture("FontTexture", "data/font.png");

				spriteSheet("Human_Walking", "data/animation2.png", 0, 32, 32, 32, 2);
				spriteSheet("Human_Idle", "data/animation2.png", 0, 0, 32, 32, 2);
				spriteSheet("Human_Jump", "data/animation2.png", 0, 64, 32, 32, 1);
				spriteSheet("Human_Fall", "data/animation2.png", 0, 96, 32, 32, 2);

				spriteSheet("Robo", "data/animation2.png", 96, 32, 32, 32, 1);

				spriteSheet("Enemy", "data/animation2.png", 64, 32, 32, 32, 1);

				spriteSheet("EnemyLaser", "data/animation2.png", 64, 0, 32, 32, 3);
				spriteSheet("FriendlyLaser", "data/animation2.png", 64, 64, 32, 32, 3);

				sound("Jump", "data/jump.ogg");
				sound("Laser", "data/laser.ogg");
				sound("Explosion", "data/explosion.ogg");
			}

			private void spriteSheet(String id, final String file, final int x, final int y, final int w, final int h, final int framesCount) {
				resourceManager.add(id, new ResourceLoaderImpl<SpriteSheet>(new DataLoader<SpriteSheet>() {

					@Override
					public SpriteSheet load() {

						Texture spriteSheet = new Texture(internal(file));

						Sprite[] frames = new Sprite[framesCount];
						for (int i = 0; i < frames.length; i++)
							frames[i] = new Sprite(spriteSheet, x + i * w, y, w, h);

						return new SpriteSheet(spriteSheet, frames);
					}

				}));
			}

		};

		Resource<Texture> fontTextureResource = resourceManager.get("FontTexture");
		resourceManager.add("Font", new CachedResourceLoader<BitmapFont>(new ResourceLoaderImpl<BitmapFont>( //
				new BitmapFontDataLoader(Gdx.files.internal("data/font.fnt"), new Sprite(fontTextureResource.get())))));

	}

	@Override
	public void dispose() {

	}

}
