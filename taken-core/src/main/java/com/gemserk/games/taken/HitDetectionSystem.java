package com.gemserk.games.taken;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.systems.ActivableSystem;
import com.gemserk.commons.artemis.systems.ActivableSystemImpl;
import com.gemserk.commons.artemis.triggers.Trigger;
import com.gemserk.games.taken.components.HitComponent;
import com.gemserk.games.taken.components.TargetComponent;

public class HitDetectionSystem extends EntityProcessingSystem implements ActivableSystem {

	private final ActivableSystem activableSystem = new ActivableSystemImpl();

	public HitDetectionSystem() {
		super(HitComponent.class);
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
		TargetComponent targetComponent = e.getComponent(TargetComponent.class);

		targetComponent.setTarget(null);

		String hitGroup = hitComponent.getGroup();

		ImmutableBag<Entity> targets = world.getGroupManager().getEntities(hitGroup);

		if (targets == null)
			return;

		if (targets.isEmpty())
			return;

		SpatialComponent spatialComponent = e.getComponent(SpatialComponent.class);
		Vector2 position = spatialComponent.getPosition();

		// Entity mainCharacter = world.getTagManager().getEntity("MainCharacter");

		// find the first entity colliding

		Entity targetEntity = null;

		for (int i = 0; i < targets.size(); i++) {
			Entity target = targets.get(i);
			SpatialComponent targetSpatialComponent = target.getComponent(SpatialComponent.class);
			Vector2 targetPosition = targetSpatialComponent.getPosition();

			if (position.dst(targetPosition) < 0.3f) {
				targetEntity = target;
				break;
			}

		}

		if (targetEntity == null)
			return;

		targetComponent.setTarget(targetEntity);
		Trigger trigger = hitComponent.getTrigger();
		if (trigger.isAlreadyTriggered())
			return;
		trigger.trigger(e);
	}

}
