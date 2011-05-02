package com.gemserk.games.taken;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.systems.ActivableSystem;
import com.gemserk.commons.artemis.systems.ActivableSystemImpl;
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;

public class HealthVialSystem extends EntityProcessingSystem implements ActivableSystem {

	private final ActivableSystem activableSystem = new ActivableSystemImpl();
	
	private final ResourceManager<String> resourceManager;

	public HealthVialSystem(ResourceManager<String> resourceManager) {
		super(HealthVialComponent.class);
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
			// add particle effects!!
			world.deleteEntity(e);
			return;
		}
		
		Entity mainCharacter = world.getTagManager().getEntity("MainCharacter");
		
		if (mainCharacter == null)
			return;
		
		SpatialComponent targetSpatialComponent = mainCharacter.getComponent(SpatialComponent.class);
		Vector2 targetPosition = targetSpatialComponent.getPosition();
		
		Vector2 position = spatialComponent.getPosition();
		
		if (position.dst(targetPosition) > 0.1f)
			return;
		
		HealthComponent healthComponent = e.getComponent(HealthComponent.class);
		HealthComponent characterHealthComponent = mainCharacter.getComponent(HealthComponent.class);
		
		characterHealthComponent.getHealth().add(healthComponent.getHealth().getCurrent());
		
		Resource<Sound> healthVialSound = resourceManager.get("HealthVialSound");
		healthVialSound.get().play();
		
		world.deleteEntity(e);
	}

}
