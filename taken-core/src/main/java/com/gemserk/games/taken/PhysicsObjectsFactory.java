package com.gemserk.games.taken;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

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

	public Body createDynamicRectangle(float x, float y, float width, float height, boolean fixedRotation) {

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		// bodyDef.bullet = true;
		bodyDef.position.set(x, y);
		bodyDef.fixedRotation = fixedRotation;

		Body body = world.createBody(bodyDef);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2f, height / 2f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1f;

		body.createFixture(fixtureDef);

		shape.dispose();

		// 0.7112 mts

		// body.setTransform(tmp, (float) (direction.angle() / 180f * Math.PI));

		return body;

	}

}
