package com.gemserk.games.taken;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.systems.ActivableSystem;
import com.gemserk.commons.artemis.systems.ActivableSystemImpl;

public class EnemySpawnerSystem extends EntityProcessingSystem implements ActivableSystem {

	private final ActivableSystem activableSystem = new ActivableSystemImpl();
	
	private final GameScreen gameScreen;

	public EnemySpawnerSystem(GameScreen gameScreen) {
		super(SpawnerComponent.class);
		this.gameScreen = gameScreen;
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
		
		SpawnerComponent spawnerComponent = e.getComponent(SpawnerComponent.class);
		
		spawnerComponent.setTime(spawnerComponent.getTime() - world.getDelta());
		
		if (spawnerComponent.getTime() > 0)
			return;
		
		ImmutableBag<Entity> playerEntities = world.getGroupManager().getEntities("Player");
		
		if (playerEntities == null)
			return;
		
		if (playerEntities.isEmpty())
			return;
		
		Entity targetEntity = playerEntities.get(0);

		SpatialComponent spatialComponent = targetEntity.getComponent(SpatialComponent.class);
		Vector2 position = spatialComponent.getPosition();
		
		float x = position.x + MathUtils.random(-5, 5);
		float y = position.y + MathUtils.random(-5, 5);

		gameScreen.createEnemy(x, y);
		
		spawnerComponent.setTime(spawnerComponent.getSpawnTime());

	}

}
