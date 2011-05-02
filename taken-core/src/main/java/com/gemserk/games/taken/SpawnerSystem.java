package com.gemserk.games.taken;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.gemserk.commons.artemis.entities.EntityTemplate;
import com.gemserk.commons.artemis.systems.ActivableSystem;
import com.gemserk.commons.artemis.systems.ActivableSystemImpl;

public class SpawnerSystem extends EntityProcessingSystem implements ActivableSystem {

	private final ActivableSystem activableSystem = new ActivableSystemImpl();
	
	public SpawnerSystem() {
		super(SpawnerComponent.class);
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
		
		SpawnerComponent spawnerComponent = e.getComponent(SpawnerComponent.class);
		
		spawnerComponent.setTime(spawnerComponent.getTime() - world.getDelta());
		
		if (spawnerComponent.getTime() > 0)
			return;
		
		EntityTemplate entityTemplate = spawnerComponent.getEntityTemplate();
		
		entityTemplate.build();
		
		spawnerComponent.setTime(spawnerComponent.getSpawnTime());

	}

}
