package com.gemserk.games.taken;

import java.util.ArrayList;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.systems.ActivableSystem;
import com.gemserk.commons.artemis.systems.ActivableSystemImpl;
import com.gemserk.games.taken.PowerUp.Type;
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;

public class WeaponSystem extends EntityProcessingSystem implements ActivableSystem {

	private final ActivableSystem activableSystem = new ActivableSystemImpl();

	private final GameScreen gameScreen;

	private final ResourceManager<String> resourceManager;

	public WeaponSystem(GameScreen gameScreen, ResourceManager<String> resourceManager) {
		super(WeaponComponent.class);
		this.gameScreen = gameScreen;
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

		WeaponComponent weaponComponent = e.getComponent(WeaponComponent.class);

		ImmutableBag<Entity> targets = world.getGroupManager().getEntities(weaponComponent.getTargetGroup());

		if (targets == null)
			return;

		if (targets.isEmpty())
			return;

		SpatialComponent spatialComponent = e.getComponent(SpatialComponent.class);

		weaponComponent.setTime(weaponComponent.getTime() - world.getDelta());

		if (!weaponComponent.isReady())
			return;

		Vector2 position = spatialComponent.getPosition();

		float targetRange = weaponComponent.getTargetRange();

		int bulletAliveTime = 2000;

		Entity targetEntity = null;

		for (int i = 0; i < targets.size(); i++) {
			Entity target = targets.get(i);
			SpatialComponent targetSpatialComponent = target.getComponent(SpatialComponent.class);
			Vector2 targetPosition = targetSpatialComponent.getPosition();

			if (targetPosition.dst(position) < targetRange) {
				targetEntity = target;
				break;
			}
		}

		if (targetEntity == null)
			return;

		SpatialComponent targetSpatialComponent = targetEntity.getComponent(SpatialComponent.class);
		Vector2 targetPosition = targetSpatialComponent.getPosition();

		float damage = weaponComponent.getDamage();

		// and enemy is near

		Vector2 velocity = targetPosition.tmp().sub(position);
		velocity.nor();
		velocity.mul(weaponComponent.getBulletSpeed());

		Resource<Sound> laserSound;

		if (weaponComponent.getOwnerGroup().equals("Enemy")) {
			laserSound = resourceManager.get("EnemyLaserSound");
		} else {
			laserSound = resourceManager.get("FriendlyLaserSound");
		}

		laserSound.get().play();

		gameScreen.createLaser(position.x, position.y, bulletAliveTime, velocity.x, velocity.y, damage, weaponComponent.getOwnerGroup(), weaponComponent.getTargetGroup());

		PowerUpComponent powerUpComponent = e.getComponent(PowerUpComponent.class);

		int reloadTime = weaponComponent.getReloadTime();

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
