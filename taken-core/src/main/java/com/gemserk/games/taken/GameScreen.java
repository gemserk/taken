package com.gemserk.games.taken;

import java.util.ArrayList;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import org.w3c.dom.Element;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gemserk.animation4j.FrameAnimation;
import com.gemserk.animation4j.FrameAnimationImpl;
import com.gemserk.animation4j.interpolator.function.InterpolationFunctions;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.artemis.components.MovementComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.entities.EntityTemplate;
import com.gemserk.commons.artemis.systems.MovementSystem;
import com.gemserk.commons.artemis.systems.RenderLayer;
import com.gemserk.commons.artemis.systems.SpriteRendererSystem;
import com.gemserk.commons.artemis.systems.SpriteUpdateSystem;
import com.gemserk.commons.gdx.ScreenAdapter;
import com.gemserk.commons.gdx.box2d.Box2DCustomDebugRenderer;
import com.gemserk.commons.gdx.box2d.Box2dUtils;
import com.gemserk.commons.gdx.camera.Camera;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.gdx.controllers.Controller;
import com.gemserk.commons.gdx.input.LibgdxPointer;
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
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.componentsengine.properties.PropertyBuilder;
import com.gemserk.componentsengine.utils.Container;
import com.gemserk.games.taken.PowerUp.Type;
import com.gemserk.games.taken.controllers.CharacterController;
import com.gemserk.games.taken.controllers.KeyboardCharacterController;
import com.gemserk.games.taken.controllers.MultiTouchCharacterController;
import com.gemserk.games.taken.controllers.SingleTouchCharacterController;
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;
import com.gemserk.resources.dataloaders.DataLoader;
import com.gemserk.resources.resourceloaders.CachedResourceLoader;
import com.gemserk.resources.resourceloaders.ResourceLoaderImpl;

