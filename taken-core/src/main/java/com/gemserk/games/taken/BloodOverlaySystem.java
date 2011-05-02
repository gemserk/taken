package com.gemserk.games.taken;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.systems.ActivableSystem;
import com.gemserk.commons.artemis.systems.ActivableSystemImpl;
import com.gemserk.componentsengine.utils.Container;

public class BloodOverlaySystem extends EntityProcessingSystem implements ActivableSystem {

	private final ActivableSystem activableSystem = new ActivableSystemImpl();

	public BloodOverlaySystem() {
		super(BloodOverlayComponent.class);
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

		BloodOverlayComponent bloodOverlayComponent = e.getComponent(BloodOverlayComponent.class);
		SpatialComponent spatialComponent = e.getComponent(SpatialComponent.class);

		Entity character = bloodOverlayComponent.getEntity();
		
		SpatialComponent characterSpatialComponent = character.getComponent(SpatialComponent.class);
		
		// character probably dead
		if (characterSpatialComponent == null)
			return;
		
		spatialComponent.setPosition(characterSpatialComponent.getPosition());
		spatialComponent.setAngle(characterSpatialComponent.getAngle());

		SpriteComponent bloodSpriteComponent = e.getComponent(SpriteComponent.class);

		// sprite sheet depends on movement and health..
		PhysicsComponent characterPhysicsComponent = character.getComponent(PhysicsComponent.class);
		
		if (characterPhysicsComponent == null)
			return;
		
		Vector2 linearVelocity = characterPhysicsComponent.getBody().getLinearVelocity();

		// idle animation?
		if (linearVelocity.len() < 0.1f) {
			// front 
			bloodOverlayComponent.setCurrentAnimation(0);
		} else {
			// side 
			bloodOverlayComponent.setCurrentAnimation(1);
			
			if (linearVelocity.x < 0f) {
				bloodSpriteComponent.getSprite().setScale(-1f, 1f);
			} else {
				bloodSpriteComponent.getSprite().setScale(1f, 1f);
			}
			
		}
		
		HealthComponent healthComponent = character.getComponent(HealthComponent.class);
		Container health = healthComponent.getHealth();
		
		float percentage = health.getPercentage();
		
		int frame;
		
		if (percentage > 0.7f)
			frame = 0;
		else if (percentage > 0.4f)
			frame = 1;
		else 
			frame = 2;

		// depends on the health 
		bloodSpriteComponent.setSprite(bloodOverlayComponent.getSpriteSheets().getFrame(frame));

	}

}
