package com.gemserk.games.taken;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.systems.ActivableSystem;
import com.gemserk.commons.artemis.systems.ActivableSystemImpl;

public class CharacterControllerSystem extends EntityProcessingSystem implements ActivableSystem {

	private final ActivableSystem activableSystem = new ActivableSystemImpl();

	private final float[] direction = new float[] { 0f, 0f };

	private final Vector2 force = new Vector2();

	private final Vector2 impulse = new Vector2();

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

		CharacterControllerComponent component = e.getComponent(CharacterControllerComponent.class);
		CharacterController characterController = component.getCharacterController();

		AnimationComponent animationComponent = e.getComponent(AnimationComponent.class);

		PhysicsComponent physicsComponent = e.getComponent(PhysicsComponent.class);
		Body body = physicsComponent.getBody();

		Contact contact = physicsComponent.getContact();

		SpatialComponent spatialComponent = e.getComponent(SpatialComponent.class);

		if (characterController.shouldSwitchSize()) {

			Vector2 size = spatialComponent.getSize();

			if (size.x == 1f) {
				Synchronizers.transition(size, Transitions.transitionBuilder(size).time(500).end(new Vector2(5f, 5f)).build());
			} else if (size.x == 5f) {
				Synchronizers.transition(size, Transitions.transitionBuilder(size).time(500).end(new Vector2(1f, 1f)).build());
			}

		}

		if (characterController.isWalking()) {

			animationComponent.setCurrentAnimation(0);

			characterController.getWalkingDirection(direction);

			force.set(direction[0], direction[1]);
			force.mul(10f);

			body.applyForce(force, body.getTransform().getPosition());

			Vector2 linearVelocity = body.getLinearVelocity();

			float speed = Math.abs(linearVelocity.x);
			float maxSpeed = 2f;

			if (speed > maxSpeed) {
				float factor = maxSpeed / speed;
				linearVelocity.x *= factor;
				body.setLinearVelocity(linearVelocity);
			}

			SpriteComponent spriteComponent = e.getComponent(SpriteComponent.class);
			Sprite sprite = spriteComponent.getSprite();

			if (linearVelocity.x < 0f) {
				sprite.setScale(-1f, 1f);
			} else {
				sprite.setScale(1f, 1f);
			}

		} else {

			if (contact.isInContact()) {
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
		
		Vector2 normal = contact.getNormal();
		
		Vector2 horizontal = new Vector2(1f,0f);
		
		float dot = Math.abs(normal.dot(horizontal));
		
		if (characterController.jumped() && contact.isInContact() && dot < 0.4f) {
			// it should be checking if it is over an object to jump...

			// if (Math.abs(linearVelocity.y) < 0.05f) {

			impulse.set(0f, 1f);
			// impulse.mul(10f);

			body.applyLinearImpulse(impulse, body.getTransform().getPosition());
			// }

		}

	}

}
