package com.gemserk.games.taken;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.artemis.components.MovementComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.systems.ActivableSystem;
import com.gemserk.commons.artemis.systems.ActivableSystemImpl;

public class FollowCharacterBehaviorSystem extends EntityProcessingSystem implements ActivableSystem {

	private final ActivableSystem activableSystem = new ActivableSystemImpl();

	public FollowCharacterBehaviorSystem() {
		super(FollowCharacterComponent.class);
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
		
		ImmutableBag<Entity> playerEntities = world.getGroupManager().getEntities("Player");
		
		if (playerEntities == null)
			return;
		
		if (playerEntities.isEmpty())
			return;
		
		Entity targetEntity = playerEntities.get(0);

		SpatialComponent targetSpatialComponent = targetEntity.getComponent(SpatialComponent.class);
		
		FollowCharacterComponent followCharacterComponent = e.getComponent(FollowCharacterComponent.class);
		MovementComponent movementComponent = e.getComponent(MovementComponent.class);
		SpatialComponent spatialComponent = e.getComponent(SpatialComponent.class);
		
		Vector2 position = spatialComponent.getPosition();
		Vector2 targetPosition = followCharacterComponent.getTargetPosition();
		
		if (targetPosition.dst(position) < 1f) {
			
			Vector2 targetEntityPosition = targetSpatialComponent.getPosition();
			
			// near the character ?
			targetPosition.set(targetEntityPosition.x + MathUtils.random(-2, 2), targetEntityPosition.y + MathUtils.random(-2, 2));
			Vector2 direction = targetPosition.tmp().sub(position);
			direction.nor();
			
			direction.mul(2f);
			
			movementComponent.getVelocity().set(direction);
			
		}
		
		

	}

}