package com.gemserk.games.taken;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.gemserk.commons.gdx.box2d.BodyBuilder;

public class PhysicsObjectsFactory {

	private final BodyBuilder bodyBuilder;
	
	private final World world;

	public PhysicsObjectsFactory(World world) {
		this.world = world;
		bodyBuilder = new BodyBuilder(world);
	}

	public BodyBuilder bodyBuilder() {
		// return new BodyBuilder(world).reset();
		return bodyBuilder.reset();
	}

	public Body createBody(BodyBuilder bodyBuilder) {
		return bodyBuilder.build();
	}

}
