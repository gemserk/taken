package com.gemserk.games.taken;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gemserk.commons.artemis.systems.ActivableSystem;
import com.gemserk.commons.artemis.systems.ActivableSystemImpl;
import com.gemserk.games.taken.components.CharacterControllerComponent;
import com.gemserk.games.taken.components.JumpComponent;
import com.gemserk.games.taken.components.PhysicsComponent;

public class JumpSystem extends EntityProcessingSystem implements ActivableSystem {

	private final ActivableSystem activableSystem = new ActivableSystemImpl();

	private final Vector2 horizontalAxis = new Vector2(1f, 0f);

	private final Vector2 force = new Vector2(0, 1f);

	public JumpSystem() {
		super(CharacterControllerComponent.class);
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

		JumpComponent jumpComponent = e.getComponent(JumpComponent.class);

		PhysicsComponent physicsComponent = e.getComponent(PhysicsComponent.class);
		Body body = physicsComponent.getBody();

		float currentForce = jumpComponent.getCurrentForce();

		if (currentForce > 0.1f) {
			force.set(0, 1f);
			force.mul(currentForce);

			jumpComponent.setCurrentForce(currentForce * 0.85f);

			body.applyForce(force, body.getTransform().getPosition());

			Vector2 linearVelocity = body.getLinearVelocity();

			float speed = Math.abs(linearVelocity.y);
			float maxSpeed = 5f;

			if (speed > maxSpeed) {
				float factor = maxSpeed / speed;
				linearVelocity.y *= factor;
				body.setLinearVelocity(linearVelocity);
			}

			return;
		}

		if (!jumpComponent.isJumping())
			return;

		Contact contact = physicsComponent.getContact();

		int contactCount = contact.getContactCount();

		for (int i = 0; i < contactCount; i++) {

			if (!contact.isInContact(i))
				continue;

			Vector2 normal = contact.getNormal(i);

			float dot = Math.abs(normal.dot(horizontalAxis));

			if (dot > 0.4f)
				continue;

			jumpComponent.setCurrentForce(jumpComponent.getJumpForce());
			jumpComponent.setJumping(true);
			jumpComponent.getJumpSound().play();

			// applies jump only once per platform...

			return;

		}

	}

}
