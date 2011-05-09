package com.gemserk.games.taken;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.systems.ActivableSystem;
import com.gemserk.commons.artemis.systems.ActivableSystemImpl;
import com.gemserk.games.taken.components.GrabComponent;
import com.gemserk.games.taken.components.TimerComponent;
import com.gemserk.resources.ResourceManager;

public class GrabSystem extends EntityProcessingSystem implements ActivableSystem {

	private final ActivableSystem activableSystem = new ActivableSystemImpl();

	private final ResourceManager<String> resourceManager;

	public GrabSystem(ResourceManager<String> resourceManager) {
		super(GrabComponent.class);
		this.resourceManager = resourceManager;
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
		TimerComponent timerComponent = e.getComponent(TimerComponent.class);

		SpatialComponent spatialComponent = e.getComponent(SpatialComponent.class);

		if (timerComponent.isFinished()) {
			world.deleteEntity(e);
			return;
		}

		Entity mainCharacter = world.getTagManager().getEntity("MainCharacter");

		if (mainCharacter == null)
			return;

		SpatialComponent targetSpatialComponent = mainCharacter.getComponent(SpatialComponent.class);
		Vector2 targetPosition = targetSpatialComponent.getPosition();

		Vector2 position = spatialComponent.getPosition();

		if (position.dst(targetPosition) > 0.25f)
			return;

		GrabComponent grabComponent = e.getComponent(GrabComponent.class);
		grabComponent.getGrabHandler().handle(e);
	}

}
