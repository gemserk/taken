package com.gemserk.games.taken;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.systems.ActivableSystem;
import com.gemserk.commons.artemis.systems.ActivableSystemImpl;
import com.gemserk.commons.artemis.triggers.Trigger;
import com.gemserk.games.taken.components.GrabComponent;

public class GrabSystem extends EntityProcessingSystem implements ActivableSystem {

	private final ActivableSystem activableSystem = new ActivableSystemImpl();

	public GrabSystem() {
		super(GrabComponent.class);
	}

	@Override
	public void toggle() {
		activableSystem.toggle();
	}

	@Override
	public boolean isEnabled() {
		return activableSystem.isEnabled();
	}
	
	// It is more like a collision detection system, with a trigger on collision detection...

	@Override
	protected void process(Entity e) {
		SpatialComponent spatialComponent = e.getComponent(SpatialComponent.class);

		Entity mainCharacter = world.getTagManager().getEntity("MainCharacter");

		if (mainCharacter == null)
			return;

		SpatialComponent targetSpatialComponent = mainCharacter.getComponent(SpatialComponent.class);
		GrabComponent grabComponent = e.getComponent(GrabComponent.class);

		Vector2 targetPosition = targetSpatialComponent.getSpatial().getPosition();
		Vector2 position = spatialComponent.getSpatial().getPosition();

		if (position.dst(targetPosition) > grabComponent.getRadius())
			return;

		Trigger trigger = grabComponent.getTrigger();
		if (!trigger.isAlreadyTriggered())
			trigger.trigger(e);
	}

}
