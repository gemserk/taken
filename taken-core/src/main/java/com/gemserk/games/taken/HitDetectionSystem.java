package com.gemserk.games.taken;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.gemserk.commons.artemis.systems.ActivableSystem;
import com.gemserk.commons.artemis.systems.ActivableSystemImpl;
import com.gemserk.commons.artemis.triggers.Trigger;
import com.gemserk.games.taken.components.HitComponent;
import com.gemserk.games.taken.components.PhysicsComponent;
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

		PhysicsComponent physicsComponent = e.getComponent(PhysicsComponent.class);
		Contact contact = physicsComponent.getContact();
		Trigger trigger = hitComponent.getTrigger();

		for (int i = 0; i < contact.getContactCount(); i++) {

			if (!contact.isInContact(i))
				continue;

			Entity otherEntity = contact.getEntity(i);
			if (otherEntity == null)
				continue;

			targetComponent.setTarget(otherEntity);

			if (trigger.isAlreadyTriggered())
				return;

			trigger.trigger(e);

		}

	}

}
