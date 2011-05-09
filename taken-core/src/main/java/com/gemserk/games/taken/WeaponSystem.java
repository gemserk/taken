package com.gemserk.games.taken;

import java.util.ArrayList;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.systems.ActivableSystem;
import com.gemserk.commons.artemis.systems.ActivableSystemImpl;
import com.gemserk.games.taken.PowerUp.Type;
import com.gemserk.games.taken.components.PowerUpComponent;
import com.gemserk.games.taken.components.TargetComponent;
import com.gemserk.games.taken.components.WeaponComponent;

public class WeaponSystem extends EntityProcessingSystem implements ActivableSystem {

	private final ActivableSystem activableSystem = new ActivableSystemImpl();

	public WeaponSystem() {
		super(WeaponComponent.class);
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

		WeaponComponent weaponComponent = e.getComponent(WeaponComponent.class);
		weaponComponent.setTime(weaponComponent.getTime() - world.getDelta());

		if (!weaponComponent.isReady())
			return;
		
		TargetComponent targetComponent = e.getComponent(TargetComponent.class);
		
		ImmutableBag<Entity> targets = world.getGroupManager().getEntities(weaponComponent.getTargetGroup());
		targetComponent.setTarget(null);

		if (targets == null)
			return;

		if (targets.isEmpty())
			return;

		SpatialComponent spatialComponent = e.getComponent(SpatialComponent.class);

		Vector2 position = spatialComponent.getPosition();

		float targetRange = weaponComponent.getTargetRange();

		for (int i = 0; i < targets.size(); i++) {
			Entity target = targets.get(i);
			SpatialComponent targetSpatialComponent = target.getComponent(SpatialComponent.class);
			Vector2 targetPosition = targetSpatialComponent.getPosition();

			if (targetPosition.dst(position) < targetRange) {
				targetComponent.setTarget(target);
//				targetEntity = target;
				break;
			}
		}

		if (targetComponent.getTarget() == null)
			return;
		
		// and enemy is near

		weaponComponent.getEntityTemplate().fire(e, targetComponent.getTarget());

		int reloadTime = weaponComponent.getReloadTime();

		PowerUpComponent powerUpComponent = e.getComponent(PowerUpComponent.class);

		if (powerUpComponent != null) {

			ArrayList<PowerUp> powerUps = powerUpComponent.getPowerUps();
			for (int i = 0; i < powerUps.size(); i++) {
				PowerUp powerUp = powerUps.get(i);
				if (powerUp.getType() == Type.WeaponSpeedModifier) {
					reloadTime = (int) ((float) reloadTime / powerUp.getValue());
					Gdx.app.log("Taken", "Applying weapon speed modifier.");
				}
			}

		}

		weaponComponent.setTime(reloadTime);

	}

}