public class GameScreen extends ScreenAdapter {

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

	}

	void restartGame() {

		Gdx.app.log("Taken", "Reloading the level");

		spriteBatch = new SpriteBatch();

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

		worldWrapper.add(new CharacterControllerSystem());
		worldWrapper.add(new JumpSystem());

		worldWrapper.add(new PhysicsSystem(physicsWorld));
		worldWrapper.add(new FollowCharacterBehaviorSystem());
		worldWrapper.add(new WeaponSystem(this, resourceManager));
		worldWrapper.add(new MovementSystem());
		worldWrapper.add(new BulletSystem());
		worldWrapper.add(new HealthVialSystem(resourceManager));

		worldWrapper.add(new GrabSystem(resourceManager));
		worldWrapper.add(new PowerUpSystem());

		worldWrapper.add(new AnimationSystem());
		worldWrapper.add(new BloodOverlaySystem());
		worldWrapper.add(new HitDetectionSystem(resourceManager));
		worldWrapper.add(new CameraFollowSystem());
		worldWrapper.add(new SpriteUpdateSystem());
		worldWrapper.add(new SpriteRendererSystem(renderLayers));
		worldWrapper.add(new TimerSystem());

		worldWrapper.add(new SpawnerSystem());

		worldWrapper.init();

		// load scene!!

		loadResources();

		createBackground();

		loadWorld();

		loadPhysicObjects();

		// Body body = physicsObjectsFactory.createBox(new Vector2(-2, 2), new Vector2(2, 2));

		// createMainCharacter();

		createCharacterBloodOverlay();

		createRobo();

		createEnemyRobotSpawner();

		createHealthVialSpawner();

		createPowerUpSpawner();

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

				// Resource<SpriteSheet> spriteSheet = resourceManager.get(svgImage.getLabel());
				// Texture texture = spriteSheet.get().getFrame(0).getTexture();

				Resource<Texture> tileResource = resourceManager.get(svgImage.getLabel());

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

				if (tileResource != null) {
					Texture texture = tileResource.get();
					// Sprite sprite = spriteSheet.get().getFrame(0);
					Sprite sprite = new Sprite(texture);

					sprite.setScale(sx, sy);

					createStaticSprite(sprite, x, y, width, height, angle, 0, 0.5f, 0.5f, Color.WHITE);
				} else {

					String start = element.getAttribute("start");

					if (start == null)
						return;

					if ("".equals(start))
						return;

					createMainCharacter(x, y);

				}

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

	void createMainCharacter(float x, float y) {
		Resource<Animation> walkingAnimationResource = resourceManager.get("Human_Walking");
		Resource<Animation> idleAnimationResource = resourceManager.get("Human_Idle");
		Resource<Animation> jumpAnimationResource = resourceManager.get("Human_Jump");
		Resource<Animation> fallAnimationResource = resourceManager.get("Human_Fall");

		Resource<Sound> jumpSound = resourceManager.get("JumpSound");

		Sprite sprite = new Sprite(idleAnimationResource.get().getFrame(0));

		float size = 1f;

		float width = 0.15f;
		float height = 1f;

		// Body body = physicsObjectsFactory.createDynamicRectangle(x, y, width, height, true, 1f);
		// Body body = physicsObjectsFactory.createDynamicCircle(x, y, height * 0.5f, false, 1f);

		// Vector2[] bodyShape = new Vector2[] {
		// new Vector2(0f, -0.5f),
		// // new Vector2(0.075f, 0.5f),
		// new Vector2(0f, 0.5f),
		// // new Vector2(-0.075f, 0.5f),
		// };

		// Box2dUtils.initArray(bodyShape);

		Vector2[] bodyShape = Box2dUtils.createRectangle(width, height);
		Body body = physicsObjectsFactory.createPolygonBody(x, y, bodyShape, true, 0.1f, 1f, 0f, 0.15f);

		mainCharacter = world.createEntity();

		mainCharacter.setTag("MainCharacter");

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

		Animation[] spriteSheets = new Animation[] { walkingAnimationResource.get(), idleAnimationResource.get(), jumpAnimationResource.get(), fallAnimationResource.get(), };

		FrameAnimation[] animations = new FrameAnimation[] { new FrameAnimationImpl(150, 2, true), //
				new FrameAnimationImpl(true, 1000, 50), //
				new FrameAnimationImpl(100, 1, false), //
				new FrameAnimationImpl(true, 400, 200), //
		};
		
		mainCharacter.addComponent(new AnimationComponent(spriteSheets, animations));

		mainCharacter.addComponent(new CameraFollowComponent(cameraData));

		mainCharacter.addComponent(new HealthComponent(new Container(50f, 50f)));

		CharacterController characterController = null;

		if (Gdx.app.getType() == ApplicationType.Desktop)
			characterController = new KeyboardCharacterController();
		else if (Gdx.input.isPeripheralAvailable(Peripheral.MultitouchScreen))
			characterController = new MultiTouchCharacterController(new LibgdxPointer(0), new LibgdxPointer(1));
		else
			characterController = new SingleTouchCharacterController(new LibgdxPointer(0));

		mainCharacter.addComponent(new CharacterControllerComponent(characterController));
		controllers.add(characterController);

		mainCharacter.addComponent(new JumpComponent(8f, jumpSound.get()));

		mainCharacter.refresh();
	}

	void createCharacterBloodOverlay() {
		Resource<Animation> frontBloodAnimationResource = resourceManager.get("FrontBloodOverlay");
		Resource<Animation> sideBloodAnimationResource = resourceManager.get("SideBloodOverlay");

		Sprite sprite = new Sprite(frontBloodAnimationResource.get().getFrame(0));

		float size = 1f;

		Entity e = world.createEntity();

		e.addComponent(new SpatialComponent(new Vector2(0, 0), new Vector2(size, size), 0));
		e.addComponent(new SpriteComponent(sprite, 2, new Vector2(0.5f, 0.5f), new Color(Color.WHITE)));

		Animation[] spriteSheets = new Animation[] { frontBloodAnimationResource.get(), sideBloodAnimationResource.get() };

		e.addComponent(new BloodOverlayComponent(mainCharacter, spriteSheets));

		e.refresh();
	}

	void createEnemyRobotSpawner() {
		Entity entity = world.createEntity();

		entity.addComponent(new SpawnerComponent(3500, new EntityTemplate() {
			@Override
			public Entity build() {
				// TODO Auto-generated function stub

				// limit robot spawner!!

				SpatialComponent spatialComponent = mainCharacter.getComponent(SpatialComponent.class);
				Vector2 position = spatialComponent.getPosition();

				float x = position.x + MathUtils.random(-5, 5);
				float y = position.y + MathUtils.random(-5, 5);

				createEnemy(x, y);

				return null;
			}
		}));

		entity.refresh();
	}

	void createHealthVialSpawner() {
		Entity entity = world.createEntity();

		entity.addComponent(new SpawnerComponent(10000, new EntityTemplate() {
			@Override
			public Entity build() {
				// TODO Auto-generated function stub

				SpatialComponent spatialComponent = mainCharacter.getComponent(SpatialComponent.class);
				Vector2 position = spatialComponent.getPosition();

				float x = position.x + MathUtils.random(-10, 10);
				float y = 2.5f + MathUtils.random(0f, 2.5f);

				createHealthVial(x, y, 15000, 25f);

				Gdx.app.log("Taken", "Health vial spawned at (" + x + ", " + y + ")");

				return null;
			}
		}));

		entity.refresh();
	}

	void createPowerUpSpawner() {
		Entity entity = world.createEntity();

		entity.addComponent(new SpawnerComponent(15000, new EntityTemplate() {
			@Override
			public Entity build() {
				// TODO Auto-generated function stub

				SpatialComponent spatialComponent = mainCharacter.getComponent(SpatialComponent.class);
				Vector2 position = spatialComponent.getPosition();

				float x = position.x + MathUtils.random(-10, 10);
				float y = 2.5f + MathUtils.random(0f, 2.5f);

				if (MathUtils.randomBoolean()) {
					createPowerUp(x, y, 25000, new PowerUp(Type.MovementSpeedModifier, 2f, 25000));
					Gdx.app.log("Taken", "Movement Speed Power Up spawned at (" + x + ", " + y + ")");
				} else {
					createPowerUp(x, y, 25000, new PowerUp(Type.WeaponSpeedModifier, 3f, 25000));
					Gdx.app.log("Taken", "Weapon Speed Power Up spawned at (" + x + ", " + y + ")");
				}

				return null;
			}
		}));

		entity.refresh();
	}

	void createRobo() {
		Resource<Animation> enemyAnimationResource = resourceManager.get("Robo");
		Sprite sprite = enemyAnimationResource.get().getFrame(0);

		float x = 0f;
		float y = -5f;

		float size = 1f;

		Entity entity = world.createEntity();

		entity.setTag("Robo");

		entity.addComponent(new SpatialComponent(new Vector2(x, y), new Vector2(size, size), 0f));
		entity.addComponent(new MovementComponent(new Vector2(), 0f));
		entity.addComponent(new SpriteComponent(sprite, 2, new Vector2(0.5f, 0.5f), new Color(Color.WHITE)));
		entity.addComponent(new FollowCharacterComponent(new Vector2(x, y), 0f));

		entity.addComponent(new WeaponComponent(500, 6f, 2.5f, "Player", "Enemy", 10f));

		Animation[] spriteSheets = new Animation[] { enemyAnimationResource.get(), };

		FrameAnimation[] animations = new FrameAnimation[] { new FrameAnimationImpl(150, 1, false), };

		entity.addComponent(new AnimationComponent(spriteSheets, animations));
		entity.addComponent(new PowerUpComponent());

		entity.refresh();
	}

	void createEnemy(float x, float y) {
		Resource<Animation> enemyAnimationResource = resourceManager.get("Enemy");
		Sprite sprite = enemyAnimationResource.get().getFrame(0);

		float size = 1f;

		Entity entity = world.createEntity();

		entity.setGroup("Enemy");

		entity.addComponent(new SpatialComponent(new Vector2(x, y), new Vector2(size, size), 0f));
		entity.addComponent(new MovementComponent(new Vector2(), 0f));
		entity.addComponent(new SpriteComponent(sprite, 2, new Vector2(0.5f, 0.5f), new Color(Color.WHITE)));
		entity.addComponent(new FollowCharacterComponent(new Vector2(x, y), 0f));
		entity.addComponent(new WeaponComponent(900, 5.5f, 7f, "Enemy", "Player", 5f));

		entity.addComponent(new HealthComponent(new Container(20f, 20f)));

		Animation[] spriteSheets = new Animation[] { enemyAnimationResource.get(), };

		FrameAnimation[] animations = new FrameAnimation[] { new FrameAnimationImpl(150, 1, false), };

		entity.addComponent(new AnimationComponent(spriteSheets, animations));

		entity.refresh();
	}

	void createLaser(float x, float y, int time, float dx, float dy, float damage, String ownerGroup, String targetGroup) {
		Resource<Animation> laserAnimationResource;

		if (ownerGroup.equals("Player"))
			laserAnimationResource = resourceManager.get("FriendlyLaser");
		else
			laserAnimationResource = resourceManager.get("EnemyLaser");

		Sprite sprite = laserAnimationResource.get().getFrame(0);

		float size = 1f;

		Entity entity = world.createEntity();

		entity.setGroup(ownerGroup);

		Color color = new Color();

		Synchronizers.transition(color, Transitions.transitionBuilder(laserStartColor).end(laserEndColor).time(time)//
				.functions(InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut()) //
				.build());

		entity.addComponent(new SpatialComponent(new Vector2(x, y), new Vector2(size, size), 0f));
		entity.addComponent(new MovementComponent(new Vector2(dx, dy), 0f));
		entity.addComponent(new SpriteComponent(sprite, 2, new Vector2(0.5f, 0.5f), color));
		entity.addComponent(new TimerComponent(time));

		entity.addComponent(new BulletComponent());

		entity.addComponent(new HitComponent(targetGroup, damage));

		entity.addComponent(new HealthComponent(new Container(2f, 2f)));

		Animation[] spriteSheets = new Animation[] { laserAnimationResource.get(), };

		FrameAnimation[] animations = new FrameAnimation[] { new FrameAnimationImpl(150, 3, false), };

		entity.addComponent(new AnimationComponent(spriteSheets, animations));

		entity.refresh();
	}

	void createHealthVial(float x, float y, int aliveTime, float health) {
		Resource<Animation> healthVialAnimationResource = resourceManager.get("HealthVial");

		Sprite sprite = healthVialAnimationResource.get().getFrame(0);

		float size = 1f;

		Entity entity = world.createEntity();

		Color color = new Color();

		int spawnTime = 1000;

		Synchronizers.transition(color, Transitions.transitionBuilder(laserEndColor).end(laserStartColor).time(spawnTime)//
				.functions(InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut()) //
				.build());

		entity.addComponent(new SpatialComponent(new Vector2(x, y), new Vector2(size, size), 0f));
		entity.addComponent(new SpriteComponent(sprite, -1, new Vector2(0.5f, 0.5f), color));
		entity.addComponent(new TimerComponent(aliveTime));

		entity.addComponent(new HealthComponent(new Container(health, health)));

		Animation[] spriteSheets = new Animation[] { healthVialAnimationResource.get(), };

		FrameAnimation[] animations = new FrameAnimation[] { new FrameAnimationImpl(750, 2, true), };

		entity.addComponent(new AnimationComponent(spriteSheets, animations));

		entity.addComponent(new HealthVialComponent());

		entity.refresh();
	}

	void createPowerUp(float x, float y, int aliveTime, PowerUp powerUp) {

		Resource<Animation> animation = resourceManager.get("Powerup01");

		if (powerUp.getType() == Type.MovementSpeedModifier)
			animation = resourceManager.get("Powerup02");

		Sprite sprite = animation.get().getFrame(0);

		float size = 1f;

		Entity entity = world.createEntity();

		Color color = new Color();

		int spawnTime = 1000;

		Synchronizers.transition(color, Transitions.transitionBuilder(laserEndColor).end(laserStartColor).time(spawnTime)//
				.functions(InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut()) //
				.build());

		entity.addComponent(new SpatialComponent(new Vector2(x, y), new Vector2(size, size), 0f));
		entity.addComponent(new SpriteComponent(sprite, -1, new Vector2(0.5f, 0.5f), color));
		entity.addComponent(new TimerComponent(aliveTime));

		Animation[] spriteSheets = new Animation[] { animation.get(), };

		FrameAnimation[] animations = new FrameAnimation[] { new FrameAnimationImpl(750, 2, true), };

		entity.addComponent(new AnimationComponent(spriteSheets, animations));

		entity.addComponent(new GrabComponent());
		entity.addComponent(new PowerUpComponent(powerUp));

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
			gameOver = true;
		}

		if (inputDevicesMonitor.getButton("debug").isHolded()) {

			// render debug stuff.
			box2dCustomDebugRenderer.render();
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
				setCacheWhenLoad(true);

				texture("Background", "data/background-512x512.jpg");

				texture("Tiles", "data/tiles.png", false);

				// add method for sprites (with or without cache)

				texture("Tile01", "data/tile01.png");
				texture("Tile02", "data/tile02.png");
				texture("Tile03", "data/tile03.png");
				texture("Tile04", "data/tile04.png");
				texture("Tile05", "data/tile05.png");
				texture("Tile06", "data/tile06.png");
				texture("Tile07", "data/tile07.png");
				texture("Tile08", "data/tile08.png");
				texture("Tile09", "data/tile09.png");

				// spriteSheet("Tile01", "Tiles", 128 * 0, 0, 128, 128, 1);
				// spriteSheet("Tile02", "Tiles", 128 * 1, 0, 128, 128, 1);
				// spriteSheet("Tile03", "Tiles", 128 * 2, 0, 128, 128, 1);
				// spriteSheet("Tile04", "Tiles", 128 * 3, 0, 128, 128, 1);

				texture("FontTexture", "data/font.png");

				texture("CharactersSpriteSheet", "data/animation2.png", false);

				animation("Human_Walking", "CharactersSpriteSheet", 0, 32, 32, 32, 2);
				animation("Human_Idle", "CharactersSpriteSheet", 0, 0, 32, 32, 2);
				animation("Human_Jump", "CharactersSpriteSheet", 0, 64, 32, 32, 1);
				animation("Human_Fall", "CharactersSpriteSheet", 0, 96, 32, 32, 2);
				animation("Robo", "CharactersSpriteSheet", 96, 32, 32, 32, 1);
				animation("Enemy", "CharactersSpriteSheet", 64, 32, 32, 32, 1);
				animation("EnemyLaser", "CharactersSpriteSheet", 64, 0, 32, 32, 3);
				animation("FriendlyLaser", "CharactersSpriteSheet", 64, 64, 32, 32, 3);
				animation("FrontBloodOverlay", "CharactersSpriteSheet", 0, 4 * 32, 32, 32, 3);
				animation("SideBloodOverlay", "CharactersSpriteSheet", 0, 5 * 32, 32, 32, 3);
				animation("HealthVial", "CharactersSpriteSheet", 5 * 32, 0, 32, 32, 2);
				animation("Powerup01", "CharactersSpriteSheet", 5 * 32, 2 * 32, 32, 32, 2);
				animation("Powerup02", "CharactersSpriteSheet", 5 * 32, 3 * 32, 32, 32, 2);

				sound("JumpSound", "data/jump.ogg");
				sound("FriendlyLaserSound", "data/laser.ogg");
				sound("EnemyLaserSound", "data/enemy_laser.ogg");
				sound("Explosion", "data/explosion.ogg");

				sound("HealthVialSound", "data/healthvial.ogg");
			}

			private void animation(String id, String spriteSheetId, final int x, final int y, final int w, final int h, final int framesCount) {
				final Texture spriteSheet = resourceManager.getResourceValue(spriteSheetId);
				resourceManager.add(id, new ResourceLoaderImpl<Animation>(new DataLoader<Animation>() {

					@Override
					public Animation load() {
						Sprite[] frames = new Sprite[framesCount];
						for (int i = 0; i < frames.length; i++)
							frames[i] = new Sprite(spriteSheet, x + i * w, y, w, h);
						return new Animation(frames);
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
		resourceManager.unloadAll();
		spriteBatch.dispose();
	}

}
