package com.gemserk.games.taken.components;

import com.artemis.Component;

public class WeaponComponent extends Component {
	
	private int reloadTime;
	
	private int time;

	private final float bulletSpeed;

	private final float targetRange;

	private final String targetGroup;

	private final float damage;

	private final Trigger trigger;
	
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
	
	public float getTargetRange() {
		return targetRange;
	}
	
	public String getTargetGroup() {
		return targetGroup;
	}
	
	public float getDamage() {
		return damage;
	}
	
	public Trigger getTrigger() {
		return trigger;
	}
	
	public WeaponComponent(int reloadTime, float bulletSpeed, float targetRange, String targetGroup, float damage, Trigger trigger) {
		this.reloadTime = reloadTime;
		this.time = reloadTime;
		this.bulletSpeed = bulletSpeed;
		this.targetRange = targetRange;
		this.targetGroup = targetGroup;
		this.damage = damage;
		this.trigger = trigger;
	}
	
}
