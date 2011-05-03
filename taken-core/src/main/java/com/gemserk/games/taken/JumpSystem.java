package com.gemserk.games.taken;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gemserk.commons.artemis.systems.ActivableSystem;
import com.gemserk.commons.artemis.systems.ActivableSystemImpl;
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;

public class JumpSystem extends EntityProcessingSystem implements ActivableSystem {

	private final ActivableSystem activableSystem = new ActivableSystemImpl();

	private final Vector2 horizontalAxis = new Vector2(1f, 0f);

	private final Vector2 force = new Vector2(0, 1f);

	private final ResourceManager<String> resourceManager;

	public JumpSystem(ResourceManager<String> resourceManager) {
		super(CharacterControllerComponent.class);
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

		JumpComponent jumpComponent = e.getComponent(JumpComponent.class);

		PhysicsComponent physicsComponent = e.getComponent(PhysicsComponent.class);
		Body body = physicsComponent.getBody();

		float currentForce = jumpComponent.getCurrentForce();

		if (currentForce > 0.1f) {

			force.set(0, 1f);
			force.mul(currentForce);

			jumpComponent.setCurrentForce(currentForce * 0.9f);

			body.applyForce(force, body.getTransform().getPosition());
			
			System.out.println("applying jump!!");
			
			return;
		} 
		
		if (!jumpComponent.isJumping())
			return;

		Contact contact = physicsComponent.getContact();

		if (!contact.isInContact())
			return;

		// for cada uno de los contactos

		Vector2 normal = contact.getNormal();

		float dot = Math.abs(normal.dot(horizontalAxis));
		
		if (dot > 0.4f) 
			return;
		
		jumpComponent.setCurrentForce(jumpComponent.getJumpForce());
		jumpComponent.setJumping(true);
		
		System.out.println("jumping!!");
		
		Resource<Sound> jumpSound = resourceManager.get("Jump");
		jumpSound.get().play();

	}

}
