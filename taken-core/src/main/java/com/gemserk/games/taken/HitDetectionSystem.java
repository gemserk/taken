package com.gemserk.games.taken;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.systems.ActivableSystem;
import com.gemserk.commons.artemis.systems.ActivableSystemImpl;
import com.gemserk.componentsengine.utils.Container;
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;

public class HitDetectionSystem extends EntityProcessingSystem implements ActivableSystem {

	private final ActivableSystem activableSystem = new ActivableSystemImpl();
	private final ResourceManager<String> resourceManager;

	public HitDetectionSystem(ResourceManager<String> resourceManager) {
		super(HitComponent.class);
		// and health component
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

		HitComponent hitComponent = e.getComponent(HitComponent.class);
		
		String hitGroup = hitComponent.getGroup();
		
		ImmutableBag<Entity> targets = world.getGroupManager().getEntities(hitGroup);
		
		if (targets == null)
			return ;
		
		if (targets.isEmpty())
			return;
		
		SpatialComponent spatialComponent = e.getComponent(SpatialComponent.class);
		Vector2 position = spatialComponent.getPosition();
		
		for (int i = 0; i < targets.size(); i++) {
			Entity target = targets.get(i);
			SpatialComponent targetSpatialComponent = target.getComponent(SpatialComponent.class);
			Vector2 targetPosition = targetSpatialComponent.getPosition();
			
			if (position.dst(targetPosition) < 0.3f) {
				
				HealthComponent healthComponent = target.getComponent(HealthComponent.class);
				Container health = healthComponent.getContainer();
				
				health.remove(hitComponent.getDamage());
				
				// explosion sound
				// explosion graphics
				
				Resource<Sound> explosionSound = resourceManager.get("Explosion");
				explosionSound.get().play();
				
				world.deleteEntity(e);
				
				if (health.isEmpty())
					world.deleteEntity(target);
				
				return;
				
				// reduce health, if 0 then remove the entity
			}
			
		}
	

	}

}
