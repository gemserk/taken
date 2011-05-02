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

		ImmutableBag<Entity> playerEntities = world.getGroupManager().getEntities("Player");

		if (playerEntities == null)
			return;

		if (playerEntities.isEmpty())
			return;

		Entity targetEntity = playerEntities.get(0);

		EnemyWeaponComponent enemyWeaponComponent = e.getComponent(EnemyWeaponComponent.class);
		
		enemyWeaponComponent.setTime(enemyWeaponComponent.getTime() - world.getDelta());

		if (enemyWeaponComponent.isReady()) {
			
			SpatialComponent targetSpatialComponent = targetEntity.getComponent(SpatialComponent.class);
			SpatialComponent spatialComponent = e.getComponent(SpatialComponent.class);

			Vector2 targetPosition = targetSpatialComponent.getPosition();
			Vector2 position = spatialComponent.getPosition();
			
			Vector2 velocity = targetPosition.tmp().sub(position);
			velocity.nor();
			velocity.mul(enemyWeaponComponent.getBulletSpeed());
			
			Resource<Sound> laserSound = resourceManager.get("Laser");
			laserSound.get().play();
			gameScreen.createEnemyLaser(position.x, position.y, 2000, velocity.x, velocity.y);

			enemyWeaponComponent.setTime(enemyWeaponComponent.getReloadTime());
		}

	}

}
