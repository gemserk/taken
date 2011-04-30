package com.gemserk.games.taken;

import javax.vecmath.Vector2f;

import com.artemis.World;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.gdx.ScreenAdapter;
import com.gemserk.commons.gdx.box2d.Box2DCustomDebugRenderer;
import com.gemserk.commons.gdx.camera.Camera;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.svg.inkscape.SvgDocument;
import com.gemserk.commons.svg.inkscape.SvgDocumentHandler;
import com.gemserk.commons.svg.inkscape.SvgInkscapeGroup;
import com.gemserk.commons.svg.inkscape.SvgInkscapeGroupHandler;
import com.gemserk.commons.svg.inkscape.SvgInkscapePath;
import com.gemserk.commons.svg.inkscape.SvgInkscapePathHandler;
import com.gemserk.commons.svg.inkscape.SvgParser;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;

public class GameScreen extends ScreenAdapter {

	private Game game;

	private WorldWrapper worldWrapper;

	private InputDevicesMonitorImpl<String> inputDevicesMonitor;
	
	private Libgdx2dCameraTransformImpl camera = new Libgdx2dCameraTransformImpl();
	
	private Box2DCustomDebugRenderer box2dCustomDebugRenderer;

	private com.badlogic.gdx.physics.box2d.World physicsWorld;
	
	private SvgDocument svgDocument;

	private PhysicsObjectsFactory physicsObjectsFactory;
	
	private int viewportWidth;

	private int viewportHeight;

	private Camera cameraData;

	public GameScreen(Game game) {
		this.game = game;
		
		viewportWidth = Gdx.graphics.getWidth();
		viewportHeight = Gdx.graphics.getHeight();
		
		camera.center(viewportWidth / 2, viewportHeight / 2);
		camera.zoom(40f);
		
		Vector2 cameraPosition = new Vector2(viewportWidth * 0.5f * 0.025f, viewportHeight * 0.5f * 0.025f);
		cameraData = new Camera(cameraPosition, 40f, 0f);

		// create the scene...

		worldWrapper = new WorldWrapper(new World());

		physicsWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0, -10f), false);
		
		physicsObjectsFactory = new PhysicsObjectsFactory(physicsWorld);
		
		box2dCustomDebugRenderer = new Box2DCustomDebugRenderer(camera, physicsWorld);

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();

		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorKey("debug", Keys.D);
			}
		};
		
		// load scene!!
		
		SvgParser svgParser = new SvgParser();
		svgParser.addHandler(new SvgDocumentHandler() {
			@Override
			protected void handle(SvgParser svgParser, SvgDocument svgDocument) {
				GameScreen.this.svgDocument = svgDocument;
			}
		});
		svgParser.addHandler(new SvgInkscapeGroupHandler() {
			@Override
			protected void handle(SvgParser svgParser, SvgInkscapeGroup svgInkscapeGroup) {
				
				if (svgInkscapeGroup.getGroupMode().equals("layer") && !svgInkscapeGroup.getLabel().equalsIgnoreCase("Physics")) {
					svgParser.processChildren(false);
					return;
				}

			}
		});
		svgParser.addHandler(new SvgInkscapePathHandler() {
			@Override
			protected void handle(SvgParser svgParser, SvgInkscapePath svgPath) {
				
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

	}

	@Override
	public void dispose() {

	}

}
