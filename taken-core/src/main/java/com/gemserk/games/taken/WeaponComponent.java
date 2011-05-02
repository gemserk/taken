package com.gemserk.games.taken;

import com.artemis.Component;

public class WeaponComponent extends Component {
	
	private int reloadTime;
	
	private int time;

	private final float bulletSpeed;

	private final float targetRange;

	private final String ownerGroup;

	private final String targetGroup;
	
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
	
	public String getOwnerGroup() {
		return ownerGroup;
	}
	
	public String getTargetGroup() {
		return targetGroup;
	}
	
	public WeaponComponent(int reloadTime, float bulletSpeed, float targetRange, String ownerGroup, String targetGroup) {
		this.reloadTime = reloadTime;
		this.time = reloadTime;
		this.bulletSpeed = bulletSpeed;
		this.targetRange = targetRange;
		this.ownerGroup = ownerGroup;
		this.targetGroup = targetGroup;
	}
	
}
