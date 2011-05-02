package com.gemserk.games.taken;

import com.artemis.Component;
import com.gemserk.commons.artemis.entities.EntityTemplate;

public class SpawnerComponent extends Component {
	
	private final EntityTemplate entityTemplate;

	private final int spawnTime;
	
	private int time;
	
	public int getSpawnTime() {
		return spawnTime;
	}
	
	public int getTime() {
		return time;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
	
	public EntityTemplate getEntityTemplate() {
		return entityTemplate;
	}

	public SpawnerComponent(int spawnTime, EntityTemplate entityTemplate) {
		this.spawnTime = spawnTime;
		this.time = spawnTime;
		this.entityTemplate = entityTemplate;
	}

}
