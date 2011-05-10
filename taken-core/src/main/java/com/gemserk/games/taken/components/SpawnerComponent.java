package com.gemserk.games.taken.components;

import com.artemis.Component;
import com.gemserk.commons.artemis.triggers.Trigger;

public class SpawnerComponent extends Component {
	
	private final Trigger trigger;

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
	
	public Trigger getTrigger() {
		return trigger;
	}

	public SpawnerComponent(int spawnTime, Trigger trigger) {
		this.spawnTime = spawnTime;
		this.time = spawnTime;
		this.trigger = trigger;
	}

}
