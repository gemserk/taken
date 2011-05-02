package com.gemserk.games.taken;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.gemserk.commons.artemis.components.MovementComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.systems.ActivableSystem;
import com.gemserk.commons.artemis.systems.ActivableSystemImpl;

public class BulletSystem extends EntityProcessingSystem implements ActivableSystem {

	private final ActivableSystem activableSystem = new ActivableSystemImpl();

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
		SpatialComponent spatialComponent = e.getComponent(SpatialComponent.class);
		MovementComponent movementComponent = e.getComponent(MovementComponent.class);
		float angle = movementComponent.getVelocity().angle();
		spatialComponent.setAngle(angle);
		
		TimerComponent timerComponent = e.getComponent(TimerComponent.class);
		
		if (timerComponent.isFinished()) {
			// add particle effects!!
			world.deleteEntity(e);
		}
		
		// or, if collides with character...
	}

}
