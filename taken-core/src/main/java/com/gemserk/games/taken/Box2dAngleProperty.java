package com.gemserk.games.taken;

import com.badlogic.gdx.physics.box2d.Body;
import com.gemserk.commons.values.FloatValue;
import com.gemserk.componentsengine.properties.AbstractProperty;

public class Box2dAngleProperty extends AbstractProperty<FloatValue> {

	private FloatValue floatValue = new FloatValue(0f);

	private final Body body;

	public Box2dAngleProperty(Body body) {
		this.body = body;
	}

	public FloatValue get() {
		floatValue.value = (float) (body.getAngle() * 180f / Math.PI);
		return floatValue;
	}
}