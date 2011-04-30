package com.gemserk.games.taken;

import java.util.ArrayList;

import javax.vecmath.Vector2f;

import org.w3c.dom.Element;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
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
import com.gemserk.commons.svg.inkscape.SvgDocument;
import com.gemserk.commons.svg.inkscape.SvgDocumentHandler;
import com.gemserk.commons.svg.inkscape.SvgInkscapeGroup;
import com.gemserk.commons.svg.inkscape.SvgInkscapeGroupHandler;
import com.gemserk.commons.svg.inkscape.SvgInkscapePath;
import com.gemserk.commons.svg.inkscape.SvgInkscapePathHandler;
import com.gemserk.commons.svg.inkscape.SvgParser;
import com.gemserk.componentsengine.input.ButtonMonitor;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxButtonMonitor;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.componentsengine.properties.PropertyBuilder;
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class GameScreen extends ScreenAdapter {

	static class KeyboardCharacterController implements CharacterController {
		
		private Vector2 direction = new Vector2(0f, 0f);
		
		private boolean walking = false;
		
		private boolean jumped = false;
		
		ButtonMonitor jumpButtonMonitor = new LibgdxButtonMonitor(Keys.W);
		
		@Override
		public void update(int delta) {
			
			walking = false;
			jumped = false;
			
			direction.set(0f,0f);
			
			if (Gdx.input.isKeyPressed(Keys.DPAD_RIGHT)) {
				direction.x = 1f;
				walking = true;
			} else if (Gdx.input.isKeyPressed(Keys.DPAD_LEFT)) {
				direction.x = -1f;
				walking = true;
			}
			
			jumpButtonMonitor.update();
			
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
	}

	private Game game;

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

	public GameScreen(Game game) {
		this.game = game;

		viewportWidth = Gdx.graphics.getWidth();
		viewportHeight = Gdx.graphics.getHeight();

		camera.center(viewportWidth / 2, viewportHeight / 2);
		camera.zoom(40f);

		float zoom = 20f;
		float invZoom = 1 / zoom;
		
		Vector2 cameraPosition = new Vector2(viewportWidth * 0.5f * invZoom, viewportHeight * 0.5f * invZoom);
		cameraData = new Camera(cameraPosition, 20f, 0f);

		physicsWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0, -10f), false);

		physicsObjectsFactory = new PhysicsObjectsFactory(physicsWorld);

		box2dCustomDebugRenderer = new Box2DCustomDebugRenderer(camera, physicsWorld);

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();

		resourceManager = new ResourceManagerImpl<String>();

		// create the scene...

		world = new World();

		worldWrapper = new WorldWrapper(world);

		ArrayList<RenderLayer> renderLayers = new ArrayList<RenderLayer>();

		// background layer
		renderLayers.add(new RenderLayer(-1000, -100, backgroundLayerCamera));
		// world layer
		renderLayers.add(new RenderLayer(-100, 100, camera));
		// hud layer
		renderLayers.add(new RenderLayer(100, 1000, hudLayerCamera));

		worldWrapper.add(new PhysicsSystem(physicsWorld));
		worldWrapper.add(new CharacterControllerSystem());
		worldWrapper.add(new SpriteUpdateSystem());
		worldWrapper.add(new SpriteRendererSystem(renderLayers));

		worldWrapper.init();

		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorKey("debug", Keys.D);
			}
		};

		// load scene!!

		loadResources();

		createBackground();

		createMainCharacter();

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
					System.out.println(vertices[i]);
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
		Sprite sprite = new Sprite(resource.get());

		float x = 2f;
		float y = 2f;

		float width = 0.15f;
		float height = 1f;

		Body body = physicsObjectsFactory.createDynamicRectangle(x, y, width, height, true, 1f);

		Entity entity = world.createEntity();

		entity.addComponent(new PhysicsComponent(body));
		entity.addComponent(new SpatialComponent( //
				new Box2dPositionProperty(body), //
				PropertyBuilder.vector2(1f, 1f), //
				new Box2dAngleProperty(body)));
		// entity.addComponent(new SpatialComponent(new Vector2(0, 0), new Vector2(viewportWidth, viewportWidth), 0f));
		entity.addComponent(new SpriteComponent(sprite, 1, new Vector2(0.5f, 0.5f), new Color(Color.WHITE)));

		KeyboardCharacterController characterController = new KeyboardCharacterController();
		
		entity.addComponent(new CharacterControllerComponent(characterController));
		
		controllers.add(characterController);

		entity.refresh();
	}

	@Override
	public void render(float delta) {

		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);

		Vector2 cameraPosition = cameraData.position;

		camera.zoom(cameraData.zoom);
		camera.move(cameraPosition.x, cameraPosition.y);
		camera.rotate(cameraData.angle);

		int deltaInMs = (int) (delta * 1000f);
		
		worldWrapper.update(deltaInMs);

		inputDevicesMonitor.update();

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

	}

	protected void loadResources() {

		new LibgdxResourceBuilder(resourceManager) {
			{
				texture("Background", "data/background-512x512.jpg");
				texture("Human", "data/character-64x64.png");
			}
		};

	}

	@Override
	public void dispose() {

	}

}
