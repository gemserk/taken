package com.gemserk.games.taken;

import com.artemis.Component;

public class SpawnerComponent extends Component {
	
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

	public SpawnerComponent(int spawnTime) {
		this.spawnTime = spawnTime;
		this.time = spawnTime;
	}

}
