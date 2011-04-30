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
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.commons.svg.inkscape.SvgDocument;
import com.gemserk.commons.svg.inkscape.SvgDocumentHandler;
import com.gemserk.commons.svg.inkscape.SvgInkscapeGroup;
import com.gemserk.commons.svg.inkscape.SvgInkscapeGroupHandler;
import com.gemserk.commons.svg.inkscape.SvgInkscapePath;
import com.gemserk.commons.svg.inkscape.SvgInkscapePathHandler;
import com.gemserk.commons.svg.inkscape.SvgParser;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class GameScreen extends ScreenAdapter {

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

	public GameScreen(Game game) {
		this.game = game;
		
		viewportWidth = Gdx.graphics.getWidth();
		viewportHeight = Gdx.graphics.getHeight();
		
		camera.center(viewportWidth / 2, viewportHeight / 2);
		camera.zoom(40f);
		
		Vector2 cameraPosition = new Vector2(viewportWidth * 0.5f * 0.025f, viewportHeight * 0.5f * 0.025f);
		cameraData = new Camera(cameraPosition, 40f, 0f);

		// create the scene...

		World world = new World();
		
		worldWrapper = new WorldWrapper(world);
		
		ArrayList<RenderLayer> renderLayers = new ArrayList<RenderLayer>();
		
		// background layer
		renderLayers.add(new RenderLayer(-1000, -100, backgroundLayerCamera));
		// world layer
		renderLayers.add(new RenderLayer(-100, 100, camera));
		// hud layer
		renderLayers.add(new RenderLayer(100, 1000, hudLayerCamera));
		
		worldWrapper.add(new SpriteUpdateSystem());
		worldWrapper.add(new SpriteRendererSystem(renderLayers));
		
		worldWrapper.init();

		physicsWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0, -10f), false);
		
		physicsObjectsFactory = new PhysicsObjectsFactory(physicsWorld);
		
		box2dCustomDebugRenderer = new Box2DCustomDebugRenderer(camera, physicsWorld);

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		
		resourceManager = new ResourceManagerImpl<String>();
		
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorKey("debug", Keys.D);
			}
		};
		
		// load scene!!
		
		loadResources();
		
		Resource<Texture> resource = resourceManager.get("Background");
		Sprite backgroundSprite = new Sprite(resource.get());
		
		Entity background = world.createEntity();
		
		background.addComponent(new SpatialComponent(new Vector2(0,0), new Vector2(viewportWidth, viewportWidth), 0f));
		background.addComponent(new SpriteComponent(backgroundSprite, -101, new Vector2(0f, 0f), new Color(Color.WHITE)));
		
		background.refresh();
		
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

	@Override
	public void render(float delta) {

		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		Vector2 cameraPosition = cameraData.position;

		camera.zoom(cameraData.zoom);
		camera.move(cameraPosition.x, cameraPosition.y);
		camera.rotate(cameraData.angle);
		
		worldWrapper.update((int) (delta * 1000f));
		
		inputDevicesMonitor.update();

		if (inputDevicesMonitor.getButton("debug").isHolded()) {

			// render debug stuff.
			box2dCustomDebugRenderer.render();
			

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
			}
		};

	}

	@Override
	public void dispose() {

	}

}
