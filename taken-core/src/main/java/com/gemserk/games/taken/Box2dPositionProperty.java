package com.gemserk.games.taken;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gemserk.componentsengine.properties.AbstractProperty;

public class Box2dPositionProperty extends AbstractProperty<Vector2> {

	private final Body body;

	public Box2dPositionProperty(Body body) {
		this.body = body;
	}

	@Override
	public Vector2 get() {
		return body.getTransform().getPosition();
	}

	@Override
	public void set(Vector2 value) {
		body.setTransform(value, body.getAngle());
	}
}