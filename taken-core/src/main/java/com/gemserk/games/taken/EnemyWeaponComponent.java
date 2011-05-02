package com.gemserk.games.taken;

import com.artemis.Component;

public class EnemyWeaponComponent extends Component {
	
	private int reloadTime;
	
	private int time;

	private final float bulletSpeed;

	public boolean isReady() {
		return time < 0;
	}
	
	public int getReloadTime() {
		return reloadTime;
	}
	
	public int getTime() {
		return time;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
	
	public float getBulletSpeed() {
		return bulletSpeed;
	}
	
	public EnemyWeaponComponent(int reloadTime, float bulletSpeed) {
		this.reloadTime = reloadTime;
		this.time = reloadTime;
		this.bulletSpeed = bulletSpeed;
	}
	
}
