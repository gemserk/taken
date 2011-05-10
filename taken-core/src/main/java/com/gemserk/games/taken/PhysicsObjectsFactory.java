package com.gemserk.games.taken;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.gemserk.commons.gdx.box2d.Box2dUtils;

public class PhysicsObjectsFactory {

	private final World world;

	private final Vector2 tmp = new Vector2();

	public PhysicsObjectsFactory(World world) {
		this.world = world;
	}

	public Body createGround(Vector2 position, Vector2 size) {
		Vector2[] vertices = new Vector2[] { //
		new Vector2(-size.x * 0.5f, -size.y * 0.5f), //
				new Vector2(size.x * 0.5f, -size.y * 0.5f), //
				new Vector2(size.x * 0.5f, size.y * 0.5f), //
				new Vector2(-size.x * 0.5f, size.y * 0.5f), //
		};
		return createGround(position, vertices);
	}

	public Body createGround(Vector2 position, final Vector2[] vertices) {
		PolygonShape shape = new PolygonShape();
		shape.set(vertices);

		// shape.setAsBox(2f, 2f);

		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.type = BodyType.StaticBody;
		groundBodyDef.position.set(position);
		Body body = world.createBody(groundBodyDef);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.friction = 0.5f;
		fixtureDef.density = 1f;

		body.createFixture(fixtureDef);
		shape.dispose();

		return body;
	}

	public Body createBox(Vector2 position, Vector2 size) {
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(size.x * 0.5f, size.y * 0.5f);

		// shape.setAsBox(2f, 2f);

		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.type = BodyType.StaticBody;
		groundBodyDef.position.set(position);
		Body body = world.createBody(groundBodyDef);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.friction = 0.5f;
		fixtureDef.density = 1f;

		body.createFixture(fixtureDef);
		shape.dispose();

		return body;
	}

	public Vector2[] createRectangle(float width, float height) {
		Vector2[] vertices = Box2dUtils.initArray(4);
		Box2dUtils.createRectangle(width, height, vertices);
		return vertices;
	}

	public Body createPolygonBody(float x, float y, Vector2[] vertices, boolean fixedRotation, float friction, float density, float restitution, float mass, short categoryBits, short maskBits) {

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(x, y);
		bodyDef.fixedRotation = fixedRotation;

		Body body = world.createBody(bodyDef);

		PolygonShape shape = new PolygonShape();
		shape.set(vertices);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = density;
		fixtureDef.friction = friction;
		fixtureDef.restitution = restitution;

		fixtureDef.filter.categoryBits = categoryBits;
		fixtureDef.filter.maskBits = maskBits;

		body.createFixture(fixtureDef);

		MassData massData = body.getMassData();
		massData.mass = mass;
		body.setMassData(massData);

		shape.dispose();

		tmp.set(x, y);
		body.setTransform(tmp, 0f);

		return body;
	}

	// TODO : make a body builder!!

	public class BodyBuilder {

		private BodyDef bodyDef;
		
		private FixtureDef fixtureDef;

		private float mass = 1f;

		private Object userData = null;
		
		private Vector2 position = new Vector2();

		public BodyBuilder() {
			bodyDef = new BodyDef();
			fixtureDef = new FixtureDef();
		}

		public BodyBuilder type(BodyType type) {
			bodyDef.type = type;
			return this;
		}
		
		public BodyBuilder bullet() {
			bodyDef.bullet = true;
			return this;
		}
		
		public BodyBuilder sensor() {
			fixtureDef.isSensor = true;
			return this;
		}
		
		public BodyBuilder boxShape(float hx, float hy) {
			PolygonShape shape = new PolygonShape();
			shape.setAsBox(hx, hy);
			fixtureDef.shape = shape;
			return this;
		}
		
		public BodyBuilder circleShape(float radius) {
			Shape shape = new CircleShape();
			shape.setRadius(radius);
			fixtureDef.shape = shape;
			return this;
		}
		
		public BodyBuilder polygonShape(Vector2[] vertices) {
			PolygonShape shape = new PolygonShape();
			shape.set(vertices);
			fixtureDef.shape = shape;
			return this;
		}
		
		public BodyBuilder mass(float mass) {
			this.mass = mass;
			return this;
		}
		
		public BodyBuilder friction(float friction) {
			fixtureDef.friction = friction;
			return this;
		}

		public BodyBuilder categoryBits(short categoryBits) {
			fixtureDef.filter.categoryBits = categoryBits;
			return this;
		}

		public BodyBuilder maskBits(short maskBits) {
			fixtureDef.filter.maskBits = maskBits;
			return this;
		}
		
		public BodyBuilder userData(Object userData) {
			this.userData = userData;
			return this;
		}
		
		public BodyBuilder position(float x, float y) {
			this.position.set(x, y);
			return this;
		}

		public Body build() {
			Body body = world.createBody(bodyDef);
			body.createFixture(fixtureDef);
			
			MassData massData = body.getMassData();
			massData.mass = mass;
			body.setMassData(massData);
			body.setUserData(userData);
			body.setTransform(position, 0f);
			return body;
		}

	}

	public BodyBuilder bodyBuilder() {
		return new BodyBuilder();
	}

	public Body createBody(BodyBuilder bodyBuilder) {
		return bodyBuilder.build();
	}

	public Body builderExample() {
		return createBody(bodyBuilder() //
				.type(BodyType.DynamicBody) //
				.circleShape(5f) //
				.bullet() //
				.mass(1f) //
				.friction(0.1f) //
				.categoryBits(CollisionBits.EnemyLaser) //
				.maskBits(CollisionBits.All)
		);
	}

	public Body createDynamicRectangle(float x, float y, float width, float height, boolean fixedRotation, float friction, float mass, boolean bullet, //
			boolean isSensor, short categoryBits, short maskBits) {

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.bullet = bullet;
		bodyDef.position.set(x, y);
		bodyDef.fixedRotation = fixedRotation;

		Body body = world.createBody(bodyDef);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2f, height / 2f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1f;
		fixtureDef.friction = friction;
		fixtureDef.isSensor = isSensor;

		fixtureDef.filter.categoryBits = categoryBits;
		fixtureDef.filter.maskBits = maskBits;

		body.createFixture(fixtureDef);

		MassData massData = body.getMassData();
		massData.mass = mass;
		body.setMassData(massData);

		shape.dispose();

		// 0.7112 mts

		// body.setTransform(tmp, (float) (direction.angle() / 180f * Math.PI));

		return body;

	}

	public Body createDynamicCircle(float x, float y, float radius, boolean fixedRotation, float friction) {

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		// bodyDef.bullet = true;
		bodyDef.position.set(x, y);
		bodyDef.fixedRotation = fixedRotation;

		Body body = world.createBody(bodyDef);

		CircleShape shape = new CircleShape();
		// PolygonShape shape = new PolygonShape();
		// shape.setPosition(position)
		shape.setRadius(radius);
		// shape.setAsBox(width / 2f, height / 2f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1f;
		fixtureDef.friction = friction;

		body.createFixture(fixtureDef);

		shape.dispose();

		// 0.7112 mts

		// body.setTransform(tmp, (float) (direction.angle() / 180f * Math.PI));

		return body;

	}

}
