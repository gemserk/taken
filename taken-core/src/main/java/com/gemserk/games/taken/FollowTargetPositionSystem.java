package com.gemserk.games.taken;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gemserk.commons.artemis.systems.ActivableSystem;
import com.gemserk.commons.artemis.systems.ActivableSystemImpl;
import com.gemserk.commons.artemis.triggers.Trigger;
import com.gemserk.games.taken.components.PhysicsComponent;
import com.gemserk.games.taken.components.TargetPositionComponent;

public class FollowTargetPositionSystem extends EntityProcessingSystem implements ActivableSystem {
	
	private final static Vector2 direction = new Vector2();

	private final ActivableSystem activableSystem = new ActivableSystemImpl();

	@SuppressWarnings("unchecked")
	public FollowTargetPositionSystem() {
		super(TargetPositionComponent.class, PhysicsComponent.class);
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
		PhysicsComponent physicsComponent = e.getComponent(PhysicsComponent.class);
		TargetPositionComponent targetPositionComponent = e.getComponent(TargetPositionComponent.class);
		
		Body body = physicsComponent.getBody();
		Vector2 position = body.getTransform().getPosition();
		Vector2 targetPosition = targetPositionComponent.getPosition();
		
		direction.set(targetPosition).sub(position);
		float distance = direction.len();
		direction.nor();
		
		body.applyForce(direction, position);
		
		if (distance < targetPositionComponent.getDistance()) {
			Trigger trigger = targetPositionComponent.getTrigger();
			if (trigger.isAlreadyTriggered())
				return;
			trigger.trigger(e);
		}
		
	}

}
