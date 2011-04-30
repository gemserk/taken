package com.gemserk.games.taken;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
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

		PhysicsComponent physicsComponent = e.getComponent(PhysicsComponent.class);
		Body body = physicsComponent.getBody();

		if (characterController.isWalking()) {

			characterController.getWalkingDirection(direction);

			force.set(direction[0], direction[1]);
			force.mul(10f);

			body.applyForce(force, body.getTransform().getPosition());

			Vector2 linearVelocity = body.getLinearVelocity();

			float speed = Math.abs(linearVelocity.x);
			float maxSpeed = 1f;

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

		}

		if (characterController.jumped()) {

			Vector2 linearVelocity = body.getLinearVelocity();

			// it should be checking if it is over an object to jump...
			
			if (Math.abs(linearVelocity.y) < 0.05f) {

				impulse.set(0f, 1f);
				// impulse.mul(10f);

				body.applyLinearImpulse(impulse, body.getTransform().getPosition());
			}

		}

	}

}
