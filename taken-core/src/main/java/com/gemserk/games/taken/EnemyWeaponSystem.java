package com.gemserk.games.taken;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.systems.ActivableSystem;
import com.gemserk.commons.artemis.systems.ActivableSystemImpl;
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;

public class EnemyWeaponSystem extends EntityProcessingSystem implements ActivableSystem {

	private final ActivableSystem activableSystem = new ActivableSystemImpl();

	private final GameScreen gameScreen;

	private final ResourceManager<String> resourceManager;

	public EnemyWeaponSystem(GameScreen gameScreen, ResourceManager<String> resourceManager) {
		super(EnemyWeaponComponent.class);
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

		ImmutableBag<Entity> targets = world.getGroupManager().getEntities("Player");

		if (targets == null)
			return;

		if (targets.isEmpty())
			return;

		EnemyWeaponComponent weaponComponent = e.getComponent(EnemyWeaponComponent.class);
		SpatialComponent spatialComponent = e.getComponent(SpatialComponent.class);

		weaponComponent.setTime(weaponComponent.getTime() - world.getDelta());

		if (!weaponComponent.isReady())
			return;

		Vector2 position = spatialComponent.getPosition();

		float targetRange = weaponComponent.getTargetRange();

		int bulletAliveTime = 1500;

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

		// and enemy is near

		Vector2 velocity = targetPosition.tmp().sub(position);
		velocity.nor();
		velocity.mul(weaponComponent.getBulletSpeed());

		Resource<Sound> laserSound = resourceManager.get("Laser");
		laserSound.get().play();
		gameScreen.createEnemyLaser(position.x, position.y, bulletAliveTime, velocity.x, velocity.y);

		weaponComponent.setTime(weaponComponent.getReloadTime());

	}

}
