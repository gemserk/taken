package com.gemserk.games.taken;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gemserk.commons.artemis.systems.ActivableSystem;
import com.gemserk.commons.artemis.systems.ActivableSystemImpl;
import com.gemserk.games.taken.components.BulletComponent;
import com.gemserk.games.taken.components.PhysicsComponent;

public class BulletSystem extends EntityProcessingSystem implements ActivableSystem {

	private static final Vector2 antiGravity = new Vector2(0, 10f);
	
	private final ActivableSystem activableSystem = new ActivableSystemImpl();
	
	private final Vector2 bodyAntiGravity = new Vector2(0, 10f);

	public BulletSystem() {
		super(BulletComponent.class);
	}

	@Override
	public void toggle() {
		activableSystem.toggle();
	}

	@Override
	public boolean isEnabled() {
		return activableSystem.isEnabled();
	}

	@Override
	protected void process(Entity e) {
		// SpatialComponent spatialComponent = e.getComponent(SpatialComponent.class);
		// MovementComponent movementComponent = e.getComponent(MovementComponent.class);
		// float angle = movementComponent.getVelocity().angle();
		// spatialComponent.setAngle(angle);

		PhysicsComponent physicsComponent = e.getComponent(PhysicsComponent.class);
		Body body = physicsComponent.getBody();
		
		bodyAntiGravity.set(antiGravity);
		bodyAntiGravity.mul(body.getMass());
		
		body.applyForce(bodyAntiGravity, body.getTransform().getPosition());

		float angle = body.getLinearVelocity().angle();
		Vector2 position = body.getTransform().getPosition();

		body.setTransform(position, MathUtils.degreesToRadians * angle);

		// new Vector2(0, -10f)
		
		// or, if collides with character...
	}

}
