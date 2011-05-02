package com.gemserk.games.taken;

import com.artemis.Component;

public class PowerUp extends Component {
	
	public static enum Type {
		WeaponSpeedModifier,
		MovementSpeedModifier,
	};
	
	private Type type;
	
	private float value;

	private int time;
	
	public Type getType() {
		return type;
	}
	
	public float getValue() {
		return value;
	}
	
	public int getTime() {
		return time;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
	
	public PowerUp(Type type, float value, int time) {
		this.type = type;
		this.value = value;
		this.time = time;
	}

}
