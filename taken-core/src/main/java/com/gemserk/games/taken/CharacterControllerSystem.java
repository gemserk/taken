package com.gemserk.games.taken;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gemserk.commons.artemis.systems.ActivableSystem;
import com.gemserk.commons.artemis.systems.ActivableSystemImpl;
import com.gemserk.games.taken.components.AnimationComponent;
import com.gemserk.games.taken.components.CharacterControllerComponent;
import com.gemserk.games.taken.components.JumpComponent;
import com.gemserk.games.taken.components.PhysicsComponent;
import com.gemserk.games.taken.controllers.MovementController;
import com.gemserk.games.taken.controllers.JumpController;

public class CharacterControllerSystem extends EntityProcessingSystem implements ActivableSystem {

	private final ActivableSystem activableSystem = new ActivableSystemImpl();

	private final float[] direction = new float[] { 0f, 0f };

	private final Vector2 force = new Vector2();

	public CharacterControllerSystem() {
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

		CharacterControllerComponent characterControllerComponent = e.getComponent(CharacterControllerComponent.class);
		MovementController movementController = characterControllerComponent.getCharacterController();

		AnimationComponent animationComponent = e.getComponent(AnimationComponent.class);

		PhysicsComponent physicsComponent = e.getComponent(PhysicsComponent.class);
		Body body = physicsComponent.getBody();

		Contact contact = physicsComponent.getContact();

		if (movementController.isWalking()) {

			animationComponent.setCurrentAnimation(0);

			movementController.getWalkingDirection(direction);

			force.set(direction[0], direction[1]);
			force.mul(10f);

			if (contact.isInContact()) {
				Vector2 normal = contact.getNormal();
				float dot = normal.dot(force.tmp().nor());
				if (dot > 0.5) 
					force.set(0,0);
			}

			body.applyForce(force, body.getTransform().getPosition());

			Vector2 linearVelocity = body.getLinearVelocity();

			float speed = Math.abs(linearVelocity.x);
			float maxSpeed = 2f;

			if (speed > maxSpeed) {
				float factor = maxSpeed / speed;
				linearVelocity.x *= factor;
				body.setLinearVelocity(linearVelocity);
			}
			
		} else {

			if (contact.isInContact()) {

				// if normal perpendicular al piso me detengo, sino dejo al motor de física

				animationComponent.setCurrentAnimation(1);

				Vector2 linearVelocity = body.getLinearVelocity();
				linearVelocity.x = 0f;
				body.setLinearVelocity(linearVelocity);
			}

		}

		Vector2 linearVelocity = body.getLinearVelocity();

		if (linearVelocity.y > 0.05f && !contact.isInContact()) {
			animationComponent.setCurrentAnimation(2);
		}

		if (linearVelocity.y < -0.05f && !contact.isInContact()) {
			animationComponent.setCurrentAnimation(3);
		}

		// in contact with the ground!!... ?

		JumpController jumpController = characterControllerComponent.getJumpController();

		JumpComponent jumpComponent = e.getComponent(JumpComponent.class);
		jumpComponent.setJumping(jumpController.isJumping());

	}

}
